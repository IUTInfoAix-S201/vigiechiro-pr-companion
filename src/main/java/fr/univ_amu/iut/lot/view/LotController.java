package fr.univ_amu.iut.lot.view;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.view.EmplacementNavigation;
import fr.univ_amu.iut.commun.view.EmplacementPassage;
import fr.univ_amu.iut.commun.view.Lieu;
import fr.univ_amu.iut.commun.view.OuvreurDeLien;
import fr.univ_amu.iut.commun.view.OuvrirPassage;
import fr.univ_amu.iut.commun.view.OuvrirSite;
import fr.univ_amu.iut.commun.viewmodel.ContextePassage;
import fr.univ_amu.iut.lot.viewmodel.EtapeDepot;
import fr.univ_amu.iut.lot.viewmodel.LotViewModel;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/// Controller de l'écran **M-Lot** (`Lot.fxml`).
///
/// Pur câblage (patron CM4) : lie le statut, le récapitulatif, le dossier à téléverser, les alertes
/// de cohérence (R14) et les deux actions du dépôt au [LotViewModel]. « Préparer le lot » et
/// « Marquer déposé » ne sont actifs que dans l'état workflow adéquat ; la zone d'alertes
/// n'apparaît qu'en présence d'alertes bloquantes. Aucun accès base de données ni logique métier ici
/// (règle ArchUnit `view_sans_jdbc`).
public class LotController implements EmplacementNavigation {

    private final LotViewModel viewModel;
    private final OuvrirSite ouvrirSite;
    private final OuvrirPassage ouvrirPassage;

    /// Ouvre le sous-dossier `depot/` dans le gestionnaire de fichiers du système (#251) : le dépôt étant
    /// manuel, on amène l'observateur au bon endroit. Abstrait pour rester testable (faux en tête).
    private final OuvreurDeLien ouvreurDeLien;

    /// Contexte de navigation (passage + site), mémorisé pour reconstruire le fil d'Ariane du chrome.
    private ContextePassage contexte;

    @FXML
    private Label lblStatut;

    @FXML
    private Label lblRecap;

    @FXML
    private HBox stepper;

    @FXML
    private Label lblCheminDepot;

    @FXML
    private VBox zoneAlertes;

    @FXML
    private ListView<String> listeAlertes;

    @FXML
    private Button btnPreparer;

    @FXML
    private Button btnDeposer;

    @FXML
    private Label lblTitreArchives;

    @FXML
    private Button btnGenererArchives;

    @FXML
    private ProgressIndicator indicateurGeneration;

    @FXML
    private ListView<String> listeArchives;

    @FXML
    private Button btnOuvrirDepot;

    @FXML
    private Label lblMessage;

    @Inject
    public LotController(
            LotViewModel viewModel, OuvrirSite ouvrirSite, OuvrirPassage ouvrirPassage, OuvreurDeLien ouvreurDeLien) {
        this.viewModel = Objects.requireNonNull(viewModel, "viewModel");
        this.ouvrirSite = Objects.requireNonNull(ouvrirSite, "ouvrirSite");
        this.ouvrirPassage = Objects.requireNonNull(ouvrirPassage, "ouvrirPassage");
        this.ouvreurDeLien = Objects.requireNonNull(ouvreurDeLien, "ouvreurDeLien");
    }

    @FXML
    private void initialize() {
        lblStatut.textProperty().bind(viewModel.statutProperty());
        lblRecap.textProperty().bind(viewModel.recapProperty());
        // Étape ③ : la cible du téléversement est le sous-dossier depot/ (archives ZIP), pas la session.
        lblCheminDepot.textProperty().bind(viewModel.cheminDepotProperty());

        // Stepper du dépôt (#251), reconstruit à chaque changement d'étapes (mêmes styles que M-Passage).
        viewModel.etapes().addListener((ListChangeListener<EtapeDepot>) changement -> majStepper());
        majStepper();

        listeAlertes.setItems(viewModel.alertes());
        // La zone d'alertes n'a de sens qu'en présence d'alertes bloquantes (R14).
        BooleanBinding alertesPresentes = Bindings.isNotEmpty(viewModel.alertes());
        zoneAlertes.visibleProperty().bind(alertesPresentes);
        zoneAlertes.managedProperty().bind(alertesPresentes);

        btnPreparer.disableProperty().bind(viewModel.peutPreparerProperty().not());
        // « Marquer déposé » : pas pendant une génération en cours, sinon on marquerait le passage déposé
        // avant la fin de l'écriture des archives (#259).
        btnDeposer
                .disableProperty()
                .bind(viewModel.peutDeposerProperty().not().or(viewModel.generationEnCoursProperty()));

        // Archives de dépôt (#110) : titre = plafond configuré ; bouton actif une fois le lot préparé et
        // hors génération en cours ; la liste reflète les ZIP produits.
        lblTitreArchives.textProperty().bind(viewModel.titreArchivesProperty());
        btnGenererArchives
                .disableProperty()
                .bind(viewModel.peutGenererArchivesProperty().not().or(viewModel.generationEnCoursProperty()));
        // Indicateur d'activité (#251) : visible uniquement pendant la génération hors-thread.
        indicateurGeneration.visibleProperty().bind(viewModel.generationEnCoursProperty());
        indicateurGeneration.managedProperty().bind(viewModel.generationEnCoursProperty());
        listeArchives.setItems(viewModel.archives());

        // Étape ③ : « Ouvrir le dossier » seulement quand les archives sont réellement prêtes (#259), pas
        // dès qu'un chemin existe : les ZIP sont écrits sous leur nom final pendant la génération, ouvrir
        // (ou téléverser) avant la fin exposerait un fichier partiel. Donc activé après une génération
        // réussie (liste non vide) et hors génération en cours.
        btnOuvrirDepot
                .disableProperty()
                .bind(Bindings.isEmpty(viewModel.archives()).or(viewModel.generationEnCoursProperty()));

        lblMessage.textProperty().bind(viewModel.messageProperty());
        var messagePresent = viewModel.messageProperty().isNotEmpty();
        lblMessage.visibleProperty().bind(messagePresent);
        lblMessage.managedProperty().bind(messagePresent);
    }

    /// Reconstruit le stepper du dépôt (#251) depuis [LotViewModel#etapes()] : une puce par étape,
    /// stylée selon son état (franchie / courante / à venir), comme le stepper de M-Passage.
    private void majStepper() {
        stepper.getChildren().clear();
        for (EtapeDepot etape : viewModel.etapes()) {
            Label puce = new Label(etape.libelle());
            puce.getStyleClass().addAll("etape", "etape-" + etape.etat().name().toLowerCase(Locale.ROOT));
            stepper.getChildren().add(puce);
        }
    }

    /// Ouvre l'écran sur le passage `passage`. Appelée par [NavigationLot] après le chargement FXML ;
    /// mémorise le contexte pour le fil d'Ariane.
    public void ouvrirSur(ContextePassage passage) {
        this.contexte = passage;
        viewModel.ouvrirSur(passage.idPassage());
    }

    /// Emplacement dans le fil d'Ariane : `Mes sites › Carré N › Détails du passage N° X › Préparer le
    /// dépôt` (rendu par le chrome). Le segment passage rouvre M-Passage.
    @Override
    public List<Lieu> emplacement() {
        return EmplacementPassage.emplacementEnfant(contexte, ouvrirSite, ouvrirPassage, "Préparer le dépôt");
    }

    @FXML
    private void preparer() {
        viewModel.preparer();
    }

    @FXML
    private void deposer() {
        viewModel.deposer();
    }

    /// Lance la génération des archives **hors fil JavaFX** (#251) : l'opération peut être longue sur une
    /// grosse nuit, on ne fige pas l'IHM. L'état « en cours » est posé sur le fil JavaFX, le calcul tourne
    /// sur un fil virtuel, puis le résultat (succès ou échec) est appliqué via `Platform.runLater`.
    @FXML
    private void genererArchives() {
        viewModel.marquerGenerationEnCours();
        Thread.ofVirtual().name("archives-depot-vigiechiro").start(() -> {
            try {
                var produites = viewModel.calculerArchivesDepot();
                Platform.runLater(() -> viewModel.appliquerGeneration(produites));
            } catch (RuntimeException echec) {
                Platform.runLater(() -> viewModel.echecGeneration(echec.getMessage()));
            }
        });
    }

    /// Ouvre le sous-dossier `depot/` dans le gestionnaire de fichiers du système (#251), pour aider au
    /// téléversement manuel (étape ③). Sans chemin, le bouton est désactivé ; l'ouverture ne lève jamais.
    @FXML
    private void ouvrirDossierDepot() {
        String chemin = viewModel.cheminDepotProperty().get();
        if (chemin != null && !chemin.isBlank()) {
            ouvreurDeLien.ouvrir(Path.of(chemin).toUri().toString());
        }
    }
}

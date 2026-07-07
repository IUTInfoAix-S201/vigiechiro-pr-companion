package fr.univ_amu.iut.multisite.viewmodel;

import fr.univ_amu.iut.commun.model.StatutWorkflow;
import fr.univ_amu.iut.commun.model.Verdict;
import fr.univ_amu.iut.commun.viewmodel.Filtres;
import fr.univ_amu.iut.multisite.model.CarreAgrege;
import fr.univ_amu.iut.multisite.model.FiltresMultisite;
import fr.univ_amu.iut.multisite.model.LignePassage;
import fr.univ_amu.iut.multisite.model.SavedView;
import fr.univ_amu.iut.multisite.model.ServiceMultisite;
import fr.univ_amu.iut.multisite.model.TriMultisite;
import fr.univ_amu.iut.sites.model.ServiceSites;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

/// ViewModel de l'écran **M-Multisite** (vue agrégée des passages de tous les sites de
/// l'utilisateur, parcours P5, story E5, statut **SHOULD**).
///
/// Expose le tableau des [lignes][LignePassage], les critères de **filtre** (numéro de carré,
/// statut, verdict, année) et de **tri** ([TriMultisite]), et l'**export CSV**.
///
/// **Filtrage côté client (#537).** Les passages sont chargés **une seule fois**
/// ([#rafraichir()]) puis filtrés **en mémoire** via le socle partagé [Filtres] : chaque
/// changement de filtre recompose la **conjonction** de prédicats sur une [FilteredList], **sans
/// ré-interroger le service** (le multisite chargeait auparavant tous les passages à chaque
/// changement de critère). Le **tri nommé** ré-ordonne la liste publiée. Les prédicats
/// **réutilisent** [FiltresMultisite#accepte(LignePassage)] : même sémantique que les vues
/// sauvegardées et que la vue « saison » du service, aucune logique de filtrage dupliquée.
///
/// Gère aussi les **vues sauvegardées** ([SavedView]) : enregistrer la combinaison de filtres
/// courante sous un nom, lister, appliquer (rejouer ses filtres), mettre à jour ou supprimer. Une
/// vue reste sérialisée en [FiltresMultisite] (`saved_view.filters_json`) ; l'appliquer repose les
/// quatre propriétés de filtre, que le socle traduit en prédicats.
///
/// VM agnostique de l'IHM (règle ArchUnit `viewmodel_sans_javafx_ui`) : seuls
/// `javafx.beans`/`javafx.collections`. Non-singleton (un VM frais par chargement de vue).
public class MultisiteViewModel {

    /// Clés des filtres composables (une par critère) dans le socle [Filtres].
    private static final String CLE_CARRE = "carre";

    private static final String CLE_STATUT = "statut";
    private static final String CLE_VERDICT = "verdict";
    private static final String CLE_ANNEE = "annee";

    private final ServiceMultisite service;
    private final String idUtilisateur;

    /// File des déplacements de points en attente (mode édition des positions, #154). Responsabilité
    /// extraite : le ViewModel l'expose, la vue la pilote.
    private final PositionsEnAttente positionsEnAttente;

    private final StringProperty filtreNumeroCarre = new SimpleStringProperty(this, "filtreNumeroCarre", "");
    private final ObjectProperty<StatutWorkflow> filtreStatut = new SimpleObjectProperty<>(this, "filtreStatut");
    private final ObjectProperty<Verdict> filtreVerdict = new SimpleObjectProperty<>(this, "filtreVerdict");
    private final ObjectProperty<Integer> filtreAnnee = new SimpleObjectProperty<>(this, "filtreAnnee");
    private final ObjectProperty<TriMultisite> tri = new SimpleObjectProperty<>(this, "tri", TriMultisite.PAR_SITE);

    /// Tous les passages de l'utilisateur, chargés une fois ([#rafraichir()]). Source **non filtrée**
    /// du socle : les filtres et le tri travaillent dessus en mémoire, sans ré-interroger le service.
    private final ObservableList<LignePassage> tousLesPassages = FXCollections.observableArrayList();

    private final FilteredList<LignePassage> passagesFiltres = new FilteredList<>(tousLesPassages);

    /// Lignes **publiées** vers la vue : sous-ensemble filtré, ré-ordonné par le tri nommé. La vue y
    /// pose par-dessus un [javafx.collections.transformation.SortedList] pour le tri par clic
    /// d'en-tête (#145) ; cette liste reste donc la même instance au fil des rafraîchissements.
    private final ObservableList<LignePassage> lignes = FXCollections.observableArrayList();

    private final ObservableList<SavedView> vues = FXCollections.observableArrayList();
    /// Agrégat des carrés pour la carte (#152) : vue d'ensemble **non filtrée** (carrés + points + statut).
    private final ObservableList<CarreAgrege> carresCarte = FXCollections.observableArrayList();
    private final ReadOnlyBooleanWrapper nonVide = new ReadOnlyBooleanWrapper(this, "nonVide", false);
    private final ReadOnlyStringWrapper resume = new ReadOnlyStringWrapper(this, "resume", "");
    private final ReadOnlyStringWrapper message = new ReadOnlyStringWrapper(this, "message", "");

    /// Socle de filtres composables (#537) : recompose la conjonction sur [#passagesFiltres] puis
    /// publie via [#publierLignes()]. Déclaré après ses dépendances (la liste filtrée).
    private final Filtres<LignePassage> filtres = new Filtres<>(passagesFiltres, this::publierLignes);

    public MultisiteViewModel(ServiceMultisite service, ServiceSites serviceSites, String idUtilisateur) {
        this.service = Objects.requireNonNull(service, "service");
        this.idUtilisateur = Objects.requireNonNull(idUtilisateur, "idUtilisateur");
        this.positionsEnAttente = new PositionsEnAttente(serviceSites, this::rafraichirCarte, message::set);
        // Chaque critère branche/retire son prédicat dans le socle (filtrage en mémoire, sans
        // ré-interroger le service). Le tri nommé ne re-filtre pas : il ré-ordonne la liste publiée.
        filtreNumeroCarre.addListener(
                (obs, ancien, nouveau) -> filtres.definir(CLE_CARRE, CriteresMultisite.parCarre(nouveau)));
        filtreStatut.addListener(
                (obs, ancien, nouveau) -> filtres.definir(CLE_STATUT, CriteresMultisite.parStatut(nouveau)));
        filtreVerdict.addListener(
                (obs, ancien, nouveau) -> filtres.definir(CLE_VERDICT, CriteresMultisite.parVerdict(nouveau)));
        filtreAnnee.addListener(
                (obs, ancien, nouveau) -> filtres.definir(CLE_ANNEE, CriteresMultisite.parAnnee(nouveau)));
        tri.addListener((obs, ancien, nouveau) -> publierLignes());
    }

    /// (Re)charge **tous** les passages de l'utilisateur, puis ré-applique filtres et tri courants.
    /// À appeler à l'ouverture de l'écran et après une modification des données (retour d'un passage
    /// édité). Les changements de filtre ou de tri **ne rechargent pas** : ils re-filtrent /
    /// ré-ordonnent en mémoire.
    public void rafraichir() {
        tousLesPassages.setAll(service.listerPassages(idUtilisateur));
        filtres.appliquer();
    }

    /// Callback du socle (`apresApplication`) : ré-ordonne le sous-ensemble filtré selon le tri
    /// nommé, le publie dans [#lignes], et met à jour le résumé et l'indice d'état vide.
    private void publierLignes() {
        List<LignePassage> triees = new ArrayList<>(passagesFiltres);
        triees.sort(tri.get().comparateur());
        lignes.setAll(triees);
        nonVide.set(!lignes.isEmpty());
        resume.set(lignes.size() + " passage(s) affiché(s).");
        message.set("");
    }

    /// (Re)charge l'agrégat des carrés pour la **carte** (#152), vue d'ensemble **non filtrée**.
    /// **Séparé** de [#rafraichir()] : la carte ne dépend ni des filtres ni du tri du tableau, donc on ne
    /// la recalcule pas à chaque changement de filtre/tri (coût inutile), mais seulement aux moments où les
    /// données changent (ouverture de l'écran, retour après modification d'un passage), à la charge de la
    /// vue (controller).
    public void rafraichirCarte() {
        carresCarte.setAll(service.agregerPourCarte(idUtilisateur));
    }

    /// File des déplacements de points **en attente** (mode édition de la carte, #154) : la vue y met les
    /// marqueurs glissés, puis enregistre ou abandonne. Voir [PositionsEnAttente].
    public PositionsEnAttente positionsEnAttente() {
        return positionsEnAttente;
    }

    private FiltresMultisite filtresCourants() {
        return new FiltresMultisite(
                CriteresMultisite.texteOuNull(filtreNumeroCarre.get()),
                filtreStatut.get(),
                filtreVerdict.get(),
                filtreAnnee.get());
    }

    /// Applique un jeu de filtres d'un seul tenant (réinitialisation, vue sauvegardée) : repose les
    /// quatre propriétés, que les listeners traduisent en prédicats du socle (re-filtrage en mémoire).
    private void appliquerFiltres(FiltresMultisite filtresAAppliquer) {
        filtreNumeroCarre.set(filtresAAppliquer.numeroCarre() == null ? "" : filtresAAppliquer.numeroCarre());
        filtreStatut.set(filtresAAppliquer.statut());
        filtreVerdict.set(filtresAAppliquer.verdict());
        filtreAnnee.set(filtresAAppliquer.annee());
    }

    /// Réinitialise tous les filtres (le tri est conservé). Le socle re-filtre en mémoire.
    public void reinitialiserFiltres() {
        appliquerFiltres(FiltresMultisite.aucun());
    }

    /// Exporte les lignes **internes** du tableau (sous-ensemble filtré, tri nommé) en CSV vers
    /// `destination`. La vue préfère [#exporter(Path, List)] pour exporter l'ordre **affiché** (tri par
    /// clic d'en-tête inclus).
    public boolean exporter(Path destination) {
        return exporter(destination, lignes);
    }

    /// Exporte les **lignes fournies** en CSV vers `destination` (P5-CA5). Permet à la vue d'exporter
    /// l'ordre **réellement affiché** (le tri par clic d'en-tête vit côté `TableView`, pas dans le
    /// ViewModel, cf. #291). Sans dossier, l'appel est ignoré ; le bilan (ou l'erreur) va dans
    /// [#messageProperty()].
    ///
    /// @param destination fichier cible choisi par l'observateur
    /// @param lignesAExporter lignes à écrire, dans l'ordre voulu
    /// @return `true` si le fichier a été écrit
    public boolean exporter(Path destination, List<LignePassage> lignesAExporter) {
        if (destination == null) {
            return false;
        }
        try {
            service.exporterCsvVers(destination, lignesAExporter);
            message.set("Tableau exporté vers " + destination.getFileName() + " (" + lignesAExporter.size()
                    + " ligne(s)).");
            return true;
        } catch (RuntimeException echec) {
            message.set(echec.getMessage());
            return false;
        }
    }

    // --- Vues sauvegardées (story E5.S3) ---

    /// Recharge la liste des vues sauvegardées (à appeler à l'ouverture de la modale de gestion).
    public void chargerVues() {
        vues.setAll(service.listerVues());
    }

    /// Enregistre la combinaison de filtres courante sous `nom`. Un nom vide est refusé.
    ///
    /// @return `true` si la vue a été enregistrée
    public boolean enregistrerVue(String nom) {
        if (nom == null || nom.isBlank()) {
            message.set("Donnez un nom à la vue avant de l'enregistrer.");
            return false;
        }
        try {
            service.enregistrerVue(nom.trim(), filtresCourants());
            chargerVues();
            message.set("Vue « " + nom.trim() + " » enregistrée.");
            return true;
        } catch (RuntimeException echec) {
            message.set(echec.getMessage());
            return false;
        }
    }

    /// Applique les filtres d'une vue sauvegardée (rejoue la combinaison). Le socle re-filtre en
    /// mémoire.
    ///
    /// @return `true` si la vue a été appliquée
    public boolean appliquerVue(SavedView vue) {
        if (vue == null) {
            return false;
        }
        try {
            appliquerFiltres(service.chargerVue(vue.id()));
            return true;
        } catch (RuntimeException echec) {
            message.set(echec.getMessage());
            return false;
        }
    }

    /// Met à jour une vue existante : son nom et la combinaison de filtres courante.
    ///
    /// @return `true` si la vue a été mise à jour
    public boolean mettreAJourVue(SavedView vue, String nom) {
        if (vue == null || nom == null || nom.isBlank()) {
            return false;
        }
        try {
            service.mettreAJourVue(vue.id(), nom.trim(), filtresCourants());
            chargerVues();
            message.set("Vue « " + nom.trim() + " » mise à jour.");
            return true;
        } catch (RuntimeException echec) {
            message.set(echec.getMessage());
            return false;
        }
    }

    /// Supprime une vue sauvegardée.
    ///
    /// @return `true` si la vue a été supprimée
    public boolean supprimerVue(SavedView vue) {
        if (vue == null) {
            return false;
        }
        try {
            service.supprimerVue(vue.id());
            chargerVues();
            message.set("Vue supprimée.");
            return true;
        } catch (RuntimeException echec) {
            message.set(echec.getMessage());
            return false;
        }
    }

    public ObservableList<LignePassage> lignes() {
        return lignes;
    }

    /// Agrégat des carrés pour la **carte** (#152) : carrés + points (GPS, statut dominant) de l'utilisateur,
    /// vue d'ensemble non filtrée. La couche `view` le traduit en marqueurs/emprises.
    public ObservableList<CarreAgrege> carresCarte() {
        return carresCarte;
    }

    public ObservableList<SavedView> vues() {
        return vues;
    }

    public StringProperty filtreNumeroCarreProperty() {
        return filtreNumeroCarre;
    }

    public ObjectProperty<StatutWorkflow> filtreStatutProperty() {
        return filtreStatut;
    }

    public ObjectProperty<Verdict> filtreVerdictProperty() {
        return filtreVerdict;
    }

    public ObjectProperty<Integer> filtreAnneeProperty() {
        return filtreAnnee;
    }

    public ObjectProperty<TriMultisite> triProperty() {
        return tri;
    }

    public ReadOnlyBooleanProperty nonVideProperty() {
        return nonVide.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty resumeProperty() {
        return resume.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty messageProperty() {
        return message.getReadOnlyProperty();
    }
}

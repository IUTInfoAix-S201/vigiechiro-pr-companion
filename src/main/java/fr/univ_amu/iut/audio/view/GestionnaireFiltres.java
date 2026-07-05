package fr.univ_amu.iut.audio.view;

import fr.univ_amu.iut.audio.viewmodel.FiltresAudio;
import fr.univ_amu.iut.commun.model.NormalisationTexte;
import fr.univ_amu.iut.validation.model.LigneObservationAudio;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/// Barre de filtres de la table audio, patron **« à la Notion »** (#470/#471) : une **recherche texte
/// permanente**, un bouton **« + Filtre »** qui liste les critères non encore actifs, et des **puces**
/// retirables pour les filtres actifs. Chaque puce branche/retire son prédicat sur [FiltresAudio] ; la
/// conjonction est appliquée à la table et les compteurs suivent le sous-ensemble affiché.
///
/// Prototype dans `audio/view` (composant partagé à venir en phase d'uniformisation, aussi pour analyse /
/// multisite). Logique sortie du controller (pur câblage) pour tenir les seuils de cohésion PMD.
final class GestionnaireFiltres {

    /// Clé du filtre de **recherche texte** (permanent, distinct des critères du menu « + Filtre »).
    private static final String NOM_TEXTE = "texte";

    private final MenuButton menuAjout;
    private final Pane puces;
    private final FiltresAudio filtres;
    private final List<CritereFiltre> criteres;
    private final TextField recherche;
    private final Set<String> actifs = new HashSet<>();

    GestionnaireFiltres(
            TextField recherche, MenuButton menuAjout, Pane puces, FiltresAudio filtres, List<CritereFiltre> criteres) {
        this.recherche = Objects.requireNonNull(recherche, "recherche");
        this.menuAjout = Objects.requireNonNull(menuAjout, "menuAjout");
        this.puces = Objects.requireNonNull(puces, "puces");
        this.filtres = Objects.requireNonNull(filtres, "filtres");
        this.criteres = List.copyOf(criteres);
        recherche
                .textProperty()
                .addListener((obs, avant, texte) -> filtres.definir(
                        NOM_TEXTE, texte == null || texte.isBlank() ? null : ligne -> correspond(ligne, texte)));
        reconstruireMenu();
    }

    /// Retire **tous** les filtres (texte + puces) : utilisé quand on doit garantir la visibilité d'une
    /// observation ciblée (navigation), quel que soit le filtrage courant.
    void reinitialiser() {
        recherche.clear(); // retire le filtre texte via son écouteur
        puces.getChildren().clear();
        actifs.clear();
        filtres.reinitialiser();
        reconstruireMenu();
    }

    /// Menu « + Filtre » : les critères **non encore actifs** ; désactivé quand tout est déjà ajouté.
    private void reconstruireMenu() {
        menuAjout
                .getItems()
                .setAll(criteres.stream()
                        .filter(critere -> !actifs.contains(critere.nom()))
                        .map(this::itemMenu)
                        .toList());
        menuAjout.setDisable(menuAjout.getItems().isEmpty());
    }

    private MenuItem itemMenu(CritereFiltre critere) {
        MenuItem item = new MenuItem(critere.libelle());
        item.setOnAction(evenement -> ajouterPuce(critere));
        return item;
    }

    private void ajouterPuce(CritereFiltre critere) {
        actifs.add(critere.nom());
        Node editeur = critere.editeur(predicat -> filtres.definir(critere.nom(), predicat));
        puces.getChildren().add(construirePuce(critere, editeur));
        reconstruireMenu();
    }

    private HBox construirePuce(CritereFiltre critere, Node editeur) {
        HBox puce = new HBox(6.0, new Label(critere.libelle()));
        puce.getStyleClass().add("puce-filtre");
        if (editeur != null) {
            puce.getChildren().add(editeur);
        }
        Button retirer = new Button("✕");
        retirer.getStyleClass().add("puce-filtre-retirer");
        retirer.setAccessibleText("Retirer le filtre " + critere.libelle());
        retirer.setOnAction(evenement -> retirerPuce(critere, puce));
        puce.getChildren().add(retirer);
        return puce;
    }

    private void retirerPuce(CritereFiltre critere, HBox puce) {
        puces.getChildren().remove(puce);
        actifs.remove(critere.nom());
        filtres.definir(critere.nom(), null);
        reconstruireMenu();
    }

    /// Vrai si un des champs cherchables (fichier, taxon Tadarida, commentaire) contient `texte`
    /// (comparaison **insensible casse/accents**).
    private static boolean correspond(LigneObservationAudio ligne, String texte) {
        String aiguille = NormalisationTexte.normaliser(texte);
        return contient(ligne.nomFichier(), aiguille)
                || contient(ligne.taxonTadarida(), aiguille)
                || contient(ligne.nomTadarida(), aiguille)
                || contient(ligne.commentaire(), aiguille);
    }

    private static boolean contient(String champ, String aiguille) {
        return champ != null && NormalisationTexte.normaliser(champ).contains(aiguille);
    }
}

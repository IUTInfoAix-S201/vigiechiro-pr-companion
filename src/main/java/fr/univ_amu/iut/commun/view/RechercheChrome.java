package fr.univ_amu.iut.commun.view;

import fr.univ_amu.iut.commun.model.RechercheGlobale;
import fr.univ_amu.iut.commun.model.ResultatRecherche;
import fr.univ_amu.iut.commun.viewmodel.ContexteSite;
import java.util.List;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

/// Recherche globale du chrome (#144) : câble le **champ** de recherche, la **liste déroulante** de
/// résultats et la **navigation** au clic/clavier. Extrait de `MainController` pour garder le contrôleur
/// du chrome focalisé sur la navigation (et sous le seuil de taille de classe).
///
/// Comportement : la saisie alimente la liste (insensible casse/accents, via [RechercheGlobale]) ; ↓
/// depuis le champ entre dans la liste, Entrée ouvre l'élément, Échap ferme ; la liste se ferme quand le
/// focus quitte la zone de recherche. La cellule affiche un **en-tête de groupe** (Sites/Points/Passages)
/// et expose le résultat en `accessibleText` (#163).
final class RechercheChrome {

    private final TextField champ;
    private final VBox panneau;
    private final ListView<ResultatRecherche> liste;
    private final RechercheGlobale recherche;
    private final OuvrirSite ouvrirSite;
    private final OuvrirPassage ouvrirPassage;

    RechercheChrome(
            TextField champ,
            VBox panneau,
            ListView<ResultatRecherche> liste,
            RechercheGlobale recherche,
            OuvrirSite ouvrirSite,
            OuvrirPassage ouvrirPassage) {
        this.champ = champ;
        this.panneau = panneau;
        this.liste = liste;
        this.recherche = recherche;
        this.ouvrirSite = ouvrirSite;
        this.ouvrirPassage = ouvrirPassage;
    }

    /// Installe la cellule, les écouteurs de saisie/clavier/souris et la fermeture automatique.
    void configurer() {
        liste.setCellFactory(vue -> new CelluleResultat());
        champ.textProperty().addListener((obs, ancien, texte) -> majResultats(texte));

        liste.setOnMouseClicked(
                evenement -> naviguerVers(liste.getSelectionModel().getSelectedItem()));
        liste.setOnKeyPressed(evenement -> {
            if (evenement.getCode() == KeyCode.ENTER) {
                naviguerVers(liste.getSelectionModel().getSelectedItem());
            } else if (evenement.getCode() == KeyCode.ESCAPE) {
                masquer();
                champ.requestFocus();
            }
        });
        champ.setOnKeyPressed(evenement -> {
            if (evenement.getCode() == KeyCode.DOWN && !liste.getItems().isEmpty()) {
                liste.getSelectionModel().select(0);
                liste.requestFocus();
                evenement.consume();
            } else if (evenement.getCode() == KeyCode.ENTER && !liste.getItems().isEmpty()) {
                naviguerVers(liste.getItems()
                        .get(Math.max(0, liste.getSelectionModel().getSelectedIndex())));
            } else if (evenement.getCode() == KeyCode.ESCAPE) {
                masquer();
            }
        });

        // Fermer quand le focus quitte la zone de recherche (clic ailleurs, Tab) : test différé d'un tick
        // car lors d'un clic champ → liste le focus transite par un état intermédiaire.
        champ.focusedProperty().addListener((obs, ancien, focus) -> Platform.runLater(this::masquerSiHorsRecherche));
        liste.focusedProperty().addListener((obs, ancien, focus) -> Platform.runLater(this::masquerSiHorsRecherche));
    }

    /// Donne le focus au champ et présélectionne son contenu (raccourci Ctrl+F).
    void activer() {
        champ.requestFocus();
        champ.selectAll();
    }

    private void majResultats(String texte) {
        List<ResultatRecherche> resultats = recherche.rechercher(texte);
        liste.getItems().setAll(resultats);
        boolean aDesResultats = !resultats.isEmpty();
        panneau.setVisible(aDesResultats);
        panneau.setManaged(aDesResultats);
        if (aDesResultats) {
            liste.getSelectionModel().clearSelection();
        }
    }

    private void masquer() {
        panneau.setVisible(false);
        panneau.setManaged(false);
    }

    private void masquerSiHorsRecherche() {
        if (!champ.isFocused() && !liste.isFocused()) {
            masquer();
        }
    }

    /// Ouvre l'écran cible : site/point → détail du site (carré) ; passage → M-Passage avec le contexte
    /// site/point. Vide le champ et ferme la liste.
    private void naviguerVers(ResultatRecherche resultat) {
        if (resultat == null) {
            return;
        }
        masquer();
        champ.clear();
        switch (resultat.type()) {
            case SITE, POINT -> ouvrirSite.ouvrirDetail(resultat.numeroCarre());
            case PASSAGE ->
                ouvrirPassage.ouvrir(
                        resultat.idPassage(),
                        new ContexteSite(resultat.numeroCarre(), resultat.codePoint(), resultat.nomSite()));
        }
    }

    /// Cellule d'un résultat : libellé + détail, précédés d'un **en-tête de groupe** quand le type change.
    private final class CelluleResultat extends ListCell<ResultatRecherche> {
        @Override
        protected void updateItem(ResultatRecherche resultat, boolean vide) {
            super.updateItem(resultat, vide);
            if (vide || resultat == null) {
                setText(null);
                setGraphic(null);
                setAccessibleText(null);
                return;
            }
            Label libelle = new Label(resultat.libelle());
            libelle.getStyleClass().add("recherche-libelle");
            Label details = new Label(resultat.details());
            details.getStyleClass().add("recherche-details");
            VBox texte = new VBox(2, libelle, details);
            texte.setAlignment(Pos.CENTER_LEFT);

            boolean premierDuType = getIndex() == 0
                    || getListView().getItems().get(getIndex() - 1).type() != resultat.type();
            if (premierDuType) {
                Label entete = new Label(resultat.type().libellePluriel());
                entete.getStyleClass().add("recherche-groupe");
                setGraphic(new VBox(4, entete, texte));
            } else {
                setGraphic(texte);
            }
            setText(null);
            setAccessibleText(
                    resultat.type().libellePluriel() + " : " + resultat.libelle() + ". " + resultat.details());
        }
    }
}

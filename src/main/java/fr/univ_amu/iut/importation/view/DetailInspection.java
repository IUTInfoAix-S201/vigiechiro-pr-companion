package fr.univ_amu.iut.importation.view;

import fr.univ_amu.iut.commun.view.IconesSeverite;
import fr.univ_amu.iut.commun.viewmodel.RetourOperation.Severite;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableStringValue;
import javafx.scene.control.Label;

/// Une ligne d'inspection : ce que le dossier contient, et si c'est **présent ou absent**.
///
/// Les trois lignes de la section 2 (journal du capteur, relevé climatique, enregistrements WAV)
/// écrivaient leur présence sous forme de glyphe en tête du texte - `« ✓ Journal du capteur : … »`,
/// `« ⚠ Relevé climatique absent »`. Un caractère dans un libellé dépend des polices installées et ne
/// se teinte pas avec le texte ([ADR
/// 0035](../../../../../../../dev-docs/decisions/0035-un-pictogramme-est-une-icone-pas-un-caractere.md)).
///
/// Les classes `.insp-ok` (vert) et `.insp-absent` (ambre) **existaient déjà** dans `importation.css`,
/// sans aucun usage : la couleur avait été prévue pour dire cette présence, et le glyphe l'a dite à sa
/// place. Elles reprennent leur rôle, et l'icône vient de [IconesSeverite] - la même table que le
/// bandeau, le compte rendu et l'encart, pour qu'un même sens ait partout la même forme.
final class DetailInspection {

    private DetailInspection() {}

    /// Lie le libellé à son texte, son icône de présence et sa couleur.
    ///
    /// `present` pilote les trois d'un coup : ils ne peuvent donc pas se contredire, ce qu'un glyphe
    /// recopié dans deux branches d'un ternaire ne garantissait pas.
    ///
    /// Le paramètre est un [ObservableBooleanValue] et **non** un `ObservableValue<Boolean>` : passer
    /// `propriete.asObject()` fabriquerait une liaison intermédiaire que plus rien ne retient une fois
    /// l'appel terminé. Elle est alors collectée, l'écouteur ne se déclenche plus, et l'icône reste
    /// figée sur sa valeur initiale - un journal présent s'affichait avec le triangle de l'absence.
    static void lier(Label label, ObservableBooleanValue present, ObservableStringValue texte) {
        label.textProperty().bind(texte);
        rendre(label, present.get());
        present.addListener((observable, avant, apres) -> rendre(label, apres));
    }

    /// Lie un libellé dont l'objet est **toujours** présent : il n'a pas de branche « absent ».
    static void lierPresent(Label label, ObservableStringValue texte) {
        label.textProperty().bind(texte);
        rendre(label, true);
    }

    private static void rendre(Label label, boolean present) {
        Severite severite = present ? Severite.SUCCES : Severite.AVERTISSEMENT;
        label.getStyleClass().setAll(present ? "insp-ok" : "insp-absent");
        label.setGraphic(IconesSeverite.icone(severite, "insp-icone"));
    }
}

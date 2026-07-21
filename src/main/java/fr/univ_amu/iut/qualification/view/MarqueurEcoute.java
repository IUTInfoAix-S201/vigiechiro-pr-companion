package fr.univ_amu.iut.qualification.view;

import fr.univ_amu.iut.qualification.model.SequenceEnSelection;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

/// Rendu de la colonne « Écouté » de la liste de sélection : l'état d'écoute d'une séquence, **posé**
/// en icône plutôt qu'écrit en glyphe (#2237, hygiène ADR 0035 « un pictogramme se pose, il ne
/// s'écrit pas »).
///
/// La colonne renvoyait `ecoutee() ? "✓" : "○"` - deux pictogrammes **dans une chaîne**. Ils ne
/// disent pourtant pas une sévérité (le ✓ ici veut dire « écouté », pas « succès ») mais un **état
/// binaire**, que la table doit poser comme elle pose déjà le badge de verdict ([VerdictParFichier]) :
/// un `cellFactory` qui met un [FontIcon] en `graphic`.
///
/// La **forme** distingue les deux états - un CHECK plein quand la séquence est écoutée, un cercle
/// creux sinon ; la **couleur**, posée par la feuille de style, reste neutre pour ne pas emprunter le
/// vert du succès.
final class MarqueurEcoute {

    private MarqueurEcoute() {}

    /// Câble la colonne : la donnée est le booléen `ecoutee()` de la ligne, le rendu une icône d'état.
    static void lier(TableColumn<SequenceEnSelection, Boolean> colEcoute) {
        colEcoute.setCellValueFactory(
                cellule -> new ReadOnlyObjectWrapper<>(cellule.getValue().ecoutee()));
        colEcoute.setCellFactory(colonne -> new Cellule());
    }

    /// L'icône de l'état d'écoute : CHECK plein si `ecoutee`, cercle creux sinon. La classe de style
    /// porte la couleur (neutre), le texte accessible dit l'état en toutes lettres.
    static FontIcon icone(boolean ecoutee) {
        FontIcon icone = new FontIcon(ecoutee ? FontAwesomeSolid.CHECK : FontAwesomeRegular.CIRCLE);
        icone.getStyleClass().add(ecoutee ? "marqueur-ecoute-oui" : "marqueur-ecoute-non");
        icone.setAccessibleText(ecoutee ? "écoutée" : "non écoutée");
        return icone;
    }

    /// Cellule d'état : pose l'icône d'écoute en `graphic`, rien pour une ligne vide.
    private static final class Cellule extends TableCell<SequenceEnSelection, Boolean> {
        @Override
        protected void updateItem(Boolean ecoutee, boolean vide) {
            super.updateItem(ecoutee, vide);
            setGraphic(vide || ecoutee == null ? null : icone(ecoutee));
        }
    }
}

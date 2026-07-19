package fr.univ_amu.iut.multisite.view;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import org.kordamp.ikonli.javafx.FontIcon;

/// Stylage des **contrôles superposés à la carte** de M-Multisite : les boutons d'overlay (recadrer,
/// éditer/enregistrer les positions) et les **poignées** de repli des panneaux. Sorti du
/// [MultisiteController] (fonctions de présentation pures, sans état) pour le garder sous le seuil de
/// taille (PMD `NcssCount`).
final class StyleControlesCarte {

    private StyleControlesCarte() {}

    /// Applique la classe CSS `classe` à un bouton d'overlay, lui pose son **icône** et renseigne son
    /// libellé accessible + son infobulle avec `description`.
    ///
    /// Ces boutons n'ont pas de texte : l'icône **est** le libellé, et c'est `description` qui le dit aux
    /// lecteurs d'écran comme au survol. Une [FontIcon] plutôt qu'un caractère (#1564, règle #700) : un
    /// glyphe littéral dépend des polices de la machine, et ne suit ni la couleur ni l'état du contrôle.
    static void overlay(ButtonBase bouton, String classe, String iconeLiteral, String description) {
        bouton.getStyleClass().add(classe);
        bouton.setGraphic(new FontIcon(iconeLiteral));
        bouton.setAccessibleText(description);
        bouton.setTooltip(new Tooltip(description));
    }

    /// Configure une poignée de repli : libellé visible **précédé ou suivi** de son chevron, libellé
    /// accessible + infobulle, et état activé.
    ///
    /// Le chevron pointe vers le panneau qui va se replier ou se rouvrir : il change donc de sens avec
    /// l'état, comme le libellé. On le pose ici **impérativement** plutôt que par `IconeSelonEtat` : cet
    /// état-là n'est pas une propriété observable mais la visibilité de nœuds, recalculée à chaque appel.
    ///
    /// @param cote côté où placer le chevron par rapport au texte ([ContentDisplay#LEFT] ou
    ///     [ContentDisplay#RIGHT]) : la poignée de gauche pointe vers l'extérieur, celle de droite aussi.
    static void poignee(
            Button poignee,
            String libelle,
            String iconeLiteral,
            ContentDisplay cote,
            String description,
            boolean actif) {
        poignee.setText(libelle);
        poignee.setGraphic(new FontIcon(iconeLiteral));
        poignee.setContentDisplay(cote);
        poignee.setAccessibleText(description);
        poignee.setTooltip(new Tooltip(description));
        poignee.setDisable(!actif);
    }
}

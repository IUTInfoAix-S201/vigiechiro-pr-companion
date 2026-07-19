package fr.univ_amu.iut.commun.view;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;

/// Relit l'infobulle posée par [IndicateurBlocage#expliquer] sur une enveloppe, pour que les tests
/// puissent vérifier **ce qu'elle dit** et pas seulement qu'elle existe.
///
/// `Tooltip.install(Node, Tooltip)` ne range pas l'infobulle dans une propriété publique du nœud
/// (`getTooltip()` n'existe que sur `Control`) : elle vit dans la table de propriétés du nœud, sous une
/// clé interne à JavaFX. On la relit donc par cette clé, faute de mieux.
///
/// Cette dépendance à un détail d'implémentation est **assumée et localisée ici** : si une version de
/// JavaFX change la clé, un seul fichier échoue, avec un message qui dit quoi corriger. L'alternative -
/// ne vérifier que la présence de l'infobulle - laisserait passer exactement le défaut que #1970
/// corrige : un motif de blocage qui existe mais que personne ne lit.
public final class InfobulleDeBlocage {

    /// Clé interne utilisée par `Tooltip.install` (cf. `Tooltip.TOOLTIP_PROP_KEY`).
    private static final String CLE = "javafx.scene.control.Tooltip";

    private InfobulleDeBlocage() {}

    /// Texte de l'infobulle installée sur `enveloppe`.
    ///
    /// @throws AssertionError si aucune infobulle n'y est posée - un blocage sans motif est le défaut
    ///     que ces tests cherchent
    public static String texteDe(Node enveloppe) {
        Object installee = enveloppe.getProperties().get(CLE);
        if (!(installee instanceof Tooltip infobulle)) {
            throw new AssertionError("Aucune infobulle sur " + enveloppe.getId()
                    + " : un contrôle grisé sans motif ne dit pas à l'utilisateur ce qu'il doit corriger."
                    + " Si JavaFX a changé sa clé interne, corriger InfobulleDeBlocage.CLE.");
        }
        return infobulle.getText();
    }
}

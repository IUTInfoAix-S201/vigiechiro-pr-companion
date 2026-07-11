package fr.univ_amu.iut.commun.view;

import javafx.beans.property.BooleanProperty;
import javafx.stage.Window;

/// Contrat d'une **entrée du menu ☰** (outils) du chrome, contribué par DI. Même mécanisme
/// d'inversion de dépendance que [ActiviteAccueil] / [OngletReglages] : le socle déclare ce contrat,
/// chaque feature (ou le socle) en fournit des implémentations enregistrées dans le
/// `Multibinder<ActionMenu>` de son module, et le [MainController] bâtit le `MenuButton` depuis
/// `Set<ActionMenu>` **sans dépendre d'aucune feature**. Ajouter une entrée = une implémentation + une
/// ligne de binding, le socle n'est jamais retouché (#930).
///
/// Deux formes : une **action** (clic → [#executer(Window)]) ou une **bascule** ([#estBascule()] vrai,
/// case liée à [#selection()]).
public interface ActionMenu {

    /// Groupe d'appartenance (détermine les séparateurs, cf. [GroupeMenu]).
    GroupeMenu groupe();

    /// Rang **dans le groupe** (ordre croissant).
    int ordre();

    /// Libellé affiché. Réévalué à **chaque ouverture** du menu, pour les libellés dynamiques (ex.
    /// l'état de connexion).
    String libelle();

    /// Code d'icône [Ikonli](https://kordamp.org/ikonli/) affiché en tête d'entrée (chaîne vide =
    /// aucune ; le socle construit le `FontIcon`). Les entrées historiques portent leur pictogramme
    /// directement dans le [#libelle()].
    default String iconeLiteral() {
        return "";
    }

    /// Vrai si l'entrée est une **case à cocher** (bascule) plutôt qu'une action. Une bascule utilise
    /// [#selection()] et ne reçoit pas de clic via [#executer(Window)].
    default boolean estBascule() {
        return false;
    }

    /// Property booléenne de la bascule, liée bidirectionnellement à la case. Appelée seulement si
    /// [#estBascule()] est vrai.
    default BooleanProperty selection() {
        throw new UnsupportedOperationException(
                "Entrée de menu sans bascule : " + getClass().getName());
    }

    /// Exécute l'action (entrée non-bascule). `proprietaire` est la fenêtre propriétaire des éventuels
    /// dialogues (sélecteurs, alertes) ; les actions sans dialogue l'ignorent.
    void executer(Window proprietaire);
}

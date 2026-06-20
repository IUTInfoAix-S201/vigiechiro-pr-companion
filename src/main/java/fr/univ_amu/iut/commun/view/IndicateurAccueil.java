package fr.univ_amu.iut.commun.view;

/// Indicateur chiffré du **tableau de bord d'accueil** (bandeau de compteurs au-dessus des
/// cartes d'activités) : « 12 sites », « 340 observations »…
///
/// Même mécanisme d'inversion de dépendance que [ActiviteAccueil] : le socle (`commun.view`)
/// déclare ce contrat ; chaque feature en fournit une implémentation (qui interroge ses propres
/// DAO/services) et l'enregistre dans le `Multibinder<IndicateurAccueil>` de son module Guice.
/// Le [MainController] injecte `Set<IndicateurAccueil>` et bâtit le bandeau **sans dépendre
/// d'aucune feature** (graphe de slices acyclique, cf. `ArchitectureTest`).
///
/// La [#valeur] est (re)calculée à chaque affichage de l'accueil, pour refléter l'état courant
/// de la base après une action (import, déclaration de site…).
public interface IndicateurAccueil {

    /// Rang d'affichage (ordre croissant : les plus petits en premier).
    int ordre();

    /// Code d'icône [Ikonli](https://kordamp.org/ikonli/) FontAwesome 5 de la pastille (ex.
    /// `"fas-moon"`). Le socle en construit un `FontIcon` coloré ; la feature ne dépend d'aucune
    /// classe JavaFX/Ikonli.
    String iconeLiteral();

    /// Couleur d'accent (hex CSS, ex. `"#a29bfe"`) appliquée à l'icône de la pastille.
    String couleur();

    /// Libellé court (ex. « Sites », « Points d'écoute »).
    String libelle();

    /// Valeur courante du compteur (calculée à la volée, ≥ 0).
    long valeur();
}

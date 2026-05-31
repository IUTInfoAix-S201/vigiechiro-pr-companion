package fr.univ_amu.iut.cli.model;

/// Erreur d'**invocation** de la ligne de commande : commande inconnue, argument requis manquant
/// ou mal formé. Distincte des erreurs *métier* (`RegleMetierException`) et
/// *techniques* (`DataAccessException`) qui surviennent *pendant* l'exécution d'une
/// commande.
///
/// La `Cli` la traduit en code de sortie `2` (« mauvaise invocation », convention
/// proche de `EX_USAGE`), tandis qu'un échec d'exécution donne un code `1`. Aucun import
/// JavaFX (classe `model` pure, cf. règle ArchUnit `..model..` sans `javafx..`).
public final class ErreurUsage extends RuntimeException {

  public ErreurUsage(String message) {
    super(message);
  }
}

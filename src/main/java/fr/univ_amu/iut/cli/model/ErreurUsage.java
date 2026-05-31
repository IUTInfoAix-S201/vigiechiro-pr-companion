package fr.univ_amu.iut.cli.model;

/**
 * Erreur d'<b>invocation</b> de la ligne de commande : commande inconnue, argument requis manquant
 * ou mal formé. Distincte des erreurs <i>métier</i> ({@code RegleMetierException}) et
 * <i>techniques</i> ({@code DataAccessException}) qui surviennent <i>pendant</i> l'exécution d'une
 * commande.
 *
 * <p>La {@code Cli} la traduit en code de sortie {@code 2} (« mauvaise invocation », convention
 * proche de {@code EX_USAGE}), tandis qu'un échec d'exécution donne un code {@code 1}. Aucun import
 * JavaFX (classe {@code model} pure, cf. règle ArchUnit {@code ..model..} sans {@code javafx..}).
 */
public final class ErreurUsage extends RuntimeException {

  public ErreurUsage(String message) {
    super(message);
  }
}

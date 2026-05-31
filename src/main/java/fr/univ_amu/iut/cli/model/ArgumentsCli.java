package fr.univ_amu.iut.cli.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Analyseur d'arguments <b>minimal et manuel</b> de la ligne de commande, sans aucune dépendance
 * externe (pour ne pas toucher au {@code pom.xml} ni au {@code module-info}). Il reconnaît des
 * options longues de la forme {@code --cle valeur} ; une option suivie d'une autre option (ou en
 * fin de tableau) est traitée comme un drapeau sans valeur.
 *
 * <pre>{@code
 * --source /tmp/sd --point 3 --verbeux   →  {source=/tmp/sd, point=3, verbeux=null}
 * }</pre>
 *
 * <p>Classe {@code model} pure (aucun import JavaFX) : elle ne fait que transformer un {@code
 * String[]} en table d'options consultable. Les valeurs manquantes ou mal typées lèvent une {@link
 * ErreurUsage}, que la {@code Cli} traduit en code de sortie « mauvaise invocation ».
 */
public final class ArgumentsCli {

  private final Map<String, String> options;

  private ArgumentsCli(Map<String, String> options) {
    this.options = options;
  }

  /**
   * Analyse les jetons situés <b>après</b> le nom de la sous-commande.
   *
   * @param jetons tableau des arguments restants (peut être vide)
   * @return la table des options reconnues (ordre d'apparition préservé)
   */
  public static ArgumentsCli analyser(String[] jetons) {
    Map<String, String> options = new LinkedHashMap<>();
    int i = 0;
    while (i < jetons.length) {
      String jeton = jetons[i];
      if (jeton.startsWith("--") && jeton.length() > 2) {
        String cle = jeton.substring(2);
        if (i + 1 < jetons.length && !jetons[i + 1].startsWith("--")) {
          options.put(cle, jetons[i + 1]);
          i += 2;
        } else {
          options.put(cle, null); // drapeau sans valeur
          i += 1;
        }
      } else {
        // Jeton positionnel non reconnu : ignoré (les commandes lisent des options nommées).
        i += 1;
      }
    }
    return new ArgumentsCli(options);
  }

  /** {@code true} si l'option {@code --cle} a été fournie (avec ou sans valeur). */
  public boolean present(String cle) {
    return options.containsKey(cle);
  }

  /**
   * Valeur de {@code --cle} si elle est présente et porte une valeur, sinon {@link
   * Optional#empty()}.
   */
  public Optional<String> valeur(String cle) {
    return Optional.ofNullable(options.get(cle));
  }

  /**
   * Valeur obligatoire de {@code --cle}.
   *
   * @throws ErreurUsage si l'option est absente ou fournie sans valeur
   */
  public String exiger(String cle) {
    String valeur = options.get(cle);
    if (valeur == null) {
      throw new ErreurUsage("Argument requis manquant ou sans valeur : --" + cle);
    }
    return valeur;
  }

  /**
   * Valeur obligatoire de {@code --cle} convertie en {@code long}.
   *
   * @throws ErreurUsage si l'option est absente, sans valeur, ou non numérique
   */
  public long exigerLong(String cle) {
    String valeur = exiger(cle);
    try {
      return Long.parseLong(valeur.trim());
    } catch (NumberFormatException e) {
      throw new ErreurUsage("L'argument --" + cle + " doit être un entier : « " + valeur + " ».");
    }
  }

  /**
   * Valeur entière optionnelle de {@code --cle}.
   *
   * @throws ErreurUsage si l'option est présente mais non numérique
   */
  public OptionalInt entierOptionnel(String cle) {
    String valeur = options.get(cle);
    if (valeur == null) {
      return OptionalInt.empty();
    }
    try {
      return OptionalInt.of(Integer.parseInt(valeur.trim()));
    } catch (NumberFormatException e) {
      throw new ErreurUsage("L'argument --" + cle + " doit être un entier : « " + valeur + " ».");
    }
  }
}

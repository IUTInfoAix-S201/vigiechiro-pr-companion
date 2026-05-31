package fr.univ_amu.iut.commun.model.validation;

import java.util.regex.Pattern;

/// Validation d'un code de point d'écoute (R2).
///
/// Un code de point vaut **exactement 2 caractères : une lettre majuscule suivie d'un
/// chiffre** (ex. `A1`, `Z4`). La validation se fait à la saisie.
public final class ValidateurCodePoint {

  private static final Pattern LETTRE_CHIFFRE = Pattern.compile("[A-Z][0-9]");

  private ValidateurCodePoint() {}

  /// `true` si `code` est une lettre majuscule suivie d'un chiffre.
  public static boolean estValide(String code) {
    return code != null && LETTRE_CHIFFRE.matcher(code).matches();
  }

  /// Vérifie `code` et le renvoie inchangé, ou lève une [IllegalArgumentException] si la
  /// règle R2 n'est pas respectée.
  public static String exigerValide(String code) {
    if (!estValide(code)) {
      throw new IllegalArgumentException(
          "Code de point invalide (R2 : une lettre majuscule + un chiffre, ex. A1) : " + code);
    }
    return code;
  }
}

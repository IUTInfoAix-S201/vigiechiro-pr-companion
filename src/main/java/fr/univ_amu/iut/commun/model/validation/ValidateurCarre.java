package fr.univ_amu.iut.commun.model.validation;

import java.util.regex.Pattern;

/// Validation d'un numéro de carré (R1).
///
/// Un numéro de carré vaut **exactement 6 chiffres**. Les deux premiers correspondent au
/// département : le **zéro de tête est obligatoire** pour les départements 1 à 9 (ex. `040962` et
/// non `40962`). C'est une chaîne, pas un entier : un entier perdrait le zéro de
/// tête.
public final class ValidateurCarre {

    private static final Pattern SIX_CHIFFRES = Pattern.compile("\\d{6}");

    private ValidateurCarre() {}

    /// `true` si `carre` est composé d'exactement 6 chiffres.
    public static boolean estValide(String carre) {
        return carre != null && SIX_CHIFFRES.matcher(carre).matches();
    }

    /// Vérifie `carre` et le renvoie inchangé, ou lève une [IllegalArgumentException] si
    /// la règle R1 n'est pas respectée.
    public static String exigerValide(String carre) {
        if (!estValide(carre)) {
            throw new IllegalArgumentException(
                    "Numéro de carré invalide (6 chiffres, zéro de tête obligatoire) : " + carre);
        }
        return carre;
    }
}

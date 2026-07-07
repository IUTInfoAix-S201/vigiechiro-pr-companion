package fr.univ_amu.iut.importation.view;

import java.util.function.Function;
import javafx.util.StringConverter;

/// Fabriques de [StringConverter] pour les combobox de M-Import. Helper de vue extrait du controller :
/// convertit une valeur en libellé d'affichage (l'édition inverse n'est pas utilisée ici, les combobox
/// n'étant pas éditables).
final class Convertisseurs {

    private Convertisseurs() {}

    /// Convertisseur **affichage seul** : `versTexte` pour rendre une valeur (chaîne vide si `null`),
    /// `fromString` renvoie `null` (combobox non éditable).
    static <T> StringConverter<T> depuis(Function<T, String> versTexte) {
        return new StringConverter<>() {
            @Override
            public String toString(T valeur) {
                return valeur == null ? "" : versTexte.apply(valeur);
            }

            @Override
            public T fromString(String texte) {
                return null;
            }
        };
    }
}

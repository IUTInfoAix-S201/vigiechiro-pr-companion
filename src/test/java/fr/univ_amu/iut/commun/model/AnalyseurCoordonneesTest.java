package fr.univ_amu.iut.commun.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/// Analyse de coordonnées GPS multi-format (#153) : degrés décimaux (DD), degrés/minutes/secondes (DMS),
/// degrés-minutes décimales, avec hémisphère ou signe. Toutes les variantes valides d'une même position
/// (≈ 43,401 N / 1,574 W) doivent converger vers la même valeur décimale.
class AnalyseurCoordonneesTest {

    private static final double TOLERANCE = 1e-6;

    @Nested
    @DisplayName("Degrés décimaux (DD)")
    class DegresDecimaux {

        @ParameterizedTest(name = "\"{0}\" → {1}")
        @CsvSource({
            "43.401, 43.401",
            "'43,401', 43.401", // virgule française
            "-1.574, -1.574",
            "'43.401 N', 43.401",
            "'43.401 S', -43.401",
            "'1.574 W', -1.574",
            "'1.574 E', 1.574",
        })
        void parse_le_decimal_avec_signe_ou_hemisphere(String saisie, double attendu) {
            assertThat(AnalyseurCoordonnees.enDegresDecimaux(saisie)).isCloseTo(attendu, within(TOLERANCE));
        }
    }

    @Nested
    @DisplayName("Degrés / minutes / secondes (DMS)")
    class DegresMinutesSecondes {

        @ParameterizedTest(name = "\"{0}\" ≈ 43.401")
        @ValueSource(
                strings = {
                    "43°24'3.6\"N",
                    "43 24 3.6 N",
                    "43:24:3.6 N",
                    "43°24.06'N", // degrés + minutes décimales
                    "N 43 24 3.6",
                })
        void toutes_les_ecritures_dms_donnent_la_meme_latitude(String saisie) {
            assertThat(AnalyseurCoordonnees.enDegresDecimaux(saisie)).isCloseTo(43.401, within(TOLERANCE));
        }

        @Test
        @DisplayName("l'hémisphère ouest rend la longitude négative")
        void ouest_est_negatif() {
            assertThat(AnalyseurCoordonnees.enDegresDecimaux("1°34'26.4\"W")).isCloseTo(-1.574, within(TOLERANCE));
        }
    }

    @Nested
    @DisplayName("Saisies invalides")
    class Invalides {

        @ParameterizedTest(name = "\"{0}\" rejeté")
        @ValueSource(
                strings = {
                    "abc", // pas de nombre
                    "43x", // lettre parasite
                    "43 61 00 N", // minutes ≥ 60
                    "43 24 75 N", // secondes ≥ 60
                    "43 24 03 05 N", // quatre composantes
                    "43N 24S", // deux hémisphères
                })
        void leve_une_exception(String saisie) {
            assertThatExceptionOfType(NumberFormatException.class)
                    .isThrownBy(() -> AnalyseurCoordonnees.enDegresDecimaux(saisie));
        }

        @Test
        @DisplayName("une saisie vide ou nulle est rejetée")
        void vide_rejete() {
            assertThatExceptionOfType(NumberFormatException.class)
                    .isThrownBy(() -> AnalyseurCoordonnees.enDegresDecimaux("  "));
            assertThatExceptionOfType(NumberFormatException.class)
                    .isThrownBy(() -> AnalyseurCoordonnees.enDegresDecimaux(null));
        }
    }
}

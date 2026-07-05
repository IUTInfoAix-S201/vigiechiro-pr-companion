package fr.univ_amu.iut.diagnostic.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Vérifie [AnalyseCoherenceHoraire] : la fenêtre nocturne est calculée au point d'Aix-en-Provence
/// pour la nuit du 20 juin 2026 (coucher ≈ 21:23, lever ≈ 05:58 heure locale), avec les scénarios
/// « hors nuit » et les dégradations propres (GPS ou horaires manquants, horaires illisibles,
/// latitude polaire).
class AnalyseCoherenceHoraireTest {

    private static final double AIX_LAT = 43.529;
    private static final double AIX_LON = 5.447;
    private static final String NUIT = "2026-06-20";

    @Test
    @DisplayName("Nuit cohérente : démarrage après le coucher, arrêt avant le lever → aucun écart")
    void nuit_coherente_sans_ecart() {
        CoherenceHoraire coherence = AnalyseCoherenceHoraire.analyser(AIX_LAT, AIX_LON, NUIT, "22:00:00", "05:00:00");

        assertThat(coherence.disponible()).isTrue();
        assertThat(coherence.coucherSoleil()).isCloseTo(LocalTime.of(21, 23), within(5, ChronoUnit.MINUTES));
        assertThat(coherence.leverSoleil()).isCloseTo(LocalTime.of(5, 58), within(5, ChronoUnit.MINUTES));
        assertThat(coherence.demarrageHorsNuit()).isFalse();
        assertThat(coherence.arretHorsNuit()).isFalse();
        assertThat(coherence.aUnEcart()).isFalse();
    }

    @Test
    @DisplayName("Démarrage avant le coucher du soleil → hors nuit")
    void demarrage_avant_coucher_est_hors_nuit() {
        CoherenceHoraire coherence = AnalyseCoherenceHoraire.analyser(AIX_LAT, AIX_LON, NUIT, "21:00:00", "05:00:00");

        assertThat(coherence.demarrageHorsNuit()).isTrue();
        assertThat(coherence.arretHorsNuit()).isFalse();
        assertThat(coherence.aUnEcart()).isTrue();
    }

    @Test
    @DisplayName("Arrêt après le lever du soleil → hors nuit")
    void arret_apres_lever_est_hors_nuit() {
        CoherenceHoraire coherence = AnalyseCoherenceHoraire.analyser(AIX_LAT, AIX_LON, NUIT, "22:00:00", "06:30:00");

        assertThat(coherence.arretHorsNuit()).isTrue();
        assertThat(coherence.demarrageHorsNuit()).isFalse();
        assertThat(coherence.aUnEcart()).isTrue();
    }

    @Test
    @DisplayName("Sans coordonnées GPS : cohérence indisponible")
    void sans_gps_est_indisponible() {
        assertThat(AnalyseCoherenceHoraire.analyser(null, null, NUIT, "22:00:00", "05:00:00"))
                .isEqualTo(CoherenceHoraire.indisponible());
    }

    @Test
    @DisplayName("Horaires manquants : cohérence indisponible")
    void horaires_manquants_est_indisponible() {
        assertThat(AnalyseCoherenceHoraire.analyser(AIX_LAT, AIX_LON, NUIT, null, null)
                        .disponible())
                .isFalse();
    }

    @Test
    @DisplayName("Horaires illisibles : cohérence indisponible (dégradation propre)")
    void horaires_illisibles_est_indisponible() {
        assertThat(AnalyseCoherenceHoraire.analyser(AIX_LAT, AIX_LON, NUIT, "minuit", "cinq heures")
                        .disponible())
                .isFalse();
    }

    @Test
    @DisplayName("Latitude polaire au solstice d'été (jour polaire) : cohérence indisponible")
    void latitude_polaire_est_indisponible() {
        assertThat(AnalyseCoherenceHoraire.analyser(78.22, 15.65, "2026-06-21", "22:00:00", "03:00:00")
                        .disponible())
                .isFalse();
    }
}

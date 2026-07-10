package fr.univ_amu.iut.validation.model;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.commun.api.DonneeVigieChiro;
import fr.univ_amu.iut.commun.api.ObservationVigieChiro;
import fr.univ_amu.iut.commun.model.ModeValidation;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Conversion des résultats VigieChiro ([DonneeVigieChiro]) en [LigneObservation] locales
/// ([ConversionDonneesVigieChiro], #719 axe 4.2) : projection **fidèle au parseur CSV** (mêmes unités,
/// fréquence arrondie à l'entier), aplatissement par observation, mode de validation déduit du taxon
/// observateur. Fonction pure.
class ConversionDonneesVigieChiroTest {

    @Test
    @DisplayName("une observation Tadarida brute -> LigneObservation (titre = séquence, freq arrondie, NON_VALIDE)")
    void observation_brute() {
        DonneeVigieChiro donnee = new DonneeVigieChiro(
                "Car130711-2026-Pass1-Z41-PaRec_20260703_220529_000",
                List.of(new ObservationVigieChiro("Pipkuh", 0.99, 44.6, 0.8, 4.7, "noise", null, null)));

        assertThat(ConversionDonneesVigieChiro.enLignes(List.of(donnee)))
                .containsExactly(new LigneObservation(
                        "Car130711-2026-Pass1-Z41-PaRec_20260703_220529_000",
                        0.8,
                        4.7,
                        45, // 44.6 arrondi à l'entier (colonne median_freq_khz INTEGER)
                        "Pipkuh",
                        0.99,
                        "noise",
                        null,
                        null,
                        ModeValidation.NON_VALIDE));
    }

    @Test
    @DisplayName("taxon observateur présent -> MANUEL ; fréquence absente -> null")
    void observation_validee_manuellement() {
        DonneeVigieChiro donnee = new DonneeVigieChiro(
                "F", List.of(new ObservationVigieChiro("Eptser", 0.70, null, 1.0, 2.0, null, "Pippip", 0.85)));

        assertThat(ConversionDonneesVigieChiro.enLignes(List.of(donnee)))
                .singleElement()
                .satisfies(ligne -> {
                    assertThat(ligne.frequenceMedianeKHz()).isNull();
                    assertThat(ligne.taxonObservateur()).isEqualTo("Pippip");
                    assertThat(ligne.probObservateur()).isEqualTo(0.85);
                    assertThat(ligne.modeValidation()).isEqualTo(ModeValidation.MANUEL);
                });
    }

    @Test
    @DisplayName("aplatissement : chaque observation devient une ligne portant le titre de sa donnée")
    void aplatissement_multi_fichiers() {
        DonneeVigieChiro a = new DonneeVigieChiro(
                "A",
                List.of(
                        new ObservationVigieChiro("Pipkuh", 0.9, 40.0, 0.0, 1.0, null, null, null),
                        new ObservationVigieChiro("noise", 0.5, 42.0, 1.0, 2.0, null, null, null)));
        DonneeVigieChiro b = new DonneeVigieChiro(
                "B", List.of(new ObservationVigieChiro("Nyclei", 0.8, 25.0, 0.0, 3.0, null, null, null)));

        assertThat(ConversionDonneesVigieChiro.enLignes(List.of(a, b)))
                .extracting(LigneObservation::nomSequence, LigneObservation::taxonTadarida)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("A", "Pipkuh"),
                        org.assertj.core.groups.Tuple.tuple("A", "noise"),
                        org.assertj.core.groups.Tuple.tuple("B", "Nyclei"));
    }

    @Test
    @DisplayName("liste vide -> aucune ligne")
    void vide() {
        assertThat(ConversionDonneesVigieChiro.enLignes(List.of())).isEmpty();
    }
}

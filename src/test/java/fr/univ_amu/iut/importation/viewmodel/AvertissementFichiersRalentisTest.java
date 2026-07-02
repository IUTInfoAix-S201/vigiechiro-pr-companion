package fr.univ_amu.iut.importation.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Rédaction de l'avertissement « enregistrements déjà ralentis » ([AvertissementFichiersRalentis]).
class AvertissementFichiersRalentisTest {

    @Test
    @DisplayName("Ralenti avec log : message citant l'en-tête et la fréquence du log")
    void ralenti_avec_log_redige_le_message() {
        String message = AvertissementFichiersRalentis.rediger(38400, 384000);
        assertThat(message).contains("38400").contains("384000").contains("rejetés");
    }

    @Test
    @DisplayName("Ralenti sans log : message citant le seuil d'ultrason brut")
    void ralenti_sans_log_redige_le_message() {
        assertThat(AvertissementFichiersRalentis.rediger(38400, null))
                .contains("38400")
                .contains("rejetés");
    }

    @Test
    @DisplayName("Vrai brut (en-tête = log) : pas d'avertissement")
    void vrai_brut_pas_d_avertissement() {
        assertThat(AvertissementFichiersRalentis.rediger(384000, 384000)).isEmpty();
    }

    @Test
    @DisplayName("Fréquence d'en-tête inconnue : pas d'avertissement")
    void frequence_inconnue_pas_d_avertissement() {
        assertThat(AvertissementFichiersRalentis.rediger(null, 384000)).isEmpty();
    }
}

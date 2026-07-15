package fr.univ_amu.iut.qualification;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.commun.model.Verdict;
import fr.univ_amu.iut.commun.model.VerdictFichier;
import fr.univ_amu.iut.qualification.model.AgregationVerdict;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Tests purs (sans base) de la dérivation du verdict final du passage à partir des verdicts par
/// fichier ([AgregationVerdict], #1524, lot 5).
class AgregationVerdictTest {

    @Test
    @DisplayName("Aucune séquence jugée (vide ou tout NON_JUGE) → À vérifier")
    void aucune_jugee_reste_a_verifier() {
        assertThat(AgregationVerdict.deriver(List.of())).isEqualTo(Verdict.A_VERIFIER);
        assertThat(AgregationVerdict.deriver(List.of(VerdictFichier.NON_JUGE, VerdictFichier.NON_JUGE)))
                .isEqualTo(Verdict.A_VERIFIER);
    }

    @Test
    @DisplayName("Toutes les séquences jugées Bon → OK (les NON_JUGE sont ignorées)")
    void toutes_bonnes_donnent_ok() {
        assertThat(AgregationVerdict.deriver(List.of(VerdictFichier.BON, VerdictFichier.NON_JUGE, VerdictFichier.BON)))
                .isEqualTo(Verdict.OK);
    }

    @Test
    @DisplayName("Au moins un Mauvais, exploitable → Douteux")
    void un_mauvais_donne_douteux() {
        assertThat(AgregationVerdict.deriver(List.of(VerdictFichier.BON, VerdictFichier.MAUVAIS, VerdictFichier.BON)))
                .isEqualTo(Verdict.DOUTEUX);
    }

    @Test
    @DisplayName("Minorité d'Inexploitable → Douteux (reste exploitable)")
    void minorite_inexploitable_donne_douteux() {
        // 1 inexploitable sur 4 jugées : pas la majorité → Douteux.
        assertThat(AgregationVerdict.deriver(List.of(
                        VerdictFichier.BON, VerdictFichier.BON, VerdictFichier.BON, VerdictFichier.INEXPLOITABLE)))
                .isEqualTo(Verdict.DOUTEUX);
    }

    @Test
    @DisplayName("Majorité stricte d'Inexploitable → À jeter")
    void majorite_inexploitable_donne_a_jeter() {
        // 2 inexploitables sur 3 jugées : majorité stricte → À jeter.
        assertThat(AgregationVerdict.deriver(
                        List.of(VerdictFichier.INEXPLOITABLE, VerdictFichier.INEXPLOITABLE, VerdictFichier.BON)))
                .isEqualTo(Verdict.A_JETER);
    }

    @Test
    @DisplayName("Moitié exactement inexploitable → pas la majorité → Douteux")
    void moitie_inexploitable_nest_pas_la_majorite() {
        assertThat(AgregationVerdict.deriver(List.of(VerdictFichier.INEXPLOITABLE, VerdictFichier.BON)))
                .isEqualTo(Verdict.DOUTEUX);
    }

    @Test
    @DisplayName("Round-trip du back-fill : diffuser un verdict passage puis re-dériver redonne le même")
    void round_trip_backfill() {
        // OK diffusé → tout Bon → OK ; Douteux → tout Mauvais → Douteux ; À jeter → tout Inexploitable → À jeter.
        assertThat(AgregationVerdict.deriver(List.of(VerdictFichier.BON, VerdictFichier.BON)))
                .isEqualTo(Verdict.OK);
        assertThat(AgregationVerdict.deriver(List.of(VerdictFichier.MAUVAIS, VerdictFichier.MAUVAIS)))
                .isEqualTo(Verdict.DOUTEUX);
        assertThat(AgregationVerdict.deriver(List.of(VerdictFichier.INEXPLOITABLE, VerdictFichier.INEXPLOITABLE)))
                .isEqualTo(Verdict.A_JETER);
    }
}

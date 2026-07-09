package fr.univ_amu.iut.commun.view;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.commun.model.StatutWorkflow;
import fr.univ_amu.iut.commun.model.Verdict;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Tests du composant [ColonneBadge] (#691) : dérivation de la classe CSS sémantique depuis un statut
/// workflow ou un verdict (le verdict absent retombe sur « à vérifier »).
class ColonneBadgeTest {

    @Test
    @DisplayName("classe(StatutWorkflow) dérive badge-statut-<nom>")
    void classe_du_statut() {
        assertThat(ColonneBadge.classe(StatutWorkflow.TRANSFORME)).isEqualTo("badge-statut-transforme");
        assertThat(ColonneBadge.classe(StatutWorkflow.PRET_A_DEPOSER)).isEqualTo("badge-statut-pret_a_deposer");
        assertThat(ColonneBadge.classe(StatutWorkflow.DEPOSE)).isEqualTo("badge-statut-depose");
    }

    @Test
    @DisplayName("classe(Verdict) dérive badge-verdict-<nom> ; absent = à vérifier")
    void classe_du_verdict() {
        assertThat(ColonneBadge.classe(Verdict.OK)).isEqualTo("badge-verdict-ok");
        assertThat(ColonneBadge.classe(Verdict.A_JETER)).isEqualTo("badge-verdict-a_jeter");
        assertThat(ColonneBadge.classe((Verdict) null)).isEqualTo("badge-verdict-a_verifier");
    }
}

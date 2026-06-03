package fr.univ_amu.iut.importation.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.importation.model.AnalyseCoherence;
import fr.univ_amu.iut.importation.model.JournalParse;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Tests de la mise en phrase de l'avertissement **« incohérence »** (#33). La détection est couverte
/// par `AnalyseCoherenceTest` ; ici on vérifie les branches du libellé (série, date, les deux) et le
/// cas concordant (chaîne vide).
class AvertissementIncoherenceTest {

    private static final LocalDate NUIT = LocalDate.of(2026, 4, 22);

    @Test
    @DisplayName("Identité concordante : aucun avertissement")
    void concordant_pas_d_avertissement() {
        AnalyseCoherence c = AnalyseCoherence.depuis(
                journal("1925492", NUIT), null, List.of(Path.of("PaRecPR1925492_20260422_203000.wav")));

        assertThat(AvertissementIncoherence.rediger(c)).isEmpty();
    }

    @Test
    @DisplayName("Série seule incohérente : le message cite la série déclarée et celle des fichiers")
    void serie_seule() {
        AnalyseCoherence c = AnalyseCoherence.depuis(
                journal("1925492", NUIT), null, List.of(Path.of("PaRecPR1648011_20260422_203000.wav")));

        assertThat(AvertissementIncoherence.rediger(c))
                .contains("la série déclarée (1925492)")
                .contains("série 1648011")
                .doesNotContain("la date du journal");
    }

    @Test
    @DisplayName("Date seule incohérente : le message cite la date du journal et celle des fichiers")
    void date_seule() {
        AnalyseCoherence c = AnalyseCoherence.depuis(
                journal("1925492", NUIT), null, List.of(Path.of("PaRecPR1925492_20260430_203000.wav")));

        assertThat(AvertissementIncoherence.rediger(c))
                .contains("la date du journal (22/04/2026)")
                .contains("30/04/2026")
                .doesNotContain("la série déclarée");
    }

    @Test
    @DisplayName("Série ET date incohérentes : les deux constats sont reliés par « et »")
    void serie_et_date() {
        AnalyseCoherence c = AnalyseCoherence.depuis(
                journal("1925492", NUIT), null, List.of(Path.of("PaRecPR1648011_20260430_203000.wav")));

        assertThat(AvertissementIncoherence.rediger(c)).contains("série 1648011) et la date du journal");
    }

    private static JournalParse journal(String serie, LocalDate date) {
        return new JournalParse(serie, null, date, null, null, null, null, null, true, null, List.of(), List.of());
    }
}

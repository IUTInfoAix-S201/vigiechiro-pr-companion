package fr.univ_amu.iut.importation.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.importation.model.AnalyseMelange;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Tests de la mise en phrase de l'avertissement **« mélange »** (#33). La détection elle-même est
/// couverte par `AnalyseMelangeTest` ; ici on vérifie les trois branches du libellé (enregistreurs
/// seuls, nuits seules, les deux) et le cas homogène (chaîne vide).
class AvertissementMelangeTest {

    @Test
    @DisplayName("Dossier homogène (une nuit, un enregistreur) : aucun avertissement")
    void homogene_pas_d_avertissement() {
        AnalyseMelange analyse = AnalyseMelange.depuis(
                List.of(Path.of("PaRecPR1925492_20260422_203000.wav"), Path.of("PaRecPR1925492_20260422_233000.wav")));

        assertThat(AvertissementMelange.rediger(analyse)).isEmpty();
    }

    @Test
    @DisplayName("Plusieurs enregistreurs seuls : le message cite les séries, pas les nuits")
    void enregistreurs_seuls() {
        AnalyseMelange analyse = AnalyseMelange.depuis(
                List.of(Path.of("PaRecPR1925492_20260422_203000.wav"), Path.of("PaRecPR1648011_20260422_203000.wav")));

        assertThat(AvertissementMelange.rediger(analyse))
                .contains("plusieurs enregistreurs (séries 1648011, 1925492)")
                .doesNotContain("plusieurs nuits");
    }

    @Test
    @DisplayName("Plusieurs nuits seules : le message cite le nombre de dates, pas les séries")
    void nuits_seules() {
        AnalyseMelange analyse = AnalyseMelange.depuis(List.of(
                Path.of("PaRecPR1925492_20260422_203000.wav"),
                Path.of("PaRecPR1925492_20260423_203000.wav"),
                Path.of("PaRecPR1925492_20260424_203000.wav")));

        assertThat(AvertissementMelange.rediger(analyse))
                .contains("plusieurs nuits (3 dates d'acquisition)")
                .doesNotContain("plusieurs enregistreurs");
    }

    @Test
    @DisplayName("Mélange complet : les deux constats sont reliés par « et »")
    void enregistreurs_et_nuits() {
        AnalyseMelange analyse = AnalyseMelange.depuis(List.of(
                Path.of("PaRecPR1925492_20260422_203000.wav"),
                Path.of("PaRecPR1648011_20260423_203000.wav"),
                Path.of("PaRecPR1648011_20260424_203000.wav")));

        assertThat(AvertissementMelange.rediger(analyse))
                .contains("plusieurs enregistreurs (séries 1648011, 1925492) et plusieurs nuits");
    }
}

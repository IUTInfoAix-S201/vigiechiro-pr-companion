package fr.univ_amu.iut.passage.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Dérivation de la [DisponibiliteAudio] depuis le décompte : les trois états, dont les bords
/// (0/0 : passage sans séquence persistée, donc rien à écouter).
class DecompteAudioTest {

    @Test
    @DisplayName("Toutes les séquences présentes : COMPLETE")
    void toutes_presentes() {
        assertThat(new DecompteAudio(3, 3).disponibilite()).isEqualTo(DisponibiliteAudio.COMPLETE);
    }

    @Test
    @DisplayName("Une partie seulement : PARTIELLE")
    void une_partie() {
        assertThat(new DecompteAudio(2, 3).disponibilite()).isEqualTo(DisponibiliteAudio.PARTIELLE);
    }

    @Test
    @DisplayName("Aucune présente : ABSENTE")
    void aucune() {
        assertThat(new DecompteAudio(0, 3).disponibilite()).isEqualTo(DisponibiliteAudio.ABSENTE);
    }

    @Test
    @DisplayName("Aucune séquence persistée (0/0) : ABSENTE, rien à écouter")
    void sans_sequence_persistee() {
        assertThat(new DecompteAudio(0, 0).disponibilite()).isEqualTo(DisponibiliteAudio.ABSENTE);
    }

    @Test
    @DisplayName("Décompte incohérent (présentes > total ou négatif) : rejeté")
    void decompte_incoherent_rejete() {
        assertThatIllegalArgumentException().isThrownBy(() -> new DecompteAudio(4, 3));
        assertThatIllegalArgumentException().isThrownBy(() -> new DecompteAudio(-1, 3));
        assertThatIllegalArgumentException().isThrownBy(() -> new DecompteAudio(0, -1));
    }
}

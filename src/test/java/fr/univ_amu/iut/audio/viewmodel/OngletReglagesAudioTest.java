package fr.univ_amu.iut.audio.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.commun.view.DescripteurReglage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Onglet « Audio » de l'écran Réglages (#1006) : ses descripteurs pointent les clés/défauts partagés
/// avec les options du menu ☰ de la vue audio (`LecteurAudio`, qui référence les mêmes constantes) —
/// impossible de dériver. On vérifie clés, défauts et types.
class OngletReglagesAudioTest {

    @Test
    @DisplayName("l'onglet Audio déclare lecture-auto (défaut vrai) et boucle (défaut faux)")
    void declare_les_deux_preferences_de_lecture() {
        OngletReglagesAudio onglet = new OngletReglagesAudio();

        assertThat(onglet.idFeature()).isEqualTo("audio");
        assertThat(onglet.reglages())
                .extracting(DescripteurReglage::cle)
                .containsExactly(OngletReglagesAudio.CLE_LECTURE_AUTO, OngletReglagesAudio.CLE_BOUCLE);

        assertThat(onglet.reglages())
                .allSatisfy(descripteur -> assertThat(descripteur).isInstanceOf(DescripteurReglage.Booleen.class));

        DescripteurReglage.Booleen lectureAuto =
                (DescripteurReglage.Booleen) onglet.reglages().get(0);
        DescripteurReglage.Booleen boucle =
                (DescripteurReglage.Booleen) onglet.reglages().get(1);
        assertThat(lectureAuto.defaut())
                .isEqualTo(OngletReglagesAudio.DEFAUT_LECTURE_AUTO)
                .isTrue();
        assertThat(boucle.defaut()).isEqualTo(OngletReglagesAudio.DEFAUT_BOUCLE).isFalse();
    }
}

package fr.univ_amu.iut.lot.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.univ_amu.iut.commun.model.RegleMetierException;
import fr.univ_amu.iut.commun.model.StatutWorkflow;
import java.nio.file.Path;
import java.util.List;
import java.util.function.ToLongFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// [ChoixSourceDepot] (#1996) : la bascule ZIP / WAV, et surtout **le seuil qui la décide**.
///
/// Le test vit dans le paquet du modèle parce que la classe y est confinée : la politique de dépôt n'a
/// pas à s'exposer hors de la feature.
class ChoixSourceDepotTest {

    private static final long PLAFOND = 700L * 1000 * 1000;
    private static final String DOSSIER = "/ws/session-42";

    /// 10 Go de séquences : la génération complète en demanderait ~6,1 Go, le pipeline ~1,5 Go.
    private static final long VOLUME_SOURCE = 10L * 1000 * 1000 * 1000;

    @Test
    @DisplayName("#1996 : un disque trop petit pour tout le lot mais assez pour la fenêtre reste en ZIP")
    void fenetre_suffit_pour_rester_en_zip() {
        // C'est LE changement du lot : avant, ce disque déclenchait le repli WAV, alors que le pipeline
        // n'a jamais besoin que de deux archives à la fois.
        long disponible = 2 * PLAFOND + 200L * 1000 * 1000; // au-dessus de la fenêtre, loin sous le total

        assertThat(choix(disponible).disquePermetArchives(lot())).isTrue();
    }

    @Test
    @DisplayName("un disque trop petit même pour la fenêtre bascule en WAV")
    void sous_la_fenetre_bascule_en_wav() {
        long disponible = PLAFOND; // même pas deux archives

        assertThat(choix(disponible).disquePermetArchives(lot())).isFalse();
    }

    @Test
    @DisplayName("sur un petit lot, c'est son propre volume qui décide, pas la fenêtre")
    void petit_lot_dimensionne_par_son_volume() {
        // Une nuit de 150 Mo : exiger 1,5 Go de fenêtre la refuserait à tort. On retient le minimum des
        // deux seuils.
        EtatLot petit = lot(150L * 1000 * 1000);
        long disponible = 300L * 1000 * 1000;

        assertThat(choix(disponible).disquePermetArchives(petit)).isTrue();
    }

    @Test
    @DisplayName("disque illisible ou volume inconnu : repli WAV assumé, jamais de pari")
    void etat_inconnu_bascule_en_wav() {
        assertThat(choix(0).disquePermetArchives(lot())).as("espace illisible").isFalse();
        assertThat(choix(Long.MAX_VALUE).disquePermetArchives(lot(null)))
                .as("volume inconnu")
                .isFalse();
        assertThat(choix(Long.MAX_VALUE).disquePermetArchives(lotSansDossier()))
                .as("pas de dossier de session")
                .isFalse();
    }

    @Test
    @DisplayName("#1997 : le mode WAV choisi dépose les séquences, quel que soit l'état du disque")
    void mode_wav_choisi_depose_les_sequences() {
        ChoixSourceDepot choix = new ChoixSourceDepot(
                new RepertoireDepot(),
                () -> new CompacteurDepot(PLAFOND),
                () -> ModeDepot.SEQUENCES_WAV,
                dossier -> Long.MAX_VALUE);

        SourceDepot source = choix.pour(lot(), List.of(Path.of("/ws/a.wav")), Path.of("/ws/session-42"));

        assertThat(source.identifiants()).containsExactly("a.wav");
    }

    @Test
    @DisplayName("#1997 : le mode ZIP choisi mais impossible REFUSE, il ne bascule pas en WAV en douce")
    void mode_zip_impossible_refuse() {
        // Avant #1997, un disque trop petit basculait silencieusement en WAV. Ce repli changeait ce qu'il
        // advient de l'audio côté serveur (#1244) sans que l'utilisateur l'ait voulu : maintenant qu'il
        // a choisi, on lui dit que ça ne passe pas et on lui donne les deux issues.
        ChoixSourceDepot choix = new ChoixSourceDepot(
                new RepertoireDepot(), () -> new CompacteurDepot(PLAFOND), () -> ModeDepot.ARCHIVES_ZIP, dossier -> 1L);

        assertThatThrownBy(() -> choix.pour(lot(), List.of(Path.of("/ws/a.wav")), Path.of("/ws/session-42")))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("Espace disque insuffisant")
                .hasMessageContaining("Séquences WAV");
    }

    @Test
    @DisplayName("un réglage absent ou corrompu retombe sur les archives ZIP, sans empêcher de déposer")
    void reglage_illisible_retombe_sur_zip() {
        assertThat(ModeDepot.parValeur(null)).isEqualTo(ModeDepot.ARCHIVES_ZIP);
        assertThat(ModeDepot.parValeur("")).isEqualTo(ModeDepot.ARCHIVES_ZIP);
        assertThat(ModeDepot.parValeur("n-importe-quoi")).isEqualTo(ModeDepot.ARCHIVES_ZIP);
        assertThat(ModeDepot.parValeur("wav")).isEqualTo(ModeDepot.SEQUENCES_WAV);
    }

    private static ChoixSourceDepot choix(long disponible) {
        ToLongFunction<String> espace = dossier -> disponible;
        return new ChoixSourceDepot(
                new RepertoireDepot(), () -> new CompacteurDepot(PLAFOND), () -> ModeDepot.ARCHIVES_ZIP, espace);
    }

    private static EtatLot lot() {
        return lot(VOLUME_SOURCE);
    }

    private static EtatLot lot(Long volumeOctets) {
        return new EtatLot(StatutWorkflow.PRET_A_DEPOSER, DOSSIER, 1000, volumeOctets, List.of(), null);
    }

    private static EtatLot lotSansDossier() {
        return new EtatLot(StatutWorkflow.PRET_A_DEPOSER, null, 1000, VOLUME_SOURCE, List.of(), null);
    }
}

package fr.univ_amu.iut.lot;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.lot.model.CompacteurDepot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Les **deux seuils** d'espace disque du dépôt (#1996), et pourquoi ils diffèrent.
///
/// Générer (étape ②) écrit tout le lot d'un coup : son seuil reste le volume total. Déposer passe par
/// un pipeline qui ne matérialise jamais plus que sa fenêtre : son seuil est celui de la fenêtre.
class SeuilDisqueDepotTest {

    private static final long PLAFOND = 700L * 1000 * 1000;
    private static final long MARGE = 100L * 1000 * 1000;

    @Test
    @DisplayName("le seuil du pipeline vaut la fenêtre au plafond, plus la marge")
    void seuil_du_pipeline() {
        assertThat(new CompacteurDepot(PLAFOND).espaceRequisPourLaFenetre()).isEqualTo(2 * PLAFOND + MARGE);
    }

    @Test
    @DisplayName("le seuil du pipeline suit le plafond configuré")
    void seuil_suit_le_plafond() {
        long petitPlafond = 100L * 1000 * 1000;

        assertThat(new CompacteurDepot(petitPlafond).espaceRequisPourLaFenetre())
                .isEqualTo(2 * petitPlafond + MARGE)
                .isLessThan(new CompacteurDepot(PLAFOND).espaceRequisPourLaFenetre());
    }

    @Test
    @DisplayName("sur un gros lot, le pipeline exige bien moins que la génération complète")
    void pipeline_exige_moins_que_la_generation() {
        // 50 Go de séquences : la génération complète en demande ~30, le pipeline ~1,5.
        long volumeSource = 50L * 1000 * 1000 * 1000;

        long pourGenererTout = CompacteurDepot.estimationTailleDepot(volumeSource);
        long pourDeposer = new CompacteurDepot(PLAFOND).espaceRequisPourLaFenetre();

        assertThat(pourDeposer).isLessThan(pourGenererTout / 10);
    }

    @Test
    @DisplayName("sur un petit lot, le seuil de génération reste le plus bas des deux")
    void petit_lot_reste_dimensionne_par_son_volume() {
        // Une nuit qui tient dans une archive : inutile d'exiger la fenêtre entière. C'est le minimum
        // des deux que retient ChoixSourceDepot.
        long petitVolume = 150L * 1000 * 1000;

        assertThat(CompacteurDepot.estimationTailleDepot(petitVolume))
                .isLessThan(new CompacteurDepot(PLAFOND).espaceRequisPourLaFenetre());
    }
}

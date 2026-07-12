package fr.univ_amu.iut.lot.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.commun.view.DescripteurReglage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Onglet « Dépôt » de l’écran Réglages (#1047) : le descripteur pointe la clé lue par `LotModule`
/// pour le plafond des archives — impossible de dériver. On vérifie clé, défaut, bornes et type.
class OngletReglagesDepotTest {

    @Test
    @DisplayName("l'onglet Dépôt déclare le plafond d'archive (700 Mo par défaut, borné [50, 700])")
    void declare_le_plafond() {
        OngletReglagesDepot onglet = new OngletReglagesDepot();

        assertThat(onglet.idFeature()).isEqualTo("lot");
        assertThat(onglet.titre()).isEqualTo("Dépôt");
        assertThat(onglet.reglages()).hasSize(1);
        DescripteurReglage.Entier plafond =
                (DescripteurReglage.Entier) onglet.reglages().getFirst();
        assertThat(plafond.cle()).isEqualTo(OngletReglagesDepot.CLE_TAILLE_MAX);
        assertThat(plafond.defaut()).isEqualTo(OngletReglagesDepot.DEFAUT_TAILLE_MAX_MO);
        assertThat(plafond.min()).isEqualTo(50);
        assertThat(plafond.max()).isEqualTo(700);
    }
}

package fr.univ_amu.iut.sites.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.univ_amu.iut.sites.model.dao.PointDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/// Vérifie que le compteur de points d'écoute délègue au DAO (sans toucher la base).
@ExtendWith(MockitoExtension.class)
class IndicateurPointsTest {

    @Mock
    private PointDao pointDao;

    @Test
    @DisplayName("valeur() délègue à PointDao.compter() ; libellé et ordre attendus")
    void valeur_delegue_au_dao() {
        when(pointDao.compter()).thenReturn(42L);
        IndicateurPoints indicateur = new IndicateurPoints(pointDao);

        assertThat(indicateur.valeur()).isEqualTo(42L);
        assertThat(indicateur.libelle()).isEqualTo("Points d'écoute");
        assertThat(indicateur.ordre()).isEqualTo(20);
    }
}

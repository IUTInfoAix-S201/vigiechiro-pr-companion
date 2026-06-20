package fr.univ_amu.iut.sites.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.univ_amu.iut.sites.model.dao.SiteDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/// Vérifie que le compteur d'accueil délègue son comptage au DAO (sans toucher la base).
@ExtendWith(MockitoExtension.class)
class IndicateurSitesTest {

    @Mock
    private SiteDao siteDao;

    @Test
    @DisplayName("valeur() délègue à SiteDao.compter() ; libellé et ordre attendus")
    void valeur_delegue_au_dao() {
        when(siteDao.compter()).thenReturn(7L);
        IndicateurSites indicateur = new IndicateurSites(siteDao);

        assertThat(indicateur.valeur()).isEqualTo(7L);
        assertThat(indicateur.libelle()).isEqualTo("Sites");
        assertThat(indicateur.ordre()).isEqualTo(10);
    }
}

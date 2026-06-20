package fr.univ_amu.iut.sites.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.univ_amu.iut.sites.model.ServiceSites;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/// Vérifie que le compteur d'accueil délègue son comptage au service (sans toucher la base).
@ExtendWith(MockitoExtension.class)
class IndicateurSitesTest {

    @Mock
    private ServiceSites serviceSites;

    @Test
    @DisplayName("valeur() délègue à ServiceSites.compterSites() ; libellé et ordre attendus")
    void valeur_delegue_au_service() {
        when(serviceSites.compterSites()).thenReturn(7L);
        IndicateurSites indicateur = new IndicateurSites(serviceSites);

        assertThat(indicateur.valeur()).isEqualTo(7L);
        assertThat(indicateur.libelle()).isEqualTo("Sites");
        assertThat(indicateur.ordre()).isEqualTo(10);
    }
}

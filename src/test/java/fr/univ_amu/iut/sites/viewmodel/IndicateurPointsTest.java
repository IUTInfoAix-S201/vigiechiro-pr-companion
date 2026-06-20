package fr.univ_amu.iut.sites.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.univ_amu.iut.sites.model.ServiceSites;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/// Vérifie que le compteur de points d'écoute délègue au service (sans toucher la base).
@ExtendWith(MockitoExtension.class)
class IndicateurPointsTest {

    @Mock
    private ServiceSites serviceSites;

    @Test
    @DisplayName("valeur() délègue à ServiceSites.compterPoints() ; libellé et ordre attendus")
    void valeur_delegue_au_service() {
        when(serviceSites.compterPoints()).thenReturn(42L);
        IndicateurPoints indicateur = new IndicateurPoints(serviceSites);

        assertThat(indicateur.valeur()).isEqualTo(42L);
        assertThat(indicateur.libelle()).isEqualTo("Points d'écoute");
        assertThat(indicateur.ordre()).isEqualTo(20);
    }
}

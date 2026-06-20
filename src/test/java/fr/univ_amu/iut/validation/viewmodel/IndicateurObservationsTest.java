package fr.univ_amu.iut.validation.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.univ_amu.iut.validation.model.ServiceValidation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/// Vérifie que le compteur d'observations délègue au service (sans toucher la base).
@ExtendWith(MockitoExtension.class)
class IndicateurObservationsTest {

    @Mock
    private ServiceValidation serviceValidation;

    @Test
    @DisplayName("valeur() délègue à ServiceValidation.compterObservations() ; libellé et ordre attendus")
    void valeur_delegue_au_service() {
        when(serviceValidation.compterObservations()).thenReturn(340L);
        IndicateurObservations indicateur = new IndicateurObservations(serviceValidation);

        assertThat(indicateur.valeur()).isEqualTo(340L);
        assertThat(indicateur.libelle()).isEqualTo("Observations");
        assertThat(indicateur.ordre()).isEqualTo(40);
    }
}

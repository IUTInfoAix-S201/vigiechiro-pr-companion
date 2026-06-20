package fr.univ_amu.iut.passage.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.univ_amu.iut.passage.model.ServicePassage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/// Vérifie que le compteur de passages délègue au service (sans toucher la base).
@ExtendWith(MockitoExtension.class)
class IndicateurPassagesTest {

    @Mock
    private ServicePassage servicePassage;

    @Test
    @DisplayName("valeur() délègue à ServicePassage.compterPassages() ; libellé et ordre attendus")
    void valeur_delegue_au_service() {
        when(servicePassage.compterPassages()).thenReturn(5L);
        IndicateurPassages indicateur = new IndicateurPassages(servicePassage);

        assertThat(indicateur.valeur()).isEqualTo(5L);
        assertThat(indicateur.libelle()).isEqualTo("Passages");
        assertThat(indicateur.ordre()).isEqualTo(30);
    }
}

package fr.univ_amu.iut.validation.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.univ_amu.iut.validation.model.dao.ObservationDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/// Vérifie que le compteur d'observations délègue au DAO (sans toucher la base).
@ExtendWith(MockitoExtension.class)
class IndicateurObservationsTest {

    @Mock
    private ObservationDao observationDao;

    @Test
    @DisplayName("valeur() délègue à ObservationDao.compter() ; libellé et ordre attendus")
    void valeur_delegue_au_dao() {
        when(observationDao.compter()).thenReturn(340L);
        IndicateurObservations indicateur = new IndicateurObservations(observationDao);

        assertThat(indicateur.valeur()).isEqualTo(340L);
        assertThat(indicateur.libelle()).isEqualTo("Observations");
        assertThat(indicateur.ordre()).isEqualTo(40);
    }
}

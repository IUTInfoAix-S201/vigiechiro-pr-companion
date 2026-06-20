package fr.univ_amu.iut.passage.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.univ_amu.iut.passage.model.dao.PassageDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/// Vérifie que le compteur de passages délègue au DAO (sans toucher la base).
@ExtendWith(MockitoExtension.class)
class IndicateurPassagesTest {

    @Mock
    private PassageDao passageDao;

    @Test
    @DisplayName("valeur() délègue à PassageDao.compter() ; libellé et ordre attendus")
    void valeur_delegue_au_dao() {
        when(passageDao.compter()).thenReturn(5L);
        IndicateurPassages indicateur = new IndicateurPassages(passageDao);

        assertThat(indicateur.valeur()).isEqualTo(5L);
        assertThat(indicateur.libelle()).isEqualTo("Passages");
        assertThat(indicateur.ordre()).isEqualTo(30);
    }
}

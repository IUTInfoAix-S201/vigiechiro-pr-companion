package fr.univ_amu.iut.importation.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.univ_amu.iut.commun.model.HorlogeFigee;
import fr.univ_amu.iut.commun.model.Protocole;
import fr.univ_amu.iut.sites.model.PointDEcoute;
import fr.univ_amu.iut.sites.model.ServiceSites;
import fr.univ_amu.iut.sites.model.Site;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/// Tests unitaires du sous-VM [RattachementImportViewModel] (étape 3 de M-Import), extrait de
/// [ImportationViewModel] (#183). [ServiceSites] est mocké ; aucune base de données.
@ExtendWith(MockitoExtension.class)
class RattachementImportViewModelTest {

    private static final String ID_USER = "u-1";
    private static final LocalDate JOUR = LocalDate.of(2026, 5, 31);
    private static final Site ETANG = new Site(1L, "640380", "Étang", Protocole.STANDARD, null, "2026-01-01", ID_USER);
    private static final PointDEcoute A1 = new PointDEcoute(10L, "A1", 43.5, 5.4, null, 1L);

    @Mock
    private ServiceSites serviceSites;

    private RattachementImportViewModel vm;

    @BeforeEach
    void preparer() {
        vm = new RattachementImportViewModel(serviceSites, new HorlogeFigee(JOUR), ID_USER);
    }

    @Test
    @DisplayName("État initial : année préremplie à l'horloge, n° passage 1, rattachement incomplet, aperçu vide")
    void etat_initial() {
        assertThat(vm.anneeProperty().get()).isEqualTo(2026);
        assertThat(vm.numeroPassageProperty().get()).isEqualTo(1);
        assertThat(vm.estComplet()).isFalse();
        assertThat(vm.apercuPrefixeProperty().get()).isEmpty();
    }

    @Test
    @DisplayName("chargerSites alimente la liste des sites depuis le service")
    void charger_sites() {
        when(serviceSites.listerSites(ID_USER)).thenReturn(List.of(ETANG));

        vm.chargerSites();

        assertThat(vm.sites()).containsExactly(ETANG);
    }

    @Test
    @DisplayName("Choisir un site recharge ses points et réinitialise le point sélectionné")
    void site_recharge_points() {
        when(serviceSites.listerPoints(1L)).thenReturn(List.of(A1));

        vm.siteSelectionneProperty().set(ETANG);

        assertThat(vm.points()).containsExactly(A1);
        assertThat(vm.pointSelectionneProperty().get()).isNull();
    }

    @Test
    @DisplayName("Rattachement complet : estComplet vrai, idPoint + préfixe + aperçu disponibles")
    void rattachement_complet() {
        when(serviceSites.listerPoints(1L)).thenReturn(List.of(A1));
        vm.siteSelectionneProperty().set(ETANG);
        vm.pointSelectionneProperty().set(A1);

        assertThat(vm.estComplet()).isTrue();
        assertThat(vm.idPointSelectionne()).isEqualTo(10L);
        assertThat(vm.prefixeCourant()).isNotNull();
        // L'aperçu se recalcule à chaque champ ; non vide dès que site + point sont choisis.
        assertThat(vm.apercuPrefixeProperty().get()).isNotBlank().contains("640380");
    }

    @Test
    @DisplayName("Un n° de passage < 1 rend le rattachement incomplet")
    void numero_passage_invalide() {
        when(serviceSites.listerPoints(1L)).thenReturn(List.of(A1));
        vm.siteSelectionneProperty().set(ETANG);
        vm.pointSelectionneProperty().set(A1);
        vm.numeroPassageProperty().set(0);

        assertThat(vm.estComplet()).isFalse();
    }
}

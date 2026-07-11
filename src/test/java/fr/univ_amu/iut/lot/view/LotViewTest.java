package fr.univ_amu.iut.lot.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import fr.univ_amu.iut.commun.model.StatutWorkflow;
import fr.univ_amu.iut.commun.view.NavigationDeTestModule;
import fr.univ_amu.iut.commun.view.OuvreurDeLien;
import fr.univ_amu.iut.commun.viewmodel.ContextePassage;
import fr.univ_amu.iut.commun.viewmodel.ContexteSite;
import fr.univ_amu.iut.commun.viewmodel.ZonesStatut;
import fr.univ_amu.iut.lot.model.DepotVigieChiro;
import fr.univ_amu.iut.lot.model.EtatLot;
import fr.univ_amu.iut.lot.model.ServiceLot;
import fr.univ_amu.iut.lot.viewmodel.DepotViewModel;
import fr.univ_amu.iut.lot.viewmodel.LotViewModel;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/// Test d'intégration TestFX de l'écran **M-Lot** : chargement du FXML via Guice (avec un
/// [ServiceLot] mocké), ouverture sur un passage Vérifié, vérification du câblage (statut, récap,
/// dossier, activation des actions) et délégation du clic « Préparer ». Pas de base de données.
@ExtendWith(ApplicationExtension.class)
class LotViewTest {

    private ServiceLot service;
    private LotController controleur;

    @Start
    void start(Stage stage) throws Exception {
        service = mock(ServiceLot.class);
        when(service.consulterLot(anyLong()))
                .thenReturn(new EtatLot(StatutWorkflow.VERIFIE, "/ws/session-42", 2, 8192L, List.of(), null));
        // Dépôt présent (mock) → le bouton « Téléverser sur Vigie-Chiro » est visible (#142).
        DepotVigieChiro depot = mock(DepotVigieChiro.class);
        Injector injector = Guice.createInjector(
                new AbstractModule() {
                    @Provides
                    LotViewModel viewModel() {
                        return new LotViewModel(service);
                    }

                    @Provides
                    DepotViewModel depotViewModel() {
                        return new DepotViewModel(service, Optional.of(depot));
                    }

                    @Provides
                    OuvreurDeLien ouvreurDeLien() {
                        return url -> {};
                    }
                },
                new NavigationDeTestModule());
        FXMLLoader loader = new FXMLLoader(LotController.class.getResource("Lot.fxml"));
        loader.setControllerFactory(injector::getInstance);
        Parent vue = loader.load();
        controleur = loader.getController();
        controleur.ouvrirSur(new ContextePassage(42L, 2, new ContexteSite("640380", "A1", "Étang de la Tuilière")));
        stage.setScene(new Scene(vue, 900, 640));
        stage.show();
    }

    @Test
    @DisplayName("Barre de statut : gauche = contexte, centre = statut + récap ; préparer actif, déposer désactivé")
    void affiche_etat_verifie(FxRobot robot) {
        Label chemin = robot.lookup("#lblCheminDepot").queryAs(Label.class);
        Button preparer = robot.lookup("#btnPreparer").queryAs(Button.class);
        Button deposer = robot.lookup("#btnDeposer").queryAs(Button.class);

        // Statut + récap déportés dans la barre de statut (#823) : identité à gauche, statut + récap au centre.
        ZonesStatut zones = controleur.zonesStatutProperty().get();
        assertThat(zones.gauche()).isEqualTo("Carré 640380 · A1 · N° 2");
        assertThat(zones.centre()).isEqualTo("Vérifié · 2 séquences · 8 Ko");
        assertThat(chemin.getText()).isEqualTo("/ws/session-42/depot");
        assertThat(preparer.isDisabled()).isFalse();
        assertThat(deposer.isDisabled()).isTrue();
    }

    @Test
    @DisplayName("« Préparer le lot » délègue au service")
    void preparer_delegue_au_service(FxRobot robot) {
        robot.clickOn("#btnPreparer");
        verify(service).preparerLot(42L);
    }

    @Test
    @DisplayName("#142 : le bouton « Téléverser sur Vigie-Chiro » est présent et visible (dépôt disponible)")
    void bouton_televerser_present(FxRobot robot) {
        Button televerser = robot.lookup("#btnTeleverser").queryAs(Button.class);

        assertThat(televerser.isVisible()).isTrue();
        assertThat(televerser.getText()).contains("Téléverser sur Vigie-Chiro");
    }
}

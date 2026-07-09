package fr.univ_amu.iut.connexion.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import fr.univ_amu.iut.commun.api.ClientVigieChiro;
import fr.univ_amu.iut.commun.model.Horloge;
import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.commun.view.OuvreurDeLien;
import fr.univ_amu.iut.connexion.model.StockageConnexion;
import fr.univ_amu.iut.connexion.viewmodel.ConnexionViewModel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/// Test d'intégration TestFX de la modale « Connexion VigieChiro » (#727/#741) : chargement du FXML via
/// Guice (client mocké, ouvreur de lien espionné, stockage sur un dossier temporaire vierge), état
/// initial « non connecté », ouverture de la plateforme (étape 1) et copie du marque-page (étape 2).
/// Pas de réseau.
@ExtendWith(ApplicationExtension.class)
class ConnexionModaleViewTest {

    private final AtomicReference<String> urlOuverte = new AtomicReference<>();

    @Start
    void start(Stage stage) throws Exception {
        Path workspace = Files.createTempDirectory("vc-connexion");
        StockageConnexion stockage = new StockageConnexion(new Workspace(workspace), Horloge.systeme());
        ClientVigieChiro client = mock(ClientVigieChiro.class);
        OuvreurDeLien ouvreur = urlOuverte::set;
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Provides
            ConnexionViewModel viewModel() {
                return new ConnexionViewModel(stockage, client);
            }

            @Provides
            OuvreurDeLien ouvreurDeLien() {
                return ouvreur;
            }
        });
        FXMLLoader loader = new FXMLLoader(ConnexionModaleController.class.getResource("ConnexionModale.fxml"));
        loader.setControllerFactory(injector::getInstance);
        Parent vue = loader.load();
        stage.setScene(new Scene(vue));
        stage.show();
    }

    @Test
    @DisplayName("État initial : « Non connecté », champ token présent, déconnexion désactivée")
    void etat_initial(FxRobot robot) {
        assertThat(robot.lookup("#labelIdentite").queryAs(Label.class).getText())
                .isEqualTo("Non connecté");
        assertThat(robot.lookup("#champToken").queryAs(TextField.class)).isNotNull();
        assertThat(robot.lookup("#boutonDeconnecter").queryAs(Button.class).isDisabled())
                .isTrue();
    }

    @Test
    @DisplayName("Étape 1 : « Ouvrir VigieChiro » ouvre la plateforme dans le navigateur")
    void ouvrir_site(FxRobot robot) {
        robot.clickOn("Ouvrir VigieChiro");

        assertThat(urlOuverte.get()).contains("vigiechiro");
    }

    @Test
    @DisplayName("Étape 2 : « Copier le marque-page » copie et affiche l'instruction d'installation")
    void copier_marque_page(FxRobot robot) {
        robot.clickOn("Copier le marque-page");

        assertThat(robot.lookup("#labelMessage").queryAs(Label.class).getText()).contains("Marque-page copié");
    }

    @Test
    @DisplayName("Étape 3 : se connecter sans token affiche une invite, sans appel réseau")
    void connecter_sans_token(FxRobot robot) {
        robot.clickOn("Se connecter");

        assertThat(robot.lookup("#labelMessage").queryAs(Label.class).getText()).contains("Collez d'abord");
    }
}

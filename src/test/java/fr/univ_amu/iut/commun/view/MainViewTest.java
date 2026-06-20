package fr.univ_amu.iut.commun.view;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Injector;
import fr.univ_amu.iut.App;
import fr.univ_amu.iut.commun.di.RacineInjecteur;
import fr.univ_amu.iut.commun.model.Protocole;
import fr.univ_amu.iut.commun.model.Utilisateur;
import fr.univ_amu.iut.commun.model.dao.UtilisateurDao;
import fr.univ_amu.iut.commun.persistence.MigrationSchema;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.commun.viewmodel.NavigationViewModel;
import fr.univ_amu.iut.sites.model.ServiceSites;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/// Test d'intégration TestFX du chrome (`MainView`) : l'affordance **« 🏠 Accueil »** est masquée
/// sur l'accueil, apparaît dès qu'une feature prend la main sur la zone centrale, et ramène à
/// l'accueil au clic (via le socle [Navigateur#afficherAccueil]). Couvre #22.
@ExtendWith(ApplicationExtension.class)
class MainViewTest {

    private Injector injector;
    private SourceDeDonnees source;
    private Navigateur navigateur;
    private NavigationViewModel navigation;

    @Start
    void start(Stage stage) throws Exception {
        Path workspace = Files.createTempDirectory("vc-main");
        System.setProperty("vigiechiro.workspace", workspace.toString());
        injector = RacineInjecteur.creer();
        source = injector.getInstance(SourceDeDonnees.class);
        new MigrationSchema(source).migrer();
        navigateur = injector.getInstance(Navigateur.class);
        navigation = injector.getInstance(NavigationViewModel.class);
        FXMLLoader loader = new FXMLLoader(App.class.getResource("commun/view/MainView.fxml"));
        loader.setControllerFactory(injector::getInstance);
        Parent racine = loader.load();
        stage.setScene(new Scene(racine, 1000, 700));
        stage.show();
    }

    @AfterEach
    void nettoyerWorkspace() {
        System.clearProperty("vigiechiro.workspace");
    }

    @Test
    @DisplayName("Le lien « Accueil » est masqué sur l'accueil et apparaît dans une feature")
    void lien_accueil_visible_hors_accueil(FxRobot robot) {
        Hyperlink lien = robot.lookup("#lienAccueil").queryAs(Hyperlink.class);
        assertThat(lien.isVisible()).isFalse();

        robot.interact(() -> navigateur.afficher(new Group(), "sites", "Mes sites de suivi"));

        assertThat(lien.isVisible()).isTrue();
    }

    @Test
    @DisplayName("Cliquer « Accueil » ramène à l'accueil et masque de nouveau le lien")
    void clic_accueil_revient_a_l_accueil(FxRobot robot) {
        robot.interact(() -> navigateur.afficher(new Group(), "sites", "Mes sites de suivi"));
        Hyperlink lien = robot.lookup("#lienAccueil").queryAs(Hyperlink.class);

        robot.interact(lien::fire);

        assertThat(lien.isVisible()).isFalse();
        assertThat(robot.lookup("#cartesActivites").tryQuery()).isPresent();
    }

    @Test
    @DisplayName("#54 : le lien « Accueil » est grisé quand la navigation est verrouillée")
    void lien_accueil_grise_si_navigation_verrouillee(FxRobot robot) {
        robot.interact(() -> navigateur.afficher(new Group(), "import", "Importer une nuit"));
        Hyperlink lien = robot.lookup("#lienAccueil").queryAs(Hyperlink.class);
        assertThat(lien.isDisabled()).isFalse();

        robot.interact(() -> navigation.setNavigationVerrouillee(true));
        assertThat(lien.isDisabled()).isTrue();

        robot.interact(() -> navigation.setNavigationVerrouillee(false));
        assertThat(lien.isDisabled()).isFalse();
    }

    @Test
    @DisplayName("Tableau de bord : le bandeau de compteurs est masqué quand la base est vide (#141)")
    void bandeau_masque_si_base_vide(FxRobot robot) {
        FlowPane bandeau = robot.lookup("#bandeauIndicateurs").queryAs(FlowPane.class);
        assertThat(bandeau.isVisible()).isFalse();
        assertThat(bandeau.getChildren()).isEmpty();
    }

    @Test
    @DisplayName("Tableau de bord : le bandeau affiche les compteurs après un retour sur l'accueil (#141)")
    void bandeau_affiche_compteurs_apres_donnees(FxRobot robot) {
        robot.interact(() -> {
            new UtilisateurDao(source).insert(new Utilisateur("u-1", "Testeur"));
            injector.getInstance(ServiceSites.class)
                    .creerSite("640380", "Étang de la Tuilière", Protocole.STANDARD, null, "u-1");
        });
        // On quitte l'accueil puis on y revient : le retour déclenche le recalcul des compteurs.
        robot.interact(() -> navigateur.afficher(new Group(), "sites", "Mes sites de suivi"));
        robot.interact(navigateur::afficherAccueil);

        FlowPane bandeau = robot.lookup("#bandeauIndicateurs").queryAs(FlowPane.class);
        assertThat(bandeau.isVisible()).isTrue();
        assertThat(robot.lookup(".indicateur-libelle").queryAllAs(Label.class))
                .extracting(Label::getText)
                .contains("Sites");
    }

    @Test
    @DisplayName("L'accueil affiche le hero nocturne et une carte (chip + chevron) par activité")
    void accueil_affiche_hero_et_cartes(FxRobot robot) {
        assertThat(robot.lookup(".hero-nocturne").tryQuery()).isPresent();

        int cartes = robot.lookup(".carte-activite").queryAll().size();
        assertThat(cartes).isPositive();
        // Chaque carte porte exactement une pastille d'icône et un chevron d'invite.
        assertThat(robot.lookup(".carte-chip").queryAll()).hasSize(cartes);
        assertThat(robot.lookup(".carte-chevron").queryAll()).hasSize(cartes);
    }

    @Test
    @DisplayName("Tableau de bord : un compteur à zéro est atténué (classe indicateur-vide) (#141)")
    void compteur_a_zero_est_attenue(FxRobot robot) {
        robot.interact(() -> {
            new UtilisateurDao(source).insert(new Utilisateur("u-1", "Testeur"));
            injector.getInstance(ServiceSites.class)
                    .creerSite("640380", "Étang de la Tuilière", Protocole.STANDARD, null, "u-1");
        });
        robot.interact(() -> navigateur.afficher(new Group(), "sites", "Mes sites de suivi"));
        robot.interact(navigateur::afficherAccueil);

        // Sites = 1, mais Points / Passages / Observations restent à 0 : ces pastilles sont atténuées.
        assertThat(robot.lookup(".indicateur-vide").queryAll()).isNotEmpty();
    }
}

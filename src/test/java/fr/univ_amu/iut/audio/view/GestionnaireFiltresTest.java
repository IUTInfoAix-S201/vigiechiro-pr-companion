package fr.univ_amu.iut.audio.view;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.audio.viewmodel.FiltresAudio;
import fr.univ_amu.iut.validation.model.LigneObservationAudio;
import fr.univ_amu.iut.validation.model.StatutObservation;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/// Barre de filtres « à la Notion » (#470/#471) : ajouter/retirer une puce via « + Filtre », recherche
/// texte permanente, réinitialisation. Vérifie le câblage sur la [FilteredList] via [FiltresAudio].
@ExtendWith(ApplicationExtension.class)
class GestionnaireFiltresTest {

    private TextField recherche;
    private MenuButton menu;
    private FlowPane puces;
    private FilteredList<LigneObservationAudio> affichees;
    private GestionnaireFiltres gestionnaire;

    @Start
    void start(Stage stage) {
        recherche = new TextField();
        menu = new MenuButton("+ Filtre");
        puces = new FlowPane();
        ObservableList<LigneObservationAudio> source = FXCollections.observableArrayList(
                ligne(1, "Pippip", "PaRec_1.wav", StatutObservation.NON_TOUCHEE),
                ligne(2, "Nyclei", "PaRec_2.wav", StatutObservation.VALIDEE));
        affichees = new FilteredList<>(source);
        FiltresAudio filtres = new FiltresAudio(affichees, () -> {});
        gestionnaire = new GestionnaireFiltres(recherche, menu, puces, filtres, List.of(CriteresAudio.statut()));
        stage.setScene(new Scene(new VBox(recherche, menu, puces), 400, 200));
        stage.show();
    }

    @Test
    @DisplayName("« + Filtre » ajoute la puce Statut (À revoir par défaut), filtre, et vide le menu ; ✕ restaure")
    void ajouter_puis_retirer_la_puce_statut(FxRobot robot) {
        assertThat(affichees).hasSize(2);
        assertThat(menu.getItems()).extracting(MenuItem::getText).containsExactly("Statut");
        assertThat(puces.getChildren()).isEmpty();

        // Ajouter le filtre Statut → défaut À revoir → seule la ligne 1 reste ; menu vidé ; une puce.
        robot.interact(() -> menu.getItems().get(0).fire());
        assertThat(affichees).extracting(LigneObservationAudio::idObservation).containsExactly(1L);
        assertThat(menu.getItems()).isEmpty();
        assertThat(puces.getChildren()).hasSize(1);

        // Retirer la puce (✕) → tout revisible ; le critère revient au menu.
        robot.interact(() -> boutonRetirer().fire());
        assertThat(affichees).hasSize(2);
        assertThat(menu.getItems()).hasSize(1);
        assertThat(puces.getChildren()).isEmpty();
    }

    @Test
    @DisplayName("Recherche texte : filtre sur fichier / taxon (insensible casse/accents)")
    void recherche_texte(FxRobot robot) {
        robot.interact(() -> recherche.setText("nyclei"));
        assertThat(affichees).extracting(LigneObservationAudio::idObservation).containsExactly(2L);
        robot.interact(() -> recherche.clear());
        assertThat(affichees).hasSize(2);
    }

    @Test
    @DisplayName("reinitialiser efface la recherche et les puces (tout redevient visible)")
    void reinitialiser(FxRobot robot) {
        robot.interact(() -> {
            menu.getItems().get(0).fire(); // Statut = À revoir
            recherche.setText("zzz"); // ne matche rien
        });
        assertThat(affichees).isEmpty();

        robot.interact(() -> gestionnaire.reinitialiser());
        assertThat(affichees).hasSize(2);
        assertThat(puces.getChildren()).isEmpty();
        assertThat(recherche.getText()).isEmpty();
    }

    private Button boutonRetirer() {
        return (Button) puces.lookupAll(".puce-filtre-retirer").iterator().next();
    }

    private static LigneObservationAudio ligne(long id, String taxon, String fichier, StatutObservation statut) {
        return new LigneObservationAudio(
                id,
                10 + id,
                7L,
                1,
                "2026-06-20",
                "640380",
                "A1",
                "Site",
                taxon,
                0.9,
                null,
                null,
                statut,
                false,
                null,
                45,
                null,
                taxon,
                fichier,
                0.2,
                0.4);
    }
}

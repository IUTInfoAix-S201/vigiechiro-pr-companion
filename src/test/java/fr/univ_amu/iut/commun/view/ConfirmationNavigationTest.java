package fr.univ_amu.iut.commun.view;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.commun.model.Severite;
import fr.univ_amu.iut.commun.viewmodel.CompteRendu;
import fr.univ_amu.iut.commun.viewmodel.CompteRendu.Constat;
import fr.univ_amu.iut.commun.viewmodel.CompteRendu.Detail;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;

/// Le rendu d'un [Confirmateur] par défaut : ce qui distingue une confirmation **structurée** d'une
/// confirmation **texte** (#2060).
///
/// Une liste à puces aplatie dans une chaîne perd son alignement dès qu'une ligne dépasse la largeur du
/// dialogue - la continuation repart sous la puce suivante (défaut clos en #1987, recréé en #2050). La
/// parade n'est pas « mieux formater la chaîne » mais **ne plus en avoir** : le dialogue porte alors la
/// structure de [VueCompteRendu], un `Label` par détail dont le retrait est porté par le CSS.
@ExtendWith(ApplicationExtension.class)
class ConfirmationNavigationTest {

    private static final CompteRendu DOUBLON = new CompteRendu(
            "",
            "",
            List.of(new Constat(
                    "Cette nuit a déjà été importée : l'importer créera un nouveau passage.",
                    Severite.AVERTISSEMENT,
                    List.of(
                            Detail.de("n° 2 (2026) au carré 640380, point A1"),
                            Detail.de("n° 7 (2026) au carré 130711, point Z4")))),
            "Importer quand même comme nouveau passage ?");

    @Test
    @DisplayName("Un compte rendu se rend en STRUCTURE (VueCompteRendu), pas en chaîne aplatie")
    void compte_rendu_rendu_en_structure(FxRobot robot) {
        AtomicReference<DialogPane> pane = new AtomicReference<>();
        // L'Alert se construit sur le fil JavaFX ; on n'affiche pas (`showAndWait` figerait le test).
        robot.interact(
                () -> pane.set(new ConfirmationNavigation().dialogue(DOUBLON).getDialogPane()));

        // Le contenu est un nœud, PAS un texte : c'est ce qui rend le défaut de puces impossible à revenir.
        assertThat(pane.get().getContent())
                .as("un compte rendu rendu comme texte réintroduirait la chaîne à puces (#2060)")
                .isInstanceOf(VBox.class);
        VBox contenu = (VBox) pane.get().getContent();
        assertThat(contenu.getStyleClass()).contains(VueCompteRendu.CLASSE_RACINE);

        List<Label> details = contenu.getChildren().stream()
                .filter(Label.class::isInstance)
                .map(Label.class::cast)
                .filter(label -> label.getStyleClass().contains("compte-rendu-detail"))
                .toList();
        assertThat(details)
                .as("chaque passage est un détail sur sa propre ligne alignée")
                .hasSize(2);
    }
}

package fr.univ_amu.iut.audio.view;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.validation.model.MessageObservation;
import java.time.Instant;
import java.util.List;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

/// Le **fil de discussion** avec le validateur ([PanneauDiscussion], #1417).
///
/// Bâti comme un **nœud**, pas comme une fenêtre : c'est ce qui le rend vérifiable ici sans qu'un
/// `showAndWait()` fige le test (leçon #1013 / #1405). L'extension JavaFX suffit à instancier les
/// contrôles ; aucune scène n'est montée.
@ExtendWith(ApplicationExtension.class)
class PanneauDiscussionTest {

    private static final String MOI = "u-moi";

    @Test
    @DisplayName("#1417 : personne n'a écrit → le panneau reste FERMÉ (il ne vole pas de largeur au"
            + " spectrogramme pour ne rien dire)")
    void fil_vide_panneau_ferme() {
        PanneauDiscussion panneau = new PanneauDiscussion();

        panneau.afficher(List.of(), MOI);

        assertThat(panneau.racine().isVisible()).isFalse();
        assertThat(panneau.racine().isManaged())
                .as("non seulement invisible, mais non géré : il ne prend AUCUNE place")
                .isFalse();
    }

    @Test
    @DisplayName("#1417 : un fil existe → le panneau s'ouvre et dit QUI parle, dans l'ordre du serveur")
    void fil_affiche_qui_parle() {
        PanneauDiscussion panneau = new PanneauDiscussion();

        panneau.afficher(
                List.of(
                        new MessageObservation(
                                1L,
                                7L,
                                0,
                                "u-validateur",
                                "Médiane basse pour un Eptser, non ?",
                                Instant.parse("2026-07-11T21:04:00Z")),
                        new MessageObservation(2L, 7L, 1, MOI, "Je repasse le son.", null)),
                MOI);

        assertThat(panneau.racine().isVisible()).isTrue();
        assertThat(textes(panneau))
                .as("l'ordre du serveur ($push) est l'ordre chronologique : il est conservé tel quel")
                .containsSubsequence("Médiane basse pour un Eptser, non ?", "Je repasse le son.");
        assertThat(textes(panneau))
                .as("savoir qui parle est la moitié de l'information dans une discussion")
                .anyMatch(texte -> texte.startsWith("Le validateur"))
                .anyMatch(texte -> texte.startsWith("Vous"));
    }

    @Test
    @DisplayName("#1417 : changer de ligne pour une détection sans fil REFERME le panneau — pas de fil"
            + " fantôme de la ligne précédente")
    void changer_de_ligne_referme_le_panneau() {
        PanneauDiscussion panneau = new PanneauDiscussion();
        panneau.afficher(List.of(new MessageObservation(1L, 7L, 0, MOI, "Vu.", null)), MOI);
        assertThat(panneau.racine().isVisible()).isTrue();

        panneau.afficher(List.of(), MOI);

        assertThat(panneau.racine().isVisible()).isFalse();
        assertThat(textes(panneau))
                .as("le fil précédent est effacé : afficher la discussion d'une autre détection serait pire"
                        + " que de n'en afficher aucune")
                .isEmpty();
    }

    /// Tous les textes affichés dans le fil (entêtes et corps de messages confondus).
    private static List<String> textes(PanneauDiscussion panneau) {
        ScrollPane cadre = (ScrollPane) panneau.racine().getChildren().get(1);
        VBox messages = (VBox) cadre.getContent();
        return messages.getChildren().stream()
                .flatMap(bulle -> ((VBox) bulle).getChildren().stream())
                .map(noeud -> ((Label) noeud).getText())
                .toList();
    }
}

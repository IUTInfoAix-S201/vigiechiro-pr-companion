package fr.univ_amu.iut.audio.view;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Tests de la décision UI « un seul jeu par passage » partagée par les deux fronts d'import de « Sons &
/// validation » : sans jeu on importe directement (sans question) ; avec un jeu on ne remplace qu'après
/// confirmation, et la question nomme ce qui remplacera le jeu. Pure logique, sans JavaFX.
class DecisionRemplacementJeuTest {

    @Test
    @DisplayName("Pas de jeu existant : import direct (remplacer=false), sans poser de question")
    void pas_de_jeu_importe_directement_sans_question() {
        AtomicReference<String> question = new AtomicReference<>();
        Optional<Boolean> decision = DecisionRemplacementJeu.resoudre(
                false,
                message -> {
                    question.set(message);
                    return true;
                },
                "ce nouvel import");

        assertThat(decision).contains(false);
        assertThat(question.get())
                .as("aucune confirmation ne doit être demandée sans jeu existant")
                .isNull();
    }

    @Test
    @DisplayName("Jeu existant + confirmation acceptée : remplacer=true, question posée avec la provenance")
    void jeu_existant_confirme_remplace() {
        AtomicReference<String> question = new AtomicReference<>();
        Optional<Boolean> decision = DecisionRemplacementJeu.resoudre(
                true,
                message -> {
                    question.set(message);
                    return true;
                },
                "ceux de Vigie-Chiro");

        assertThat(decision).contains(true);
        assertThat(question.get()).contains("remplacer").contains("perdues").contains("ceux de Vigie-Chiro");
    }

    @Test
    @DisplayName("Jeu existant + confirmation refusée : aucune décision d'import (Optional vide)")
    void jeu_existant_refuse_n_importe_pas() {
        Optional<Boolean> decision = DecisionRemplacementJeu.resoudre(true, message -> false, "ce nouvel import");

        assertThat(decision).isEmpty();
    }
}

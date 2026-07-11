package fr.univ_amu.iut.commun.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Tests du socle « table de suivi par unité » (#946) : cycle de vie des [LigneSuivi] (en attente → en
/// cours → terminée / échec), ciblage par numéro, événements dans le désordre, numéro inconnu ignoré.
/// Purement observable (propriétés JavaFX) : aucun toolkit graphique requis. Le comportement spécialisé
/// (colonnes du dépôt) est couvert par `SuiviLignesArchivesTest`.
class SuiviLignesTest {

    @Test
    @DisplayName("remplacerLignes() pose des lignes « en attente », exposées dans l'ordre et non modifiables")
    void remplacer_pose_les_lignes_en_attente() {
        SuiviLignes<LigneSuivi> suivi = new SuiviLignes<>();
        suivi.remplacerLignes(List.of(new LigneSuivi(1), new LigneSuivi(2)));

        assertThat(suivi.lignes()).extracting(LigneSuivi::numero).containsExactly(1, 2);
        assertThat(suivi.lignes()).allSatisfy(l -> {
            assertThat(l.etatProperty().get()).isEqualTo(EtatUnite.EN_ATTENTE);
            assertThat(l.fractionProperty().get()).isEqualTo(0.0);
            assertThat(l.raisonEchecProperty().get()).isEmpty();
        });
        assertThatThrownBy(() -> suivi.lignes().add(new LigneSuivi(3)))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("le cycle démarrer → progresser → terminer fait passer la ligne en cours puis terminée")
    void cycle_de_vie_d_une_ligne() {
        SuiviLignes<LigneSuivi> suivi = new SuiviLignes<>();
        suivi.remplacerLignes(List.of(new LigneSuivi(1)));

        suivi.demarrer(1);
        assertThat(ligne(suivi, 1).etatProperty().get()).isEqualTo(EtatUnite.EN_COURS);

        suivi.progresser(1, 2, 4);
        assertThat(ligne(suivi, 1).fractionProperty().get()).isEqualTo(0.5);

        suivi.terminer(1);
        assertThat(ligne(suivi, 1).etatProperty().get()).isEqualTo(EtatUnite.TERMINEE);
        assertThat(ligne(suivi, 1).fractionProperty().get()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("la fraction intra-unité reste monotone (un point en retard ne fait pas reculer la barre)")
    void fraction_monotone() {
        SuiviLignes<LigneSuivi> suivi = new SuiviLignes<>();
        suivi.remplacerLignes(List.of(new LigneSuivi(1)));

        suivi.progresser(1, 3, 4);
        suivi.progresser(1, 2, 4); // point en retard
        assertThat(ligne(suivi, 1).fractionProperty().get()).isEqualTo(0.75);
    }

    @Test
    @DisplayName("les événements ciblent la bonne ligne par numéro, même arrivés dans le désordre")
    void evenements_desordonnes_ciblent_par_numero() {
        SuiviLignes<LigneSuivi> suivi = new SuiviLignes<>();
        suivi.remplacerLignes(List.of(new LigneSuivi(1), new LigneSuivi(2), new LigneSuivi(3)));

        // L'unité 3 démarre et finit avant l'unité 1 (travail parallèle).
        suivi.demarrer(3);
        suivi.terminer(3);
        suivi.demarrer(1);

        assertThat(ligne(suivi, 3).etatProperty().get()).isEqualTo(EtatUnite.TERMINEE);
        assertThat(ligne(suivi, 1).etatProperty().get()).isEqualTo(EtatUnite.EN_COURS);
        assertThat(ligne(suivi, 2).etatProperty().get()).isEqualTo(EtatUnite.EN_ATTENTE); // pas encore touchée
    }

    @Test
    @DisplayName("echouer() passe la ligne « échec » et retient la raison ; un numéro inconnu est ignoré")
    void echec_et_numero_inconnu() {
        SuiviLignes<LigneSuivi> suivi = new SuiviLignes<>();
        suivi.remplacerLignes(List.of(new LigneSuivi(1)));

        suivi.echouer(1, "disque plein");
        assertThat(ligne(suivi, 1).etatProperty().get()).isEqualTo(EtatUnite.ECHEC);
        assertThat(ligne(suivi, 1).raisonEchecProperty().get()).isEqualTo("disque plein");

        // Numéro inexistant : aucun effet, aucune exception. Total nul : progression ignorée.
        suivi.demarrer(99);
        suivi.progresser(99, 1, 2);
        suivi.echouer(99, "x");
        suivi.progresser(1, 1, 0);
        assertThat(suivi.lignes()).hasSize(1);
    }

    @Test
    @DisplayName("reinitialiser() vide la table")
    void reinitialiser_vide_la_table() {
        SuiviLignes<LigneSuivi> suivi = new SuiviLignes<>();
        suivi.remplacerLignes(List.of(new LigneSuivi(1)));
        suivi.reinitialiser();
        assertThat(suivi.lignes()).isEmpty();
    }

    private static LigneSuivi ligne(SuiviLignes<LigneSuivi> suivi, int numero) {
        return suivi.lignes().stream()
                .filter(l -> l.numero() == numero)
                .findFirst()
                .orElseThrow();
    }
}

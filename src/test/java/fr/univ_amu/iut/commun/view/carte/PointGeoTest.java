package fr.univ_amu.iut.commun.view.carte;

import static org.assertj.core.api.Assertions.assertThat;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Vérifie le **nom court** affiché sur la carte ([PointGeo#nom()]) : abréviation d'affichage, sans
/// toucher au [PointGeo#libelle()] qui reste la clé d'identification (édition des positions, accessibilité).
class PointGeoTest {

    @Test
    @DisplayName("nom() = la partie après le dernier « / » du libellé (carré / point → point)")
    void nom_garde_la_partie_apres_le_slash() {
        PointGeo point = new PointGeo("640380 / A1", 43.40, -1.57, Color.GREEN);

        assertThat(point.nom()).isEqualTo("A1");
        assertThat(point.libelle())
                .as("le libellé complet reste intact (clé d'édition)")
                .isEqualTo("640380 / A1");
    }

    @Test
    @DisplayName("nom() = le libellé entier quand il ne contient pas de « / »")
    void nom_inchange_sans_slash() {
        assertThat(new PointGeo("Z1", 43.40, -1.57, Color.GREEN).nom()).isEqualTo("Z1");
    }
}

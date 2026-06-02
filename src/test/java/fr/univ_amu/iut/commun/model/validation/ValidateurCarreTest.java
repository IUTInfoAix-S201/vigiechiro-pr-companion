package fr.univ_amu.iut.commun.model.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Tests de la règle R1 : numéro de carré = exactement 6 chiffres, zéro de tête obligatoire.
class ValidateurCarreTest {

    @Test
    @DisplayName("R1 : six chiffres exactement sont valides")
    void six_chiffres_valides() {
        assertThat(ValidateurCarre.estValide("040962")).isTrue();
        assertThat(ValidateurCarre.estValide("640380")).isTrue();
        assertThat(ValidateurCarre.estValide("950001")).isTrue();
    }

    @Test
    @DisplayName("R1 : un carré sans zéro de tête (5 chiffres) est rejeté")
    void zero_de_tete_obligatoire() {
        assertThat(ValidateurCarre.estValide("40962"))
                .as("5 chiffres : le zéro de tête du département est manquant")
                .isFalse();
    }

    @Test
    @DisplayName("R1 : longueur incorrecte ou caractères non numériques rejetés")
    void longueur_et_caracteres_invalides() {
        assertThat(ValidateurCarre.estValide("0409620")).as("7 chiffres").isFalse();
        assertThat(ValidateurCarre.estValide("04096")).as("5 chiffres").isFalse();
        assertThat(ValidateurCarre.estValide("04096a")).as("lettre interdite").isFalse();
        assertThat(ValidateurCarre.estValide("04 962")).as("espace interdit").isFalse();
        assertThat(ValidateurCarre.estValide("")).isFalse();
        assertThat(ValidateurCarre.estValide(null)).isFalse();
    }

    @Test
    @DisplayName("exigerValide renvoie la valeur quand elle est conforme")
    void exiger_valide_renvoie_la_valeur() {
        assertThat(ValidateurCarre.exigerValide("040962")).isEqualTo("040962");
    }

    @Test
    @DisplayName("exigerValide lève une exception quand la règle R1 est violée")
    void exiger_valide_leve_si_invalide() {
        assertThatThrownBy(() -> ValidateurCarre.exigerValide("40962"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("R1");
    }
}

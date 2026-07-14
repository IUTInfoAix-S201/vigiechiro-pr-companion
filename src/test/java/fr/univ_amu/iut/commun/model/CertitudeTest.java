package fr.univ_amu.iut.commun.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Certitude observateur ([Certitude], #1139) : jetons alignés sur l'énumération serveur
/// `observateur_probabilite` (contrat #1203), lecture tolérante (absent/inconnu → non renseignée).
class CertitudeTest {

    @Test
    @DisplayName("depuisTexte : jetons serveur exacts, insensibles à la casse et aux espaces de bordure")
    void lit_les_jetons_serveur() {
        assertThat(Certitude.depuisTexte("SUR")).isEqualTo(Certitude.SUR);
        assertThat(Certitude.depuisTexte("probable")).isEqualTo(Certitude.PROBABLE);
        assertThat(Certitude.depuisTexte(" Possible ")).isEqualTo(Certitude.POSSIBLE);
    }

    @Test
    @DisplayName("depuisTexte : absent, vide, inconnu ou numérique → null (non renseignée, tolérant)")
    void tolere_les_valeurs_hors_domaine() {
        assertThat(Certitude.depuisTexte(null)).isNull();
        assertThat(Certitude.depuisTexte("  ")).isNull();
        assertThat(Certitude.depuisTexte("CERTAIN")).isNull();
        assertThat(Certitude.depuisTexte("0.85")).isNull();
    }

    @Test
    @DisplayName("jeton : la valeur poussée à l'API et persistée est le name() exact")
    void jeton_est_le_name() {
        assertThat(Certitude.SUR.jeton()).isEqualTo("SUR");
        assertThat(Certitude.PROBABLE.jeton()).isEqualTo("PROBABLE");
        assertThat(Certitude.POSSIBLE.jeton()).isEqualTo("POSSIBLE");
    }

    @Test
    @DisplayName("libelle : forme lisible pour l'IHM")
    void libelle_lisible() {
        assertThat(Certitude.SUR.libelle()).isEqualTo("Sûr");
        assertThat(Certitude.PROBABLE.libelle()).isEqualTo("Probable");
        assertThat(Certitude.POSSIBLE.libelle()).isEqualTo("Possible");
    }
}

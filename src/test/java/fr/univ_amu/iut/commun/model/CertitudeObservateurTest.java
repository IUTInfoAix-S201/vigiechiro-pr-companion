package fr.univ_amu.iut.commun.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Certitude observateur ([CertitudeObservateur], #1139) : jetons alignés sur l'énumération serveur
/// `observateur_probabilite` (contrat #1203), lecture tolérante (absent/inconnu → non renseignée).
class CertitudeObservateurTest {

    @Test
    @DisplayName("depuisTexte : jetons serveur exacts, insensibles à la casse et aux espaces de bordure")
    void lit_les_jetons_serveur() {
        assertThat(CertitudeObservateur.depuisTexte("SUR")).isEqualTo(CertitudeObservateur.SUR);
        assertThat(CertitudeObservateur.depuisTexte("probable")).isEqualTo(CertitudeObservateur.PROBABLE);
        assertThat(CertitudeObservateur.depuisTexte(" Possible ")).isEqualTo(CertitudeObservateur.POSSIBLE);
    }

    @Test
    @DisplayName("depuisTexte : absent, vide, inconnu ou numérique → null (non renseignée, tolérant)")
    void tolere_les_valeurs_hors_domaine() {
        assertThat(CertitudeObservateur.depuisTexte(null)).isNull();
        assertThat(CertitudeObservateur.depuisTexte("  ")).isNull();
        assertThat(CertitudeObservateur.depuisTexte("CERTAIN")).isNull();
        assertThat(CertitudeObservateur.depuisTexte("0.85")).isNull();
    }

    @Test
    @DisplayName("jeton : la valeur poussée à l'API et persistée est le name() exact")
    void jeton_est_le_name() {
        assertThat(CertitudeObservateur.SUR.jeton()).isEqualTo("SUR");
        assertThat(CertitudeObservateur.PROBABLE.jeton()).isEqualTo("PROBABLE");
        assertThat(CertitudeObservateur.POSSIBLE.jeton()).isEqualTo("POSSIBLE");
    }

    @Test
    @DisplayName("libelle : forme lisible pour l'IHM")
    void libelle_lisible() {
        assertThat(CertitudeObservateur.SUR.libelle()).isEqualTo("Sûr");
        assertThat(CertitudeObservateur.PROBABLE.libelle()).isEqualTo("Probable");
        assertThat(CertitudeObservateur.POSSIBLE.libelle()).isEqualTo("Possible");
    }
}

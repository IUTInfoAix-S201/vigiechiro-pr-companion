package fr.univ_amu.iut.multisite;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.commun.model.StatutWorkflow;
import fr.univ_amu.iut.commun.model.Verdict;
import fr.univ_amu.iut.multisite.model.FiltresMultisite;
import fr.univ_amu.iut.multisite.model.FiltresMultisiteJson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Tests purs (sans base ni mock) de la sérialisation aller-retour des filtres vers
/// `filters_json`. Vérifie le format JSON émis, l'aller-retour fidèle (y compris avec libellés
/// accentués) et l'omission des critères `null`.
class FiltresMultisiteJsonTest {

  @Test
  @DisplayName("Sérialise uniquement les critères renseignés, dans un ordre stable")
  void serialise_uniquement_les_criteres_renseignes() {
    FiltresMultisite filtres =
        new FiltresMultisite("640380", StatutWorkflow.VERIFIE, Verdict.DOUTEUX, 2026);

    assertThat(FiltresMultisiteJson.serialiser(filtres))
        .isEqualTo(
            "{\"site\":\"640380\",\"statut\":\"Vérifié\",\"verdict\":\"Douteux\",\"annee\":\"2026\"}");
  }

  @Test
  @DisplayName("Un filtre sans aucun critère donne un objet JSON vide")
  void aucun_critere_donne_objet_vide() {
    assertThat(FiltresMultisiteJson.serialiser(FiltresMultisite.aucun())).isEqualTo("{}");
  }

  @Test
  @DisplayName("Les critères null sont absents du JSON (pas de clé)")
  void criteres_null_absents_du_json() {
    assertThat(FiltresMultisiteJson.serialiser(FiltresMultisite.parSite("640381")))
        .isEqualTo("{\"site\":\"640381\"}");
  }

  @Test
  @DisplayName("Aller-retour fidèle : tous les critères renseignés (libellés accentués inclus)")
  void aller_retour_complet() {
    FiltresMultisite filtres =
        new FiltresMultisite("640380", StatutWorkflow.PRET_A_DEPOSER, Verdict.A_JETER, 2026);

    String json = FiltresMultisiteJson.serialiser(filtres);

    assertThat(FiltresMultisiteJson.interpreter(json)).isEqualTo(filtres);
  }

  @Test
  @DisplayName("Aller-retour fidèle : objet vide → tous critères null")
  void aller_retour_vide() {
    String json = FiltresMultisiteJson.serialiser(FiltresMultisite.aucun());

    assertThat(FiltresMultisiteJson.interpreter(json)).isEqualTo(FiltresMultisite.aucun());
  }

  @Test
  @DisplayName("Interpréter relit l'année comme un entier et l'énum par son libellé")
  void interprete_les_types() {
    FiltresMultisite filtres =
        FiltresMultisiteJson.interpreter("{\"statut\":\"Importé\",\"annee\":\"2024\"}");

    assertThat(filtres.statut()).isEqualTo(StatutWorkflow.IMPORTE);
    assertThat(filtres.annee()).isEqualTo(2024);
    assertThat(filtres.numeroCarre()).isNull();
    assertThat(filtres.verdict()).isNull();
  }
}

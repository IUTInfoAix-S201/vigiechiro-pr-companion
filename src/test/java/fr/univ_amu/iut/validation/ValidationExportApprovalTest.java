package fr.univ_amu.iut.validation;

import fr.univ_amu.iut.validation.model.ExportVuCsv;
import fr.univ_amu.iut.validation.model.ParserCsvTadarida;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.QuietReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Golden master (ApprovalTests) du CSV {@code _Vu} exporté à partir des 473 observations réelles.
 * Verrouille la sortie canonique de {@link ExportVuCsv} : tout changement de format (ordre des
 * colonnes, sérialisation des nombres, gestion des champs vides) fait diverger le {@code
 * .received.txt} du {@code .approved.txt} et casse le test.
 *
 * <p>{@link QuietReporter} : pas de lancement d'outil de diff graphique (compatible CI headless).
 * Pour (re)générer la référence après un changement de format <b>assumé</b> : supprimer le {@code
 * .approved.txt}, relancer, puis renommer le {@code .received.txt} produit en {@code
 * .approved.txt}.
 */
@UseReporter(QuietReporter.class)
class ValidationExportApprovalTest {

  @Test
  @DisplayName("L'export _Vu des 473 observations réelles est stable (golden master)")
  void le_csv_vu_exporte_est_stable() throws URISyntaxException {
    Path brut =
        Path.of(
            ValidationExportApprovalTest.class
                .getResource("/validation/observations_brut.csv")
                .toURI());

    ParserCsvTadarida parser = new ParserCsvTadarida();
    ExportVuCsv export = new ExportVuCsv();

    Approvals.verify(export.versChaine(parser.parser(brut).lignes()));
  }
}

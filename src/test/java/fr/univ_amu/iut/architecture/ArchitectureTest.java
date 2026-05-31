package fr.univ_amu.iut.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Règles d'architecture (ArchUnit) garanties dès la fondation. Les règles UI (vue ne touche pas
 * JDBC, viewmodel sans JavaFX scene/fxml, point d'extension Protocole) seront ajoutées avec les
 * features correspondantes.
 *
 * <p>Écrit avec l'API « core » d'ArchUnit ({@link ClassFileImporter} + {@code @Test}) plutôt
 * qu'avec {@code @AnalyzeClasses}/{@code @ArchTest} : c'est la convention du projet (cf.
 * IMPL-CONVENTIONS).
 */
class ArchitectureTest {

  private static JavaClasses classes;

  @BeforeAll
  static void importerLeCodeDeProduction() {
    classes =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("fr.univ_amu.iut");
  }

  @Test
  @DisplayName("Les paquets model ne dépendent pas de JavaFX (réutilisation O6)")
  void model_sans_javafx() {
    noClasses()
        .that()
        .resideInAPackage("..model..")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("javafx..")
        .check(classes);
  }

  @Test
  @DisplayName("La persistance (infra + DAO) ne dépend pas de JavaFX")
  void persistance_sans_javafx() {
    noClasses()
        .that()
        .resideInAnyPackage("..commun.persistence..", "..model.dao..")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("javafx..")
        .check(classes);
  }

  @Test
  @DisplayName("Les slices fr.univ_amu.iut.* sont sans cycle (hors racine de composition)")
  void features_sans_cycle() {
    // La racine de composition (commun.di) connaît toutes les features : c'est son rôle.
    // On l'exclut de l'analyse de cycles (sinon commun ↔ sites apparaîtrait comme un faux cycle).
    JavaClasses horsRacineDeComposition =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .withImportOption(location -> !location.contains("/commun/di/"))
            .importPackages("fr.univ_amu.iut");

    slices()
        .matching("fr.univ_amu.iut.(*)..")
        .should()
        .beFreeOfCycles()
        .check(horsRacineDeComposition);
  }
}

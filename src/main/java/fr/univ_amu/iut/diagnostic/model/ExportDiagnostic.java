package fr.univ_amu.iut.diagnostic.model;

import fr.univ_amu.iut.commun.model.EcrivainCsv;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Export CSV déterministe d'un diagnostic (P6-CA6 : « l'export produit un CSV ou un PDF du
 * diagnostic »), via l'utilitaire partagé {@link EcrivainCsv}.
 *
 * <p>Deux exports indépendants : la <b>série climatique</b> (graphe T°/hygrométrie) et la <b>liste
 * des anomalies</b> classées (R19). Sortie <b>déterministe</b> (ordre stable, aucun horodatage ni
 * hash), donc validable par golden master / ApprovalTests.
 */
public final class ExportDiagnostic {

  private static final DateTimeFormatter HEURE =
      DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ROOT);

  private ExportDiagnostic() {}

  /**
   * Série climatique en CSV : {@code Date;Heure;Temperature_C;Humidite_pct} puis une ligne par
   * mesure. Date au format ISO {@code AAAA-MM-JJ}, heure {@code HH:MM:SS}.
   */
  public static String climatVersCsv(SerieClimatique climat) {
    List<List<String>> lignes = new ArrayList<>();
    lignes.add(List.of("Date", "Heure", "Temperature_C", "Humidite_pct"));
    for (MesureClimatique mesure : climat.mesures()) {
      lignes.add(
          List.of(
              mesure.date().toString(),
              mesure.heure().format(HEURE),
              String.valueOf(mesure.temperatureCelsius()),
              String.valueOf(mesure.humiditePourcent())));
    }
    return EcrivainCsv.minimal().versChaine(lignes);
  }

  /**
   * Anomalies en CSV : {@code Categorie;Message}, regroupées par famille (réveils non programmés,
   * erreurs SD, redémarrages, batterie). Une anomalie relevant de plusieurs familles apparaît dans
   * chacune.
   */
  public static String anomaliesVersCsv(AnalyseAnomalies analyse) {
    List<List<String>> lignes = new ArrayList<>();
    lignes.add(List.of("Categorie", "Message"));
    ajouter(lignes, "Réveil non programmé", analyse.reveilsNonProgrammes());
    ajouter(lignes, "Erreur SD", analyse.erreursSD());
    ajouter(lignes, "Redémarrage", analyse.redemarrages());
    ajouter(lignes, "Batterie", analyse.alertesBatterie());
    return EcrivainCsv.minimal().versChaine(lignes);
  }

  private static void ajouter(List<List<String>> lignes, String categorie, List<String> messages) {
    for (String message : messages) {
      lignes.add(List.of(categorie, message));
    }
  }
}

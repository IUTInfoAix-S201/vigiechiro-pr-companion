package fr.univ_amu.iut.validation.model;

import fr.univ_amu.iut.commun.model.EcrivainCsv;
import fr.univ_amu.iut.commun.model.ModeValidation;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Écrit un CSV {@code _Vu} <b>réinjectable</b> sur le portail Vigie-Chiro (parcours P7, étape E7 ;
 * règles R17 et R24). Symétrique de {@code ParserCsvTadarida} : ce que l'export produit, le parseur
 * doit savoir le relire à l'identique (au sens des {@link LigneObservation}).
 *
 * <p><b>Classe {@code model} pure</b> (aucun JavaFX, aucun SQL). La sérialisation CSV est déléguée
 * à l'utilitaire commun {@link EcrivainCsv#minimal()} (séparateur {@code ;}, guillemets seulement
 * si nécessaire, fin de ligne {@code \n}, déterministe).
 *
 * <h2>Colonnes émises</h2>
 *
 * Mêmes 11 colonnes que le fichier Tadarida (entête nue, format Vu) :
 *
 * <pre>
 * nom du fichier;temps_debut;temps_fin;frequence_mediane;tadarida_taxon;tadarida_probabilite;
 * tadarida_taxon_autre;observateur_taxon;observateur_probabilite;validateur_taxon;validateur_probabilite
 * </pre>
 *
 * Une 12e colonne {@code validation_mode} (R24) est ajoutée à la demande ({@code inclureMode}).
 *
 * <h2>Règle R17 (observation non touchée)</h2>
 *
 * Les colonnes {@code tadarida_*} sont <b>toujours</b> recopiées telles quelles (y compris une 2e
 * proposition multi-valuée). Une observation <b>non touchée</b> (sans {@code taxonObservateur})
 * laisse les colonnes observateur vides : elle « conserve ses colonnes Tadarida à l'identique ».
 * Une observation <b>validée / corrigée</b> renseigne {@code observateur_taxon} (et sa
 * probabilité). Les colonnes {@code validateur_*} (rôle distinct de l'observateur, non géré au MVP)
 * sont laissées vides, conformément aux fichiers de référence.
 */
public final class ExportVuCsv {

  /** Entête des 11 colonnes Tadarida, dans l'ordre du fichier de référence. */
  static final List<String> ENTETE =
      List.of(
          ParserCsvTadarida.COL_NOM,
          ParserCsvTadarida.COL_DEBUT,
          ParserCsvTadarida.COL_FIN,
          ParserCsvTadarida.COL_FREQ,
          ParserCsvTadarida.COL_TAXON_TADARIDA,
          ParserCsvTadarida.COL_PROB_TADARIDA,
          ParserCsvTadarida.COL_TAXON_AUTRE,
          ParserCsvTadarida.COL_TAXON_OBSERVATEUR,
          ParserCsvTadarida.COL_PROB_OBSERVATEUR,
          "validateur_taxon",
          "validateur_probabilite");

  private final EcrivainCsv ecrivain = EcrivainCsv.minimal(); // ';', minimal, '\n'

  /**
   * Sérialise les lignes en CSV {@code _Vu} (11 colonnes, sans colonne {@code validation_mode}).
   */
  public String versChaine(List<LigneObservation> lignes) {
    return versChaine(lignes, false);
  }

  /**
   * Sérialise les lignes en CSV {@code _Vu}.
   *
   * @param inclureMode {@code true} pour ajouter la 12e colonne {@code validation_mode} (R24)
   */
  public String versChaine(List<LigneObservation> lignes, boolean inclureMode) {
    return ecrivain.versChaine(toCsv(lignes, inclureMode));
  }

  /** Écrit le CSV {@code _Vu} dans {@code fichier} (11 colonnes). */
  public void ecrire(Path fichier, List<LigneObservation> lignes) {
    ecrire(fichier, lignes, false);
  }

  /**
   * Écrit le CSV {@code _Vu} dans {@code fichier}, avec colonne {@code validation_mode}
   * optionnelle.
   */
  public void ecrire(Path fichier, List<LigneObservation> lignes, boolean inclureMode) {
    ecrivain.ecrire(fichier, toCsv(lignes, inclureMode));
  }

  /** Construit les lignes CSV (entête + données). */
  private static List<List<String>> toCsv(List<LigneObservation> lignes, boolean inclureMode) {
    Objects.requireNonNull(lignes, "lignes");
    List<List<String>> csv = new ArrayList<>();
    csv.add(entete(inclureMode));
    for (LigneObservation ligne : lignes) {
      csv.add(ligneCsv(ligne, inclureMode));
    }
    return csv;
  }

  private static List<String> entete(boolean inclureMode) {
    List<String> entete = new ArrayList<>(ENTETE);
    if (inclureMode) {
      entete.add(ParserCsvTadarida.COL_MODE_VALIDATION);
    }
    return entete;
  }

  private static List<String> ligneCsv(LigneObservation l, boolean inclureMode) {
    List<String> champs = new ArrayList<>();
    champs.add(texte(l.nomSequence()));
    champs.add(nombre(l.debutS()));
    champs.add(nombre(l.finS()));
    champs.add(entier(l.frequenceMedianeHz()));
    champs.add(texte(l.taxonTadarida())); // R17 : verbatim
    champs.add(nombre(l.probTadarida())); // R17 : verbatim
    champs.add(texte(l.taxonAutreTadarida())); // R17 : verbatim (peut être multi-valué)
    champs.add(texte(l.taxonObservateur())); // R15/R16 : décision observateur
    champs.add(nombre(l.probObservateur()));
    champs.add(""); // validateur_taxon : non géré au MVP
    champs.add(""); // validateur_probabilite : non géré au MVP
    if (inclureMode) {
      ModeValidation mode =
          l.modeValidation() == null ? ModeValidation.NON_VALIDE : l.modeValidation();
      champs.add(texte(mode.libelle())); // null (NON_VALIDE) → champ vide
    }
    return champs;
  }

  private static String texte(String valeur) {
    return valeur == null ? "" : valeur;
  }

  /** Sérialise un {@link Double} via {@code Double.toString} (round-trip exact garanti). */
  private static String nombre(Double valeur) {
    return valeur == null ? "" : Double.toString(valeur);
  }

  private static String entier(Integer valeur) {
    return valeur == null ? "" : Integer.toString(valeur);
  }
}

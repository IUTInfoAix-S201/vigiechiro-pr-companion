package fr.univ_amu.iut.validation.model;

import fr.univ_amu.iut.commun.model.LecteurCsv;
import fr.univ_amu.iut.commun.model.ModeValidation;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Parseur d'un fichier de résultats Tadarida (parcours P7, étape E7 ; règle R17). Lit un CSV
 * d'observations (séparateur {@code ;}) et le projette en {@link ResultatParseTadarida} : le {@link
 * FormatTadarida} détecté + une {@link LigneObservation} par ligne.
 *
 * <p><b>Classe {@code model} pure</b> : aucune dépendance JavaFX ni SQL. La lecture brute du CSV
 * est déléguée à l'utilitaire commun {@link LecteurCsv} (gestion RFC 4180 des guillemets, {@code
 * \n} et {@code \r\n}). Le parseur ajoute par-dessus la <b>détection de format</b> et le <b>mapping
 * des colonnes Tadarida</b>.
 *
 * <h2>Détection Brut vs Vu</h2>
 *
 * Les deux fichiers ont la <b>même entête</b> et les mêmes colonnes ; ils ne diffèrent que par la
 * forme :
 *
 * <ul>
 *   <li><b>Brut</b> ({@code *-observations.csv}) : <b>tous</b> les champs sont guillemetés,
 *       l'entête commence donc par {@code "nom du fichier"…}. Les colonnes observateur sont vides
 *       ({@code ""}).
 *   <li><b>Vu</b> ({@code *-observations_Vu.csv}) : entête nu ({@code nom du fichier;…}), champs
 *       guillemetés seulement si nécessaire. Réinjectable, il peut porter les décisions de
 *       l'observateur.
 * </ul>
 *
 * On détecte donc sur la <b>présence de guillemets autour de l'entête</b> (et non sur le suffixe
 * {@code _Vu} du nom de fichier, peu fiable).
 *
 * <h2>Champ « vide »</h2>
 *
 * Dans un Brut un champ vide est {@code ""} (chaîne vide après déguillemetage). Dans certains Vu
 * réinjectés un champ vide a été ré-encodé en un guillemet littéral seul ({@code """"} dans le
 * fichier, qui se relit en un caractère {@code "}). Les deux représentations sont normalisées en
 * {@code null} (cf. {@link #estVide(String)}), pour que Brut et Vu se parsent en observations
 * équivalentes.
 *
 * <h2>Mapping des colonnes</h2>
 *
 * Les colonnes sont repérées <b>par leur nom d'entête</b> (et non par position) pour rester robuste
 * à un éventuel réordonnancement. Colonnes attendues : {@code nom du fichier}, {@code temps_debut},
 * {@code temps_fin}, {@code frequence_mediane}, {@code tadarida_taxon}, {@code
 * tadarida_probabilite}, {@code tadarida_taxon_autre}, {@code observateur_taxon}, {@code
 * observateur_probabilite}. La colonne {@code validation_mode} (R24) est facultative.
 */
public final class ParserCsvTadarida {

  static final String COL_NOM = "nom du fichier";
  static final String COL_DEBUT = "temps_debut";
  static final String COL_FIN = "temps_fin";
  static final String COL_FREQ = "frequence_mediane";
  static final String COL_TAXON_TADARIDA = "tadarida_taxon";
  static final String COL_PROB_TADARIDA = "tadarida_probabilite";
  static final String COL_TAXON_AUTRE = "tadarida_taxon_autre";
  static final String COL_TAXON_OBSERVATEUR = "observateur_taxon";
  static final String COL_PROB_OBSERVATEUR = "observateur_probabilite";
  static final String COL_MODE_VALIDATION = "validation_mode";

  private static final char BOM = '﻿';

  private final LecteurCsv lecteur = new LecteurCsv(); // séparateur ';'

  /**
   * Détecte le {@link FormatTadarida} d'un contenu CSV : {@link FormatTadarida#BRUT} si l'entête
   * est guillemetée (commence par {@code "}), {@link FormatTadarida#VU} sinon.
   */
  public FormatTadarida detecterFormat(String contenu) {
    Objects.requireNonNull(contenu, "contenu");
    String debut = sansBom(contenu).stripLeading();
    return debut.startsWith("\"") ? FormatTadarida.BRUT : FormatTadarida.VU;
  }

  /** Variante fichier de {@link #detecterFormat(String)}. */
  public FormatTadarida detecterFormat(Path fichier) {
    return detecterFormat(lire(fichier));
  }

  /** Parse un contenu CSV Tadarida (Brut ou Vu) en {@link ResultatParseTadarida}. */
  public ResultatParseTadarida parser(String contenu) {
    Objects.requireNonNull(contenu, "contenu");
    String propre = sansBom(contenu);
    FormatTadarida format = detecterFormat(propre);
    List<List<String>> lignes = lecteur.lire(propre);
    if (lignes.isEmpty()) {
      throw new IllegalArgumentException("CSV Tadarida vide : aucune entête.");
    }
    Map<String, Integer> index = indexerColonnes(lignes.get(0));
    int colNom = exigerColonne(index, COL_NOM);
    int colTaxon = exigerColonne(index, COL_TAXON_TADARIDA);

    List<LigneObservation> observations = new ArrayList<>();
    for (int i = 1; i < lignes.size(); i++) {
      List<String> ligne = lignes.get(i);
      String nom = texte(cellule(ligne, colNom));
      if (nom == null) {
        continue; // ligne vide (saut final, ligne blanche) : pas une observation
      }
      observations.add(
          new LigneObservation(
              nom,
              nombre(cellule(ligne, index.get(COL_DEBUT))),
              nombre(cellule(ligne, index.get(COL_FIN))),
              entier(cellule(ligne, index.get(COL_FREQ))),
              texte(cellule(ligne, colTaxon)),
              nombre(cellule(ligne, index.get(COL_PROB_TADARIDA))),
              texte(cellule(ligne, index.get(COL_TAXON_AUTRE))),
              texte(cellule(ligne, index.get(COL_TAXON_OBSERVATEUR))),
              nombre(cellule(ligne, index.get(COL_PROB_OBSERVATEUR))),
              mode(cellule(ligne, index.get(COL_MODE_VALIDATION)))));
    }
    return new ResultatParseTadarida(format, observations);
  }

  /** Variante fichier de {@link #parser(String)}. */
  public ResultatParseTadarida parser(Path fichier) {
    return parser(lire(fichier));
  }

  /**
   * Lit le texte brut du fichier (UTF-8). On a besoin du texte non parsé pour la détection de
   * format (qui repose sur les guillemets, perdus une fois le CSV parsé par {@link LecteurCsv}).
   */
  private static String lire(Path fichier) {
    Objects.requireNonNull(fichier, "fichier");
    try {
      return Files.readString(fichier, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException("Lecture CSV Tadarida impossible : " + fichier, e);
    }
  }

  /** Indexe l'entête : nom de colonne (en minuscules, trimé) → position. */
  private static Map<String, Integer> indexerColonnes(List<String> entete) {
    Map<String, Integer> index = new HashMap<>();
    for (int i = 0; i < entete.size(); i++) {
      String nom = entete.get(i);
      if (nom != null) {
        index.put(nom.trim().toLowerCase(Locale.ROOT), i);
      }
    }
    return index;
  }

  private static int exigerColonne(Map<String, Integer> index, String nom) {
    Integer position = index.get(nom);
    if (position == null) {
      throw new IllegalArgumentException(
          "Colonne « " + nom + " » absente de l'entête Tadarida : " + index.keySet());
    }
    return position;
  }

  /**
   * Cellule à la position {@code col} (ou {@code null} si la position est absente / hors borne).
   */
  private static String cellule(List<String> ligne, Integer col) {
    if (col == null || col < 0 || col >= ligne.size()) {
      return null;
    }
    return ligne.get(col);
  }

  /**
   * Un champ est « vide » s'il est {@code null}, blanc, ou réduit à un guillemet littéral seul
   * ({@code "}) — l'encodage d'un champ vide rencontré dans certains exports {@code _Vu}.
   */
  static boolean estVide(String champ) {
    if (champ == null) {
      return true;
    }
    String trim = champ.trim();
    return trim.isEmpty() || trim.equals("\"");
  }

  private static String texte(String champ) {
    return estVide(champ) ? null : champ.trim();
  }

  private static Double nombre(String champ) {
    return estVide(champ) ? null : Double.valueOf(champ.trim());
  }

  private static Integer entier(String champ) {
    return estVide(champ) ? null : (int) Math.round(Double.parseDouble(champ.trim()));
  }

  private static ModeValidation mode(String champ) {
    return estVide(champ) ? ModeValidation.NON_VALIDE : ModeValidation.parLibelle(champ.trim());
  }

  private static String sansBom(String contenu) {
    return (!contenu.isEmpty() && contenu.charAt(0) == BOM) ? contenu.substring(1) : contenu;
  }
}

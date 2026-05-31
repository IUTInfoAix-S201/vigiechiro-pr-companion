package fr.univ_amu.iut.validation.model;

/**
 * Format d'un fichier de résultats Tadarida (C12, colonne {@code detected_format}, R17).
 *
 * <ul>
 *   <li>{@link #BRUT} : le fichier livré par Tadarida ({@code *-observations.csv}) où <b>tous</b>
 *       les champs sont encadrés de guillemets et les colonnes observateur sont vides ;
 *   <li>{@link #VU} : le fichier <b>réinjectable</b> ({@code *-observations_Vu.csv}) où les champs
 *       ne sont plus systématiquement guillemetés et où l'observateur a pu renseigner ses
 *       décisions.
 * </ul>
 *
 * <p>La détection ne se fait pas sur le nom de fichier (peu fiable) mais sur la <b>forme</b> de
 * l'entête : un entête entièrement guillemeté ({@code "nom du fichier";…}) trahit un fichier Brut,
 * un entête nu ({@code nom du fichier;…}) un fichier Vu (cf. {@code
 * ParserCsvTadarida#detecterFormat}).
 *
 * <p>Le libellé ({@code "Brut"} / {@code "Vu"}) est la valeur persistée dans {@code
 * identification_results.detected_format} et est volontairement aligné sur l'énum {@code
 * fr.univ_amu.iut.commun.model} (libellé porteur de sens, parLibelle tolérant à la casse
 * historique).
 */
public enum FormatTadarida {
  BRUT("Brut"),
  VU("Vu");

  private final String libelle;

  FormatTadarida(String libelle) {
    this.libelle = libelle;
  }

  /** Valeur persistée (colonne {@code detected_format}). */
  public String libelle() {
    return libelle;
  }

  /** Retrouve un format depuis son libellé persisté (insensible à la casse). */
  public static FormatTadarida parLibelle(String libelle) {
    if (libelle != null) {
      for (FormatTadarida format : values()) {
        if (format.libelle.equalsIgnoreCase(libelle.trim())) {
          return format;
        }
      }
    }
    throw new IllegalArgumentException("Format Tadarida inconnu : " + libelle);
  }
}

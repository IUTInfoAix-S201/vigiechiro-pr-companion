package fr.univ_amu.iut.commun.model;

/**
 * Protocole de suivi d'un site (R3/R4).
 *
 * <ul>
 *   <li>{@link #STANDARD} : suit Vigie-Chiro à la lettre (2 passages, fenêtres temporelles) → R3/R4
 *       actives.
 *   <li>{@link #RECHERCHE} : mêmes réglages techniques mais dates/fréquences personnalisées → R3/R4
 *       muettes.
 * </ul>
 *
 * <p>La liste est volontairement extensible (Pédestre, Routier… à venir) : le {@code libelle} est
 * la valeur persistée en base, distincte du nom Java de la constante.
 */
public enum Protocole {
  STANDARD("PointFixeStandard"),
  RECHERCHE("PointFixeRecherche");

  private final String libelle;

  Protocole(String libelle) {
    this.libelle = libelle;
  }

  /** Valeur stockée en base (colonne {@code protocol}). */
  public String libelle() {
    return libelle;
  }

  /** Retrouve un protocole depuis sa valeur persistée. */
  public static Protocole parLibelle(String libelle) {
    for (Protocole protocole : values()) {
      if (protocole.libelle.equals(libelle)) {
        return protocole;
      }
    }
    throw new IllegalArgumentException("Protocole inconnu : " + libelle);
  }
}

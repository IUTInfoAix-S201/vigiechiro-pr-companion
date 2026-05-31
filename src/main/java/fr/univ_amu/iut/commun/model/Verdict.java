package fr.univ_amu.iut.commun.model;

/**
 * Verdict de vérification d'un {@code Passage}, saisi après écoute de la sélection (C5, R13/R14).
 *
 * <p>{@link #A_JETER} est un état bloquant : un passage « à jeter » ne peut pas rejoindre un lot
 * prêt à déposer (R14).
 */
public enum Verdict {
  A_VERIFIER("À vérifier"),
  OK("OK"),
  DOUTEUX("Douteux"),
  A_JETER("À jeter");

  private final String libelle;

  Verdict(String libelle) {
    this.libelle = libelle;
  }

  public String libelle() {
    return libelle;
  }

  public static Verdict parLibelle(String libelle) {
    for (Verdict verdict : values()) {
      if (verdict.libelle.equals(libelle)) {
        return verdict;
      }
    }
    throw new IllegalArgumentException("Verdict inconnu : " + libelle);
  }
}

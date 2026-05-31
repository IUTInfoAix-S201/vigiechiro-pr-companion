package fr.univ_amu.iut.commun.model;

/**
 * Mode de validation d'une {@code Observation} (C13, R24).
 *
 * <ul>
 *   <li>{@link #MANUEL} : saisie explicite par l'observateur ;
 *   <li>{@link #AUTO} : propagé automatiquement par le mode inventaire ;
 *   <li>{@link #NON_VALIDE} : pas encore validé (persisté en {@code NULL}).
 * </ul>
 */
public enum ModeValidation {
  MANUEL("manuel"),
  AUTO("auto"),
  NON_VALIDE(null);

  private final String libelle;

  ModeValidation(String libelle) {
    this.libelle = libelle;
  }

  /** Valeur persistée (colonne {@code validation_mode}) ; {@code null} pour {@link #NON_VALIDE}. */
  public String libelle() {
    return libelle;
  }

  /** Retrouve un mode depuis sa valeur persistée ({@code null} → {@link #NON_VALIDE}). */
  public static ModeValidation parLibelle(String libelle) {
    if (libelle == null) {
      return NON_VALIDE;
    }
    for (ModeValidation mode : values()) {
      if (libelle.equals(mode.libelle)) {
        return mode;
      }
    }
    throw new IllegalArgumentException("Mode de validation inconnu : " + libelle);
  }
}

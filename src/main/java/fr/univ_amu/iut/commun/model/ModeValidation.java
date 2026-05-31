package fr.univ_amu.iut.commun.model;

/// Mode de validation d'une `Observation` (C13, R24).
///
/// - [#MANUEL] : saisie explicite par l'observateur ;
/// - [#AUTO] : propagé automatiquement par le mode inventaire ;
/// - [#NON_VALIDE] : pas encore validé (persisté en `NULL`).
public enum ModeValidation {
  MANUEL("manuel"),
  AUTO("auto"),
  NON_VALIDE(null);

  private final String libelle;

  ModeValidation(String libelle) {
    this.libelle = libelle;
  }

  /// Valeur persistée (colonne `validation_mode`) ; `null` pour [#NON_VALIDE].
  public String libelle() {
    return libelle;
  }

  /// Retrouve un mode depuis sa valeur persistée (`null` → [#NON_VALIDE]).
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

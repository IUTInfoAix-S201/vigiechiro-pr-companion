package fr.univ_amu.iut.validation.model;

/**
 * Statut <b>dérivé</b> d'une {@link Observation} vis-à-vis de la validation taxonomique (R15, R16,
 * R17). Ce n'est pas une colonne stockée : il se déduit de la comparaison entre {@code
 * taxonObservateur} et {@code taxonTadarida} (cf. {@code ServiceValidation#statut}).
 *
 * <ul>
 *   <li>{@link #NON_TOUCHEE} (R17) : aucun taxon observateur saisi → la ligne conserve ses colonnes
 *       {@code tadarida_*} telles quelles à l'export {@code _Vu}.
 *   <li>{@link #VALIDEE} (R15) : taxon observateur = taxon Tadarida <b>et</b> probabilité
 *       observateur renseignée.
 *   <li>{@link #CORRIGEE} (R16) : taxon observateur ≠ taxon Tadarida.
 * </ul>
 */
public enum StatutObservation {
  NON_TOUCHEE,
  VALIDEE,
  CORRIGEE
}

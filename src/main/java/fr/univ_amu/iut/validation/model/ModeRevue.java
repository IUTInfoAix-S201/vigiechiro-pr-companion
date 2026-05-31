package fr.univ_amu.iut.validation.model;

/**
 * Mode de revue des observations lors de la validation taxonomique (R18, parcours P7).
 *
 * <ul>
 *   <li>{@link #INVENTAIRE} : revue orientée « liste d'espèces ». Une fois une espèce confirmée sur
 *       la nuit, les autres détections <b>non touchées</b> de la même espèce Tadarida sont
 *       propagées automatiquement (mode {@code auto}, R24) : on arrête de valider cette espèce à la
 *       main.
 *   <li>{@link #ACTIVITE} : revue quantitative. Aucune propagation : chaque observation doit être
 *       passée en revue individuellement.
 * </ul>
 *
 * <p>Le mode est un choix de l'observateur au moment de la revue (sélectionnable par passage), pas
 * une donnée persistée de l'observation. La trace de ce qui a été propagé vit, elle, dans la
 * colonne {@code validation_mode} de chaque observation (R24 : {@code manuel} vs {@code auto}).
 */
public enum ModeRevue {
  INVENTAIRE,
  ACTIVITE
}

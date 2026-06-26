package fr.univ_amu.iut.lot.viewmodel;

/// État d'avancement d'une étape du stepper de dépôt de M-Lot, relatif à l'étape courante.
/// Le nom (minuscule) sert de suffixe de classe CSS (`etape-franchie` / `etape-courante` /
/// `etape-a_venir`), comme le stepper de M-Passage.
public enum EtatEtape {
    /// Étape déjà accomplie.
    FRANCHIE,
    /// Étape en cours (là où se trouve l'observateur).
    COURANTE,
    /// Étape pas encore atteinte.
    A_VENIR
}

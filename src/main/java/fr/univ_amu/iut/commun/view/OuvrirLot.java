package fr.univ_amu.iut.commun.view;

/// Contrat socle d'**ouverture de la préparation et du dépôt du lot** (M-Lot) d'un passage.
///
/// Même esprit que [OuvrirDiagnostic] / [OuvrirValidation] : le socle inverse la dépendance. La
/// feature `lot` en fournit l'implémentation (`NavigationLot`) et la lie dans son module ; la
/// feature `passage` (M-Passage) l'injecte pour ouvrir l'écran **sans dépendre** de `lot` — qui
/// dépend déjà de `passage` (`ServiceLot` lit ses DAO), donc une dépendance directe formerait un
/// cycle (le graphe de slices reste acyclique).
public interface OuvrirLot {

    /// Ouvre l'écran de préparation et de dépôt du lot du passage `idPassage`.
    void ouvrir(Long idPassage);
}

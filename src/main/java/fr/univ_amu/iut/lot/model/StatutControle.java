package fr.univ_amu.iut.lot.model;

/// Statut d'un [ControleCoherence] de l'étape « Préparer le lot » (#254).
public enum StatutControle {
    /// Contrôle satisfait (✓).
    OK,
    /// Contrôle en échec, **bloquant** la préparation (✗) : à corriger avant de préparer le lot.
    ECHEC,
    /// Contrôle **non bloquant** signalé (⚠), par exemple un relevé climatique absent : le dépôt reste
    /// possible.
    AVERTISSEMENT
}

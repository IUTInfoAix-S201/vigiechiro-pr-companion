package fr.univ_amu.iut.commun.view;

/// Groupes du menu ☰ (outils), dans leur **ordre d'affichage**. Le socle insère un séparateur entre
/// deux groupes consécutifs distincts, reproduisant la mise en page historique du menu (#927/#930).
///
/// L'ordre des constantes fait foi (l'ordinal sert au tri) : les [ActionMenu] sont regroupées par
/// `groupe()`, puis triées par `ordre()` à l'intérieur de leur groupe.
public enum GroupeMenu {
    /// Sauvegarde / restauration de la base.
    BASE,
    /// Maintenance des données (purge des originaux).
    MAINTENANCE,
    /// Préférences transverses (source des fiches espèces…).
    PREFERENCES,
    /// Accès à l'écran de réglages.
    PARAMETRES,
    /// Compte VigieChiro (connexion).
    COMPTE
}

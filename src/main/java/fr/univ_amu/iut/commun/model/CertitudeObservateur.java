package fr.univ_amu.iut.commun.model;

import java.util.Locale;

/// Certitude que l'observateur **déclare manuellement** sur une observation au moment de sa revue
/// (#1139) : domaine fermé à trois catégories, calé sur l'énumération `observateur_probabilite` de
/// la plateforme VigieChiro (contrat d'écriture #1203 : jetons exacts `SUR | PROBABLE | POSSIBLE`,
/// champ obligatoire dès qu'un taxon observateur est poussé).
///
/// Notion **distincte** de la probabilité numérique `[0,1]` (`prob_observer`), qui est la confiance
/// **Tadarida** recopiée à la validation (héritage du format `_Vu`) : aucune conversion de l'une vers
/// l'autre, la certitude est **vide par défaut** tant que l'observateur ne l'a pas saisie (miroir du
/// site web, où les listes observateur sont vides tant qu'on n'a pas cliqué OK).
///
/// Persistée sous son [#name] (colonne `observer_certainty`, nullable) ; la relecture est
/// **tolérante** ([#depuisTexte] : valeur absente ou inconnue → `null` = « non renseignée »), comme
/// [fr.univ_amu.iut.passage.model.Vent].
public enum CertitudeObservateur {
    SUR("Sûr"),
    PROBABLE("Probable"),
    POSSIBLE("Possible");

    private final String libelle;

    CertitudeObservateur(String libelle) {
        this.libelle = libelle;
    }

    /// Libellé lisible pour l'IHM (ex. « Sûr »).
    public String libelle() {
        return libelle;
    }

    /// Jeton persisté et poussé à l'API (le [#name] : `SUR`, `PROBABLE`, `POSSIBLE`).
    public String jeton() {
        return name();
    }

    /// Lit une certitude depuis un texte stocké ou renvoyé par le serveur : `null` ou vide → `null`
    /// (non renseignée) ; sinon la constante correspondante (insensible à la casse), ou `null` si le
    /// texte ne correspond à aucune (tolérant : le serveur peut évoluer sans nous prévenir).
    public static CertitudeObservateur depuisTexte(String texte) {
        if (texte == null || texte.isBlank()) {
            return null;
        }
        try {
            return valueOf(texte.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException inconnue) {
            return null;
        }
    }
}

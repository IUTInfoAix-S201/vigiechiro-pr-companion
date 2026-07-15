package fr.univ_amu.iut.commun.model;

/// Verdict de vérification d'**un fichier son** (une séquence de la sélection d'écoute), saisi à
/// l'écoute (chantier #1524, lot 5). Granularité fine, complémentaire du [Verdict] **final du
/// passage** qui en est dérivé (agrégation dans `ServiceQualification`).
///
/// - [#BON] : séquence exploitable ;
/// - [#MAUVAIS] : séquence de mauvaise qualité mais pas inutilisable ;
/// - [#INEXPLOITABLE] : rien d'exploitable (bruit seul, saturation, tronquée…).
///
/// [#NON_JUGE] est l'état par défaut (aucune écoute encore rendue) : en base, il correspond à la
/// **colonne `NULL`** de `selection_sequence.verdict` (cf. mapping du DAO). On garde donc un enum
/// non nullable côté modèle, et l'absence est portée par le `null` SQL.
public enum VerdictFichier {
    NON_JUGE("Non jugé"),
    BON("Bon"),
    MAUVAIS("Mauvais"),
    INEXPLOITABLE("Inexploitable");

    private final String libelle;

    VerdictFichier(String libelle) {
        this.libelle = libelle;
    }

    public String libelle() {
        return libelle;
    }

    /// Retrouve un verdict par son libellé exact. Voir aussi [#parLibelleOuNonJuge(String)] pour la
    /// lecture d'une colonne nullable.
    public static VerdictFichier parLibelle(String libelle) {
        for (VerdictFichier verdict : values()) {
            if (verdict.libelle.equals(libelle)) {
                return verdict;
            }
        }
        throw new IllegalArgumentException("Verdict de fichier inconnu : " + libelle);
    }

    /// Lecture tolérante d'une colonne nullable : `null` (ou libellé de [#NON_JUGE]) ⇒ [#NON_JUGE].
    public static VerdictFichier parLibelleOuNonJuge(String libelle) {
        return libelle == null ? NON_JUGE : parLibelle(libelle);
    }
}

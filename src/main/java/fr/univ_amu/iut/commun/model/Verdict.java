package fr.univ_amu.iut.commun.model;

/// Verdict **final** de vérification d'un `Passage`, dérivé des verdicts par fichier de la sélection
/// d'écoute et surchargeable (C5, R13/R14, chantier #1524).
///
/// **Lexique cible (#1524, lot 6b).** Les libellés visibles ont basculé vers `Non vérifié / OK /
/// Utilisable / Inexploitable` (les **noms de constantes** restent `A_VERIFIER/OK/DOUTEUX/A_JETER`
/// pour limiter le blast radius : badges CSS `badge-verdict-<name()>`, tri par `ordinal`, et vues de
/// filtre sauvegardées en base via `valueOf(name())` sont ainsi préservés). Le libellé est stocké dans
/// `passage.verification_verdict` (migration `V28`).
///
/// [#A_JETER] (libellé « Inexploitable ») est un état bloquant : un passage inexploitable ne peut pas
/// rejoindre un lot prêt à déposer (R14, remplacé par la garde de dépôt du lot 7).
public enum Verdict {
    A_VERIFIER("Non vérifié"),
    OK("OK"),
    DOUTEUX("Utilisable"),
    A_JETER("Inexploitable");

    private final String libelle;

    Verdict(String libelle) {
        this.libelle = libelle;
    }

    public String libelle() {
        return libelle;
    }

    public static Verdict parLibelle(String libelle) {
        for (Verdict verdict : values()) {
            if (verdict.libelle.equals(libelle)) {
                return verdict;
            }
        }
        throw new IllegalArgumentException("Verdict inconnu : " + libelle);
    }
}

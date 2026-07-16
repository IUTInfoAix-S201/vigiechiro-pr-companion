package fr.univ_amu.iut.qualification.model;

import fr.univ_amu.iut.commun.model.Verdict;
import fr.univ_amu.iut.commun.model.VerdictFichier;
import java.util.List;
import java.util.Objects;

/// Dérive le **verdict final du passage** ([Verdict]) à partir des **verdicts par fichier**
/// ([VerdictFichier]) de sa sélection d'écoute (chantier #1524, lot 5).
///
/// **Moteur pur.** Aucune base, aucune IHM : on peut tester toutes les règles en JUnit nu. C'est
/// `ServiceQualification` qui l'alimente avec les verdicts lus en base.
///
/// **Proposé, pas imposé (R13).** Le résultat est une *proposition* : l'utilisateur garde le dernier
/// mot (surcharge câblée dans l'IHM, lot 6a). Le lexique visible de [Verdict] a basculé (lot 6b) vers
/// `Non vérifié / OK / Utilisable / Inexploitable` ; les **noms de constantes** restent inchangés
/// (`A_VERIFIER/OK/DOUTEUX/A_JETER`).
///
/// **Règle de dérivation.** Sur les seules séquences **jugées** (verdict ≠ [VerdictFichier#NON_JUGE]) :
///
/// - aucune séquence jugée → [Verdict#A_VERIFIER] (rien à agréger) ;
/// - **majorité stricte** d'[VerdictFichier#INEXPLOITABLE] parmi les jugées → [Verdict#A_JETER] ;
/// - toutes les jugées à [VerdictFichier#BON] → [Verdict#OK] ;
/// - sinon (au moins un [VerdictFichier#MAUVAIS], ou une minorité d'inexploitables) →
///   [Verdict#DOUTEUX].
///
/// Le seuil exact est peu critique : le verdict final est surchargeable. Cette règle **reproduit
/// l'existant** au back-fill (V27) : un passage jadis `OK`/`Utilisable`/`Inexploitable` diffusé sur toutes ses
/// séquences se re-dérive vers le même verdict.
public final class AgregationVerdict {

    private AgregationVerdict() {}

    /// Dérive le verdict final à partir des verdicts par fichier de la sélection.
    ///
    /// @param verdictsParFichier verdicts des séquences de la sélection (l'ordre est indifférent ;
    ///     `null` interdit)
    /// @return le verdict final proposé pour le passage
    public static Verdict deriver(List<VerdictFichier> verdictsParFichier) {
        Objects.requireNonNull(verdictsParFichier, "verdictsParFichier");
        int juges = 0;
        int inexploitables = 0;
        int bons = 0;
        for (VerdictFichier verdict : verdictsParFichier) {
            if (verdict == null || verdict == VerdictFichier.NON_JUGE) {
                continue;
            }
            juges++;
            if (verdict == VerdictFichier.INEXPLOITABLE) {
                inexploitables++;
            } else if (verdict == VerdictFichier.BON) {
                bons++;
            }
        }
        if (juges == 0) {
            return Verdict.A_VERIFIER;
        }
        if (inexploitables * 2 > juges) {
            return Verdict.A_JETER;
        }
        if (bons == juges) {
            return Verdict.OK;
        }
        return Verdict.DOUTEUX;
    }
}

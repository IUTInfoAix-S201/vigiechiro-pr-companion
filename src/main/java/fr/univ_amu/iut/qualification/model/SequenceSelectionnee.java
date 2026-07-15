package fr.univ_amu.iut.qualification.model;

import fr.univ_amu.iut.commun.model.VerdictFichier;
import java.util.Objects;

/// Rattachement d'une séquence d'écoute à une [SelectionDEcoute] : une ligne de la table de
/// jonction N..N `selection_sequence` (C11 ↔ C8).
///
/// Chaque ligne matérialise à la fois l'appartenance d'une séquence à une sélection, sa
/// `position` d'affichage dans la liste de vérification (les séquences sont relues ordonnées
/// par position), le flag `ecoutee` mis à jour à chaque lecture (« séquence écoutée », dérivé)
/// et son [#verdict] par fichier (#1524, lot 5 : on ne juge que ce qu'on écoute).
///
/// La clé primaire composite est `(selection_id, sequence_id)` : une même séquence ne peut
/// être rattachée qu'une fois à une sélection donnée.
///
/// @param idSelection identifiant de la sélection (FK → `listening_selection.id`)
/// @param idSequence identifiant de la séquence rattachée (FK → `listening_sequence.id`)
/// @param position rang d'affichage dans la sélection (≥ 0)
/// @param ecoutee `true` si la séquence a déjà été écoutée (flag `listened`)
/// @param verdict verdict par fichier de la séquence ([VerdictFichier#NON_JUGE] par défaut ;
///     colonne `verdict` nullable, `null` ⇒ non jugé)
public record SequenceSelectionnee(
        Long idSelection, Long idSequence, int position, boolean ecoutee, VerdictFichier verdict) {

    public SequenceSelectionnee {
        verdict = verdict == null ? VerdictFichier.NON_JUGE : verdict;
    }

    /// Constructeur de compatibilité (sans verdict, #1524) : préserve les appels antérieurs au verdict
    /// par fichier ; le verdict retombe sur [VerdictFichier#NON_JUGE].
    public SequenceSelectionnee(Long idSelection, Long idSequence, int position, boolean ecoutee) {
        this(idSelection, idSequence, position, ecoutee, VerdictFichier.NON_JUGE);
    }

    /// Copie avec un nouveau verdict (les autres champs inchangés).
    public SequenceSelectionnee avecVerdict(VerdictFichier nouveau) {
        return new SequenceSelectionnee(idSelection, idSequence, position, ecoutee, Objects.requireNonNull(nouveau));
    }
}

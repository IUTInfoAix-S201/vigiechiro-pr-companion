package fr.univ_amu.iut.qualification.model;

/**
 * Rattachement d'une séquence d'écoute à une {@link SelectionDEcoute} : une ligne de la table de
 * jonction N..N {@code selection_sequence} (C11 ↔ C8).
 *
 * <p>Chaque ligne matérialise à la fois l'appartenance d'une séquence à une sélection, sa {@code
 * position} d'affichage dans la liste de vérification (les séquences sont relues ordonnées par
 * position) et le flag {@code ecoutee} mis à jour à chaque lecture (« séquence écoutée », dérivé).
 *
 * <p>La clé primaire composite est {@code (selection_id, sequence_id)} : une même séquence ne peut
 * être rattachée qu'une fois à une sélection donnée.
 *
 * @param idSelection identifiant de la sélection (FK → {@code listening_selection.id})
 * @param idSequence identifiant de la séquence rattachée (FK → {@code listening_sequence.id})
 * @param position rang d'affichage dans la sélection (≥ 0)
 * @param ecoutee {@code true} si la séquence a déjà été écoutée (flag {@code listened})
 */
public record SequenceSelectionnee(
    Long idSelection, Long idSequence, int position, boolean ecoutee) {}

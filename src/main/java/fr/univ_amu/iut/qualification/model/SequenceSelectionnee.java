package fr.univ_amu.iut.qualification.model;

/// Rattachement d'une séquence d'écoute à une [SelectionDEcoute] : une ligne de la table de
/// jonction N..N `selection_sequence` (C11 ↔ C8).
///
/// Chaque ligne matérialise à la fois l'appartenance d'une séquence à une sélection, sa
/// `position` d'affichage dans la liste de vérification (les séquences sont relues ordonnées
/// par position) et le flag `ecoutee` mis à jour à chaque lecture (« séquence écoutée »,
/// dérivé).
///
/// La clé primaire composite est `(selection_id, sequence_id)` : une même séquence ne peut
/// être rattachée qu'une fois à une sélection donnée.
///
/// @param idSelection identifiant de la sélection (FK → `listening_selection.id`)
/// @param idSequence identifiant de la séquence rattachée (FK → `listening_sequence.id`)
/// @param position rang d'affichage dans la sélection (≥ 0)
/// @param ecoutee `true` si la séquence a déjà été écoutée (flag `listened`)
public record SequenceSelectionnee(Long idSelection, Long idSequence, int position, boolean ecoutee) {}

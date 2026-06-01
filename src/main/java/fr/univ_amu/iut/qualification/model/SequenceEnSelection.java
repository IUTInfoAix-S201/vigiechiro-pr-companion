package fr.univ_amu.iut.qualification.model;

import fr.univ_amu.iut.passage.model.SequenceDEcoute;

/// Ligne de la sélection d'écoute affichée dans M-Qualification : une séquence retenue
/// ([SequenceDEcoute] : nom de fichier R7, durée, chemin pour l'écoute), sa position d'affichage et
/// son flag « écoutée ».
///
/// Jointure de lecture pure (`selection_sequence` × `listening_sequence`) produite par
/// [ServiceQualification#detaillerSelection] : immuable, sans dépendance JavaFX.
///
/// @param sequence la séquence d'écoute retenue
/// @param position position d'affichage dans la sélection (≥ 0, ordre de relecture)
/// @param ecoutee `true` si la séquence a déjà été écoutée
public record SequenceEnSelection(SequenceDEcoute sequence, int position, boolean ecoutee) {}

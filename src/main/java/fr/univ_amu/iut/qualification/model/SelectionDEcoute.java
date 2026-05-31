package fr.univ_amu.iut.qualification.model;

import fr.univ_amu.iut.commun.model.MethodeSelection;

/// Sélection d'écoute : sous-ensemble de séquences retenu pour vérifier qu'une nuit
/// d'enregistrement est exploitable (C11, table `listening_selection`).
///
/// Une sélection est rattachée à **un seul passage** (`passage_id` unique → relation 0:1
/// côté passage) ; elle **porte sur N séquences** matérialisées par la table de jonction
/// `selection_sequence` (voir [SequenceSelectionnee] et
/// [fr.univ_amu.iut.qualification.model.dao.SelectionDao]).
///
/// L'`id` (clé technique auto-incrémentée) vaut `null` tant que la sélection n'a pas été
/// insérée : [fr.univ_amu.iut.qualification.model.dao.SelectionDao#insert(SelectionDEcoute)]
/// renvoie une copie avec l'id généré par SQLite.
///
/// @param id clé technique, `null` avant insertion
/// @param methode méthode de constitution (R12, [MethodeSelection#REPARTITION_TEMPORELLE] par
///     défaut)
/// @param taille nombre de séquences visées (typiquement 10 à 30, configurable)
/// @param idPassage identifiant du passage vérifié (FK → `passage.id`, unique)
public record SelectionDEcoute(Long id, MethodeSelection methode, int taille, Long idPassage) {}

package fr.univ_amu.iut.qualification.model;

import fr.univ_amu.iut.commun.model.MethodeSelection;

/**
 * Sélection d'écoute : sous-ensemble de séquences retenu pour vérifier qu'une nuit d'enregistrement
 * est exploitable (C11, table {@code listening_selection}).
 *
 * <p>Une sélection est rattachée à <b>un seul passage</b> ({@code passage_id} unique → relation 0:1
 * côté passage) ; elle <b>porte sur N séquences</b> matérialisées par la table de jonction {@code
 * selection_sequence} (voir {@link SequenceSelectionnee} et {@link
 * fr.univ_amu.iut.qualification.model.dao.SelectionDao}).
 *
 * <p>L'{@code id} (clé technique auto-incrémentée) vaut {@code null} tant que la sélection n'a pas
 * été insérée : {@link
 * fr.univ_amu.iut.qualification.model.dao.SelectionDao#insert(SelectionDEcoute)} renvoie une copie
 * avec l'id généré par SQLite.
 *
 * @param id clé technique, {@code null} avant insertion
 * @param methode méthode de constitution (R12, {@link MethodeSelection#REPARTITION_TEMPORELLE} par
 *     défaut)
 * @param taille nombre de séquences visées (typiquement 10 à 30, configurable)
 * @param idPassage identifiant du passage vérifié (FK → {@code passage.id}, unique)
 */
public record SelectionDEcoute(Long id, MethodeSelection methode, int taille, Long idPassage) {}

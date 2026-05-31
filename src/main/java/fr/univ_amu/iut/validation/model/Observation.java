package fr.univ_amu.iut.validation.model;

import fr.univ_amu.iut.commun.model.ModeValidation;

/**
 * Observation : une ligne du fichier de résultats Tadarida, soumise à validation (C13, table {@code
 * observation}). Une séquence d'écoute peut générer plusieurs observations (1 ligne par espèce
 * détectée, avec timing début/fin).
 *
 * <p>Point remarquable du MCD : l'observation porte <b>trois</b> clés étrangères distinctes vers
 * {@link Taxon} :
 *
 * <ul>
 *   <li>{@code taxonTadarida} (FK {@code taxon_tadarida}) : <b>obligatoire</b>, proposition de
 *       Tadarida ;
 *   <li>{@code taxonAutreTadarida} (FK {@code taxon_other_tadarida}) : optionnel, 2e proposition ;
 *   <li>{@code taxonObservateur} (FK {@code taxon_observer}) : optionnel, saisi en validation.
 * </ul>
 *
 * <p>Le {@code modeValidation} est mappé via {@link ModeValidation} (colonne {@code
 * validation_mode} ; {@code null} → {@link ModeValidation#NON_VALIDE}). Les colonnes numériques
 * optionnelles ({@code REAL} / {@code INTEGER} nullable) sont des types wrapper, {@code null} si
 * absentes.
 *
 * @param id clé technique, {@code null} avant insertion
 * @param idSequence séquence d'écoute source (FK → {@code listening_sequence.id}, obligatoire)
 * @param debutS temps de début dans la séquence en secondes (optionnel)
 * @param finS temps de fin dans la séquence en secondes (optionnel)
 * @param frequenceMedianeHz fréquence médiane en Hz (métrique Tadarida, optionnelle au schéma)
 * @param taxonTadarida code du taxon proposé par Tadarida (FK → {@code taxon.code}, obligatoire)
 * @param probTadarida probabilité Tadarida dans {@code [0,1]} (optionnelle)
 * @param taxonAutreTadarida code de la 2e proposition Tadarida (FK → {@code taxon.code}, optionnel)
 * @param taxonObservateur code saisi par l'observateur (FK → {@code taxon.code}, optionnel)
 * @param probObservateur probabilité saisie par l'observateur dans {@code [0,1]} (optionnelle)
 * @param commentaire commentaire libre de l'observateur (optionnel)
 * @param reference marquée comme référence dans la bibliothèque de sons (défaut {@code false})
 * @param modeValidation mode de validation (R24 : manuel / auto / non validé)
 * @param idResultats résultats d'identification agrégateurs (FK → {@code
 *     identification_results.id})
 */
public record Observation(
    Long id,
    Long idSequence,
    Double debutS,
    Double finS,
    Integer frequenceMedianeHz,
    String taxonTadarida,
    Double probTadarida,
    String taxonAutreTadarida,
    String taxonObservateur,
    Double probObservateur,
    String commentaire,
    boolean reference,
    ModeValidation modeValidation,
    Long idResultats) {}

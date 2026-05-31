package fr.univ_amu.iut.validation.model;

import fr.univ_amu.iut.commun.model.ModeValidation;

/**
 * Projection d'une <b>ligne</b> du CSV Tadarida, avant persistance (résultat de {@code
 * ParserCsvTadarida}). C'est l'image fidèle d'une ligne, indépendante de la base : elle porte le
 * <b>nom de la séquence</b> source (colonne {@code nom du fichier}) plutôt qu'une clé technique
 * {@code idSequence}, car le parseur ne connaît pas encore la base.
 *
 * <p>Le service {@code ServiceValidation} convertit ensuite chaque {@link LigneObservation} en une
 * {@link Observation} complète une fois la séquence ({@code listening_sequence}) et les résultats
 * ({@code identification_results}) résolus en base. Inversement, l'export {@code ExportVuCsv}
 * reconstitue des {@link LigneObservation} à partir des {@link Observation} relues (le nom de
 * séquence est restitué depuis la {@code SequenceDao}).
 *
 * <p>Conventions de nullité (alignées sur les colonnes nullable du schéma) :
 *
 * <ul>
 *   <li>{@code taxonAutreTadarida} : conservé <b>tel quel</b>, y compris quand Tadarida propose une
 *       liste de candidats séparés par des virgules (ex. {@code "Tetvir, Pippip, Phogri"}) ; {@code
 *       null} si la colonne est vide. (La persistance en {@link Observation} ne retient qu'un code
 *       FK unique : la conversion incombe au service.)
 *   <li>{@code taxonObservateur} / {@code probObservateur} : {@code null} tant que l'observateur
 *       n'a pas tranché (cas d'un fichier Brut ou d'une ligne non touchée, R17).
 *   <li>{@code frequenceMedianeHz} : {@link Integer} (la colonne {@code median_freq_hz} est {@code
 *       INTEGER}) ; un éventuel {@code "153.0"} du CSV est arrondi à l'entier le plus proche.
 * </ul>
 *
 * @param nomSequence nom de fichier de la séquence d'écoute source (sans clé technique)
 * @param debutS temps de début dans la séquence en secondes (optionnel)
 * @param finS temps de fin dans la séquence en secondes (optionnel)
 * @param frequenceMedianeHz fréquence médiane (métrique Tadarida, optionnelle)
 * @param taxonTadarida code du taxon proposé par Tadarida (obligatoire)
 * @param probTadarida probabilité Tadarida dans {@code [0,1]} (optionnelle)
 * @param taxonAutreTadarida 2e proposition Tadarida, brute (optionnelle, parfois multi-valuée)
 * @param taxonObservateur code saisi par l'observateur (optionnel, R15/R16)
 * @param probObservateur probabilité saisie par l'observateur (optionnelle)
 * @param modeValidation mode de validation (R24 : manuel / auto / non validé)
 */
public record LigneObservation(
    String nomSequence,
    Double debutS,
    Double finS,
    Integer frequenceMedianeHz,
    String taxonTadarida,
    Double probTadarida,
    String taxonAutreTadarida,
    String taxonObservateur,
    Double probObservateur,
    ModeValidation modeValidation) {}

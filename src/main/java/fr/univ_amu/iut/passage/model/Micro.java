package fr.univ_amu.iut.passage.model;

/**
 * Micro monté sur un enregistreur (C4bis, table {@code microphone}).
 *
 * <p>Un enregistreur ne porte qu'un seul micro actif à un instant donné, mais le matériel peut être
 * remplacé au fil du temps : l'historisation se fait via {@link #actif} (un seul {@code true} par
 * enregistreur) et {@link #retireLe} (date de fin de service). On garde donc la trace des anciens
 * micros sans les supprimer.
 *
 * @param id clé technique, {@code null} avant insertion
 * @param modeleReference modèle / référence (obligatoire, ex. {@code Knowles FG-23329})
 * @param bandePassante bande passante (optionnel, ex. {@code 8-150 kHz})
 * @param sensibilite sensibilité (optionnel, ex. {@code -42 dBV/Pa @ 1 kHz})
 * @param miseEnServiceLe date de mise en service (ISO {@code AAAA-MM-JJ}, optionnel)
 * @param retireLe date de retrait du service (ISO {@code AAAA-MM-JJ}, {@code null} si encore monté)
 * @param actif {@code true} si c'est le micro actuellement monté sur l'enregistreur
 * @param commentaire commentaire libre (optionnel)
 * @param idEnregistreur n° de série de l'enregistreur porteur (FK → {@code recorder.serial_number})
 */
public record Micro(
    Long id,
    String modeleReference,
    String bandePassante,
    String sensibilite,
    String miseEnServiceLe,
    String retireLe,
    boolean actif,
    String commentaire,
    String idEnregistreur) {}

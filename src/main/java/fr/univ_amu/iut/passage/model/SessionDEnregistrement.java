package fr.univ_amu.iut.passage.model;

/**
 * Session d'enregistrement : agrégat de données produit par un passage (C6, table {@code
 * recording_session}). Relation <b>1:1 stricte</b> avec le passage ({@code passage_id} unique) : la
 * session regroupe les enregistrements originaux, les séquences d'écoute, le journal du capteur et
 * l'éventuel relevé climatique d'une même nuit.
 *
 * <p>Les volumes ({@link #volumeOriginauxOctets}, {@link #volumeSequencesOctets}) sont des champs
 * <b>dérivés</b> (calculés à partir des fichiers sur disque), non autoritaires : le DAO se contente
 * de les mapper, sans garantir leur recalcul. Ils sont donc nullables.
 *
 * @param id clé technique, {@code null} avant insertion
 * @param cheminRacine chemin du sous-dossier workspace de la session (R22)
 * @param volumeOriginauxOctets volume total des originaux en octets (dérivé, optionnel)
 * @param volumeSequencesOctets volume total des séquences en octets (dérivé, optionnel)
 * @param idPassage identifiant du passage producteur (FK → {@code passage.id}, unique)
 */
public record SessionDEnregistrement(
    Long id,
    String cheminRacine,
    Long volumeOriginauxOctets,
    Long volumeSequencesOctets,
    Long idPassage) {}

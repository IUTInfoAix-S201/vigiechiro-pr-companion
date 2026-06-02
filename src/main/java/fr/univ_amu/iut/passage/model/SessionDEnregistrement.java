package fr.univ_amu.iut.passage.model;

/// Session d'enregistrement : agrégat de données produit par un passage (C6, table
/// `recording_session`). Relation **1:1 stricte** avec le passage (`passage_id` unique) : la
/// session regroupe les enregistrements originaux, les séquences d'écoute, le journal du capteur
/// et l'éventuel relevé climatique d'une même nuit.
///
/// Les volumes ([#volumeOriginauxOctets], [#volumeSequencesOctets]) sont des champs **dérivés**
/// (calculés à partir des fichiers sur disque), non autoritaires : le DAO se contente de les
/// mapper, sans garantir leur recalcul. Ils sont donc nullables.
///
/// @param id clé technique, `null` avant insertion
/// @param cheminRacine chemin du sous-dossier workspace de la session (R22)
/// @param volumeOriginauxOctets volume total des originaux en octets (dérivé, optionnel)
/// @param volumeSequencesOctets volume total des séquences en octets (dérivé, optionnel)
/// @param idPassage identifiant du passage producteur (FK → `passage.id`, unique)
public record SessionDEnregistrement(
        Long id, String cheminRacine, Long volumeOriginauxOctets, Long volumeSequencesOctets, Long idPassage) {}

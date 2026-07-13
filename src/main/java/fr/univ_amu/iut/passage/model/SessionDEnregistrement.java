package fr.univ_amu.iut.passage.model;

import java.time.LocalDateTime;

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
/// @param horodatageArchivage moment où l'utilisateur a **archivé** le passage (#1300), `null` s'il
///     ne l'a jamais fait. C'est le **geste volontaire** qui est enregistré, pas un état observé :
///     la disponibilité réelle de l'audio reste calculée sur disque (`DisponibiliteAudio`, #1298),
///     et c'est ce marqueur qui distingue un passage archivé d'un passage corrompu (#1303)
public record SessionDEnregistrement(
        Long id,
        String cheminRacine,
        Long volumeOriginauxOctets,
        Long volumeSequencesOctets,
        Long idPassage,
        LocalDateTime horodatageArchivage) {

    /// Constructeur de **compatibilité** (sans horodatage d'archivage) : préserve les appels
    /// antérieurs à #1300 (le marqueur vaut `null`, posé uniquement par l'archivage). Voir
    /// [#horodatageArchivage].
    public SessionDEnregistrement(
            Long id, String cheminRacine, Long volumeOriginauxOctets, Long volumeSequencesOctets, Long idPassage) {
        this(id, cheminRacine, volumeOriginauxOctets, volumeSequencesOctets, idPassage, null);
    }

    /// Le passage a-t-il été **archivé volontairement** (audio purgé pour libérer l'espace, #1300) ?
    public boolean archivee() {
        return horodatageArchivage != null;
    }
}

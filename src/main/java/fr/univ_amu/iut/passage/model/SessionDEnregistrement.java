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
/// @param horodatagePurgeOriginaux moment où les **bruts** de la session ont été purgés
///     volontairement (#1303 : même mécanisme déclaré que l'archivage, en remplacement de
///     l'heuristique `volume == 0`), `null` s'ils ne l'ont jamais été
public record SessionDEnregistrement(
        Long id,
        String cheminRacine,
        Long volumeOriginauxOctets,
        Long volumeSequencesOctets,
        Long idPassage,
        LocalDateTime horodatageArchivage,
        LocalDateTime horodatagePurgeOriginaux) {

    /// Constructeur de **compatibilité** (sans horodatage d'archivage ni de purge) : préserve les
    /// appels antérieurs à #1300 (les marqueurs valent `null`, posés uniquement par l'archivage et
    /// la purge). Voir [#horodatageArchivage] et [#horodatagePurgeOriginaux].
    public SessionDEnregistrement(
            Long id, String cheminRacine, Long volumeOriginauxOctets, Long volumeSequencesOctets, Long idPassage) {
        this(id, cheminRacine, volumeOriginauxOctets, volumeSequencesOctets, idPassage, null, null);
    }

    /// Constructeur de **compatibilité** (sans horodatage de purge des originaux) : préserve les
    /// appels antérieurs à #1303. Voir [#horodatagePurgeOriginaux].
    public SessionDEnregistrement(
            Long id,
            String cheminRacine,
            Long volumeOriginauxOctets,
            Long volumeSequencesOctets,
            Long idPassage,
            LocalDateTime horodatageArchivage) {
        this(id, cheminRacine, volumeOriginauxOctets, volumeSequencesOctets, idPassage, horodatageArchivage, null);
    }

    /// Le passage a-t-il été **archivé volontairement** (audio purgé pour libérer l'espace, #1300) ?
    public boolean archivee() {
        return horodatageArchivage != null;
    }

    /// Les bruts de la session ont-ils été **purgés volontairement** (#1303) ? Fait déclaré : c'est
    /// lui qui fait taire le contrôle d'existence des originaux dans l'audit, plus l'heuristique de
    /// volume.
    public boolean originauxPurges() {
        return horodatagePurgeOriginaux != null;
    }
}

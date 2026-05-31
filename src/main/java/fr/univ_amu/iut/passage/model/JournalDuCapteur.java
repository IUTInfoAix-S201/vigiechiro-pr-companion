package fr.univ_amu.iut.passage.model;

/// Journal du capteur : journal technique du PR pour la nuit, parsé depuis le fichier
/// `LogPR<n>.txt` du firmware Teensy (C9, table `sensor_log`). Relation **1:1** avec la session
/// (`session_id` unique). C'est lui qui fournit l'identité de l'[Enregistreur].
///
/// Les champs [#evenementsParses] et [#anomaliesDetectees] sont sérialisés en `TEXT` JSON ;
/// `anomaliesDetectees` est **dérivé** du parsing des évènements (réveils non programmés, erreurs
/// SD…) et simplement mappé ici.
///
/// @param id clé technique, `null` avant insertion
/// @param cheminFichier chemin du fichier `LogPR<n>.txt` à la racine de la session (R22)
/// @param evenementsParses évènements parsés sérialisés en JSON (optionnel)
/// @param anomaliesDetectees anomalies détectées sérialisées en JSON (dérivé, optionnel)
/// @param idSession identifiant de la session référencée (FK → `recording_session.id`, unique)
public record JournalDuCapteur(
    Long id,
    String cheminFichier,
    String evenementsParses,
    String anomaliesDetectees,
    Long idSession) {}

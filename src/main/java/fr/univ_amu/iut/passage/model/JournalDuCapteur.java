package fr.univ_amu.iut.passage.model;

/**
 * Journal du capteur : journal technique du PR pour la nuit, parsé depuis le fichier {@code
 * LogPR<n>.txt} du firmware Teensy (C9, table {@code sensor_log}). Relation <b>1:1</b> avec la
 * session ({@code session_id} unique). C'est lui qui fournit l'identité de l'{@link Enregistreur}.
 *
 * <p>Les champs {@link #evenementsParses} et {@link #anomaliesDetectees} sont sérialisés en {@code
 * TEXT} JSON ; {@code anomaliesDetectees} est <b>dérivé</b> du parsing des évènements (réveils non
 * programmés, erreurs SD…) et simplement mappé ici.
 *
 * @param id clé technique, {@code null} avant insertion
 * @param cheminFichier chemin du fichier {@code LogPR<n>.txt} à la racine de la session (R22)
 * @param evenementsParses évènements parsés sérialisés en JSON (optionnel)
 * @param anomaliesDetectees anomalies détectées sérialisées en JSON (dérivé, optionnel)
 * @param idSession identifiant de la session référencée (FK → {@code recording_session.id}, unique)
 */
public record JournalDuCapteur(
    Long id,
    String cheminFichier,
    String evenementsParses,
    String anomaliesDetectees,
    Long idSession) {}

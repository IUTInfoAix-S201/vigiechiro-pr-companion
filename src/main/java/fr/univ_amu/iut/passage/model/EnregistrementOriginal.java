package fr.univ_amu.iut.passage.model;

/**
 * Enregistrement original : fichier audio brut sortant de l'enregistreur après copie protégée et
 * renommage (C7, table {@code original_recording}). Ultrason mono 16 bits à 384 kHz,
 * <b>inaudible</b> sans transformation, conservé intact comme référence ultime.
 *
 * <p>Rattaché à une session ({@code session_id}, {@code ON DELETE CASCADE}) ; un original est
 * ensuite découpé en plusieurs {@link SequenceDEcoute}.
 *
 * @param id clé technique, {@code null} avant insertion
 * @param nomFichier nom de fichier (préfixe R6 + suffixe enregistreur R7)
 * @param cheminFichier chemin sur disque, sous-dossier {@code bruts/} (R22)
 * @param dureeSecondes durée en secondes (optionnel, typiquement 2-30 s)
 * @param frequenceEchantillonnageHz fréquence d'échantillonnage en Hz (optionnel, ex. 384000)
 * @param sha256 empreinte SHA-256 hexadécimale (optionnel, intégrité bit-à-bit)
 * @param idSession identifiant de la session contenante (FK → {@code recording_session.id})
 */
public record EnregistrementOriginal(
    Long id,
    String nomFichier,
    String cheminFichier,
    Double dureeSecondes,
    Integer frequenceEchantillonnageHz,
    String sha256,
    Long idSession) {}

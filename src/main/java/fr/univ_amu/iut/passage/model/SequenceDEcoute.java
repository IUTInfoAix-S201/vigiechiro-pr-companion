package fr.univ_amu.iut.passage.model;

/**
 * Séquence d'écoute : fichier dérivé d'un enregistrement original par expansion de temps ×10 et
 * découpage en tranches de 5 s (C8, table {@code listening_sequence}). <b>Audible</b> : c'est ce
 * qui est déposé sur Vigie-Chiro et analysé par Tadarida.
 *
 * <p>Doublement rattachée : à sa session ({@code session_id}) et à son original source ({@code
 * original_recording_id}), les deux en {@code ON DELETE CASCADE}. Le {@link #offsetSourceSecondes}
 * est un champ <b>dérivé</b> (position avant le ×10), simplement mappé ici. Le drapeau {@link
 * #dansSelection} dénormalise l'appartenance à une sélection d'écoute.
 *
 * @param id clé technique, {@code null} avant insertion
 * @param nomFichier nom de fichier (nom de l'original + suffixe {@code _000}, {@code _001}…, R8)
 * @param idEnregistrementOriginal identifiant de l'original source (FK → {@code
 *     original_recording.id})
 * @param indexSource index (≥ 0) de la séquence dans l'original (optionnel)
 * @param offsetSourceSecondes offset temporel dans le source en secondes (dérivé, optionnel)
 * @param dureeSecondes durée en secondes (optionnel, typiquement 5 s)
 * @param cheminFichier chemin sur disque, sous-dossier {@code transformes/} (R22)
 * @param dansSelection {@code true} si la séquence fait partie de la sélection d'écoute
 * @param idSession identifiant de la session contenante (FK → {@code recording_session.id})
 */
public record SequenceDEcoute(
    Long id,
    String nomFichier,
    Long idEnregistrementOriginal,
    Integer indexSource,
    Double offsetSourceSecondes,
    Double dureeSecondes,
    String cheminFichier,
    boolean dansSelection,
    Long idSession) {}

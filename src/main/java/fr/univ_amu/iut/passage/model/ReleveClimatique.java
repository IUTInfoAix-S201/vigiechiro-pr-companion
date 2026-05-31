package fr.univ_amu.iut.passage.model;

/**
 * Relevé climatique : journal T°/hygrométrie de la sonde embarquée, parsé depuis le fichier {@code
 * PaRecPR<sn>_THLog.csv} (C10, table {@code climate_log}). Relation <b>0:1</b> avec la session
 * ({@code session_id} unique mais optionnel côté session) : la sonde peut être absente ou
 * défaillante (R20).
 *
 * <p>Le champ {@link #mesures} porte la série temporelle (1 mesure / 600 s) sérialisée en {@code
 * TEXT} JSON, simplement transportée par le modèle.
 *
 * @param id clé technique, {@code null} avant insertion
 * @param cheminFichier chemin du fichier {@code _THLog.csv} à la racine de la session (R22)
 * @param mesures série de mesures (date, heure, T°, humidité) sérialisée en JSON (optionnel)
 * @param idSession identifiant de la session référencée (FK → {@code recording_session.id}, unique)
 */
public record ReleveClimatique(Long id, String cheminFichier, String mesures, Long idSession) {}

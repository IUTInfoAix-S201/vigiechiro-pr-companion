package fr.univ_amu.iut.passage.model;

/// Relevé climatique : journal T°/hygrométrie de la sonde embarquée, parsé depuis le fichier
/// `PaRecPR<sn>_THLog.csv` (C10, table `climate_log`). Relation **0:1** avec la session
/// (`session_id` unique mais optionnel côté session) : la sonde peut être absente ou défaillante
/// (R20).
///
/// Le champ [#mesures] porte la série temporelle (1 mesure / 600 s) sérialisée en `TEXT` JSON,
/// simplement transportée par le modèle.
///
/// @param id clé technique, `null` avant insertion
/// @param cheminFichier chemin du fichier `_THLog.csv` à la racine de la session (R22)
/// @param mesures série de mesures (date, heure, T°, humidité) sérialisée en JSON (optionnel)
/// @param idSession identifiant de la session référencée (FK → `recording_session.id`, unique)
public record ReleveClimatique(Long id, String cheminFichier, String mesures, Long idSession) {}

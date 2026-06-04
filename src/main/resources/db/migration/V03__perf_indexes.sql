-- ============================================================================
-- Index de performance (#28) : colonnes filtrees par les DAO et non encore
-- indexees. SQLite n'indexe pas automatiquement les cles etrangeres (seuls
-- PRIMARY KEY et UNIQUE le sont) : on indexe explicitement les colonnes des
-- WHERE ... = ? reperes en croisant les DAO avec le schema (cf. #28).
-- Effet mesurable via le banc (#29) : SCAN -> SEARCH ... USING INDEX.
-- Sous-ensemble prioritaire (impact fort/moyen) ; les colonnes a impact faible
-- (microphone.recorder_id, monitoring_site.user_id, taxon.group_id) sont laissees.
-- ============================================================================

-- Selection des observations d'un jeu de resultats (O5, ~4031 obs) : etait un SCAN.
CREATE INDEX idx_obs_results ON observation(results_id);

-- Listes de passages filtrees (P5/O5) : par enregistreur et par statut de workflow.
CREATE INDEX idx_passage_recorder ON passage(recorder_id);
CREATE INDEX idx_passage_status ON passage(workflow_status);

-- Parcours de la chaine d'import (originaux d'une session, sequences d'un original).
CREATE INDEX idx_original_session ON original_recording(session_id);
CREATE INDEX idx_seq_original ON listening_sequence(original_recording_id);

-- Points d'un site (detail d'un site).
CREATE INDEX idx_point_site ON listening_point(site_id);

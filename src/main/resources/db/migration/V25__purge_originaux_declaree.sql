-- V25 - La purge des originaux devient un fait DECLARE (#1303, EPIC #1297).
--
-- L'audit distinguait « bruts purges volontairement » d'une disparition subie par une HEURISTIQUE
-- (originals_total_bytes = 0) : une deduction, pas une declaration. #1300 a introduit le marqueur
-- explicite archived_at pour l'archivage ; cette migration fait converger les bruts vers le meme
-- mecanisme, comme l'exige #1303 : originals_purged_at enregistre le GESTE (horodatage ISO du
-- moment ou l'utilisateur a purge), NULL = jamais purge, toute disparition de bruts est alors un
-- vrai probleme a signaler.
--
-- Retro-declaration : les sessions dont le volume est deja a zero ont ete purgees par les flux
-- existants (purge par nuit, archivage) - le geste etait volontaire, on le declare une fois pour
-- toutes a la date de migration. Les sessions purgees par la PURGE GLOBALE d'avant cette version ne
-- sont pas detectables (elle n'ecrivait rien en base, volume reste errone) : rejouer la purge
-- globale les declare (idempotent).
--
-- NB : aucun point-virgule dans ces commentaires. Le decoupeur de MigrationSchema coupe les
-- instructions sur CHAQUE point-virgule, commentaires compris - un « ; » ici casse la migration.
ALTER TABLE recording_session ADD COLUMN originals_purged_at TEXT;
UPDATE recording_session SET originals_purged_at = strftime('%Y-%m-%dT%H:%M:%S', 'now') WHERE originals_total_bytes = 0;

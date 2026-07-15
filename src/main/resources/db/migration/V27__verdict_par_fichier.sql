-- V27 - Verdict par fichier son (chantier #1524, lot 5).
--
-- On passe d'un verdict global unique par passage (passage.verification_verdict) a un verdict PAR
-- FICHIER SON : une colonne sur la table de jonction selection_sequence, a la granularite de la
-- sequence de l'echantillon d'ecoute (on ne juge que ce qu'on ecoute). Le verdict FINAL du passage
-- sera derive de ces verdicts par fichier (agregation dans ServiceQualification) ; passage.
-- verification_verdict devient un cache derive persiste (inchange pour l'instant : lot 5 est additif,
-- l'IHM et le rebranchage de la source de verite viennent au lot 6).
--
-- Colonne nullable, defaut = NULL = "non juge" (VerdictFichier.NON_JUGE cote modele). Domaine des
-- valeurs non nulles : les libelles de VerdictFichier ('Bon' | 'Mauvais' | 'Inexploitable'), meme
-- convention de stockage que verification_verdict (on stocke le libelle de l'enum).

ALTER TABLE selection_sequence ADD COLUMN verdict TEXT;

-- Back-fill : diffuse le verdict deja rendu au niveau du passage vers ses sequences d'ecoute, pour que
-- la derivation reproduise l'existant. Correspondance ancien (libelle Verdict) -> cible (libelle
-- VerdictFichier) : OK -> Bon, Douteux -> Mauvais, "A jeter" -> Inexploitable. "A verifier" et les
-- passages non verifies restent NULL (non juge). SQLite : sous-requete correlee sur la jonction
-- selection -> passage.
UPDATE selection_sequence
SET verdict = (
  SELECT CASE p.verification_verdict
           WHEN 'OK' THEN 'Bon'
           WHEN 'Douteux' THEN 'Mauvais'
           WHEN 'À jeter' THEN 'Inexploitable'
         END
  FROM listening_selection ls
  JOIN passage p ON p.id = ls.passage_id
  WHERE ls.id = selection_sequence.selection_id
)
WHERE selection_id IN (
  SELECT ls.id
  FROM listening_selection ls
  JOIN passage p ON p.id = ls.passage_id
  WHERE p.verification_verdict IN ('OK', 'Douteux', 'À jeter')
);

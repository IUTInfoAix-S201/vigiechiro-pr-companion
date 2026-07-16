-- V28 - Bascule du lexique du verdict FINAL du passage (chantier #1524, lot 6b).
--
-- Les libelles visibles de l'enum Verdict passent a la cible : 'A verifier' -> 'Non verifie',
-- 'Douteux' -> 'Utilisable', 'A jeter' -> 'Inexploitable' ('OK' inchange). Les NOMS de constantes
-- (A_VERIFIER/OK/DOUTEUX/A_JETER) restent inchanges : les badges (classe CSS = name()), le tri par
-- ordinal et les vues de filtre sauvegardees (CriteresMultisite via valueOf(name())) ne sont donc pas
-- impactes. Seul le libelle STOCKE dans passage.verification_verdict doit etre reecrit (PassageDao
-- ecrit/lit le libelle via Verdict.libelle()/parLibelle).
--
-- selection_sequence.verdict stocke des libelles de VerdictFichier (Bon/Mauvais/Inexploitable),
-- INCHANGES : cette migration ne le touche pas. Les passages sans verdict restent NULL.

UPDATE passage SET verification_verdict = 'Non vérifié'   WHERE verification_verdict = 'À vérifier';
UPDATE passage SET verification_verdict = 'Utilisable'    WHERE verification_verdict = 'Douteux';
UPDATE passage SET verification_verdict = 'Inexploitable' WHERE verification_verdict = 'À jeter';

-- V16 - Groupe d'accueil des taxons synchronises depuis l'API VigieChiro (#717, axe 2).
--
-- La synchronisation du referentiel officiel (GET /taxons/liste) peut faire apparaitre des taxons
-- absents du seed V05. L'API ne fournit que le code (libelle_court) et le nom latin (libelle_long),
-- pas le groupe taxonomique : ces taxons sont donc rattaches a un groupe catch-all dedie, distinct de
-- « Hors referentiel » (V04, reserve aux codes Tadarida absents du referentiel). Miroir de V04.
--
-- INSERT OR IGNORE : idempotent, et sans contrainte d'unicite le doublon est evite par la presence
-- (au premier lancement le groupe n'existe pas ; aux suivants la migration n'est pas rejouee).
INSERT OR IGNORE INTO taxonomic_group (level, name) VALUES ('Catégorie', 'Référentiel VigieChiro');

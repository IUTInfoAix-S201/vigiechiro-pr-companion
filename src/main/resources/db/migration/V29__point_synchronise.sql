-- V29 - Origine des points d'ecoute (#1738) : distinguer les points RAPATRIES de la plateforme
-- (grille STOC, importee en masse par la synchro « mes sites ») des points ajoutes A LA MAIN.
--
-- Un carre Point Fixe rapatrie des dizaines de points ; l'observateur n'en utilise qu'un ou deux. La
-- fiche site masque desormais par defaut les points SYNCHRONISES non utilises (ils encombrent sans rien
-- apporter tant qu'aucune nuit ne s'y rattache), tout en gardant TOUJOURS visibles les points qu'on a
-- crees soi-meme. D'ou ce drapeau : 1 = rapatrie de la plateforme, 0 = ajoute manuellement.
ALTER TABLE listening_point ADD COLUMN synchronise INTEGER NOT NULL DEFAULT 0;

-- Reprise des points DEJA en base : ceux d'un site relie a VigieChiro sont repute rapatries. On ne sait
-- plus, retrospectivement, lesquels furent ajoutes a la main, mais la grille rapatriee est l'ecrasante
-- majorite et c'est precisement elle qui encombre ; les nouveaux points manuels, eux, naitront a 0.
UPDATE listening_point
   SET synchronise = 1
 WHERE site_id IN (SELECT CAST(ref_locale AS INTEGER) FROM vigiechiro_link WHERE entite = 'site');

-- V21 - Ancrage plateforme + certitude observateur (#1139, EPIC #1154).
--
-- Trois colonnes NULLABLE sur observation, toutes issues du contrat d'écriture VigieChiro (#1203) :
--
-- * vigiechiro_data_id : _id Eve de la donnée (le fichier WAV côté serveur) dont cette observation
--   est un sous-document. NULL tant que l'observation ne vient pas d'un import VigieChiro.
-- * vigiechiro_obs_index : indice BRUT de l'observation dans le tableau `observations` de sa donnée,
--   côté serveur. C'est l'identifiant positionnel attendu par PATCH /donnees/{id}/observations/{index}
--   (une observation n'a pas d'_id propre). Capturé sur le tableau JSON complet, pas sur la liste
--   filtrée par le parseur.
-- * observer_certainty : certitude déclarée MANUELLEMENT par l'observateur à la revue
--   (SUR | PROBABLE | POSSIBLE, jetons exacts du serveur). Vide par défaut, jamais dérivée de
--   prob_observer (qui reste la confiance numérique Tadarida, héritage du format _Vu).
--
-- L'ancrage n'est jamais préservé d'un import à l'autre : il vient frais du serveur à chaque import
-- (un re-compute régénère les donnees, donc les _id). La certitude, elle, est une décision humaine
-- préservée au ré-import (PreservationValidations).
ALTER TABLE observation ADD COLUMN vigiechiro_data_id TEXT;
ALTER TABLE observation ADD COLUMN vigiechiro_obs_index INTEGER;
ALTER TABLE observation ADD COLUMN observer_certainty TEXT;

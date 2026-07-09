-- V17 - Etat « verrouille » de l'objet VigieChiro rapproche (#718, axe 3).
--
-- Un site VigieChiro *verrouille* (valide par le MNHN) accepte le depot d'une participation ; c'est
-- l'information utile a un ancien utilisateur (« depot possible ») et le prealable de l'axe 4. On la
-- persiste ici, sur la correspondance, pour l'afficher (badge « Verrouille » vs « Enregistre ») sans
-- relire l'API a chaque affichage. Colonne nullable : elle ne concerne que les sites (entite='site'),
-- reste NULL pour les taxons, et NULL tant qu'aucune synchro n'a renseigne le flag.
ALTER TABLE vigiechiro_link ADD COLUMN verrouille INTEGER;

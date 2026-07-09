-- V15 - Table de correspondance locale <-> VigieChiro (#728, axe 1 « Fondations »).
--
-- Amorcee a la connexion (GET /taxons/liste et GET /moi/sites) pour relier nos entites locales
-- aux `objectid` MongoDB de la plateforme, prealable au referentiel taxons (axe 2), a l'import des
-- sites (axe 3) et au depot (axe 4).
--
-- Table generique volontairement decouplee : `entite` discrimine le type ('taxon' | 'site'), et
-- `ref_locale` porte la cle locale correspondante (taxon.code pour un taxon, monitoring_site.id en
-- texte pour un site). Pas de cle etrangere : une meme table ne peut pointer conditionnellement vers
-- deux tables distinctes, et un mapping obsolete (taxon retire du referentiel, site renomme) ne doit
-- pas bloquer une resynchronisation. La cle primaire composite (entite, ref_locale) garantit une
-- seule correspondance par entite locale (upsert idempotent).
CREATE TABLE vigiechiro_link (
    entite     TEXT NOT NULL,   -- 'taxon' | 'site'
    ref_locale TEXT NOT NULL,   -- taxon.code, ou monitoring_site.id en texte
    objectid   TEXT NOT NULL,   -- _id VigieChiro (ObjectId hexadecimal 24 caracteres)
    PRIMARY KEY (entite, ref_locale)
);

-- Recherche inverse objectid -> entite locale (recuperation des resultats/validations par objectid).
CREATE INDEX idx_vigiechiro_link_objectid ON vigiechiro_link (entite, objectid);

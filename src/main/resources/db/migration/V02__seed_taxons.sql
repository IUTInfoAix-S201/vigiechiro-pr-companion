-- ============================================================================
-- Donnees de reference taxonomiques (seedees a l'init).
-- 4 taxons fil rouge du dataset + 2 pseudo-taxons (noise, piaf).
-- Les group_id sont resolus par sous-requete (independant de l'auto-increment).
-- ============================================================================

-- Groupes taxonomiques (niveau Genre) des 4 especes fil rouge + un groupe pseudo-taxons.
INSERT INTO taxonomic_group (level, name) VALUES ('Genre', 'Pipistrellus');
INSERT INTO taxonomic_group (level, name) VALUES ('Genre', 'Nyctalus');
INSERT INTO taxonomic_group (level, name) VALUES ('Genre', 'Tadarida');
INSERT INTO taxonomic_group (level, name) VALUES ('Genre', 'Rhinolophus');
INSERT INTO taxonomic_group (level, name) VALUES ('Ordre', 'Pseudo-taxons');

-- 4 taxons fil rouge (code 6 lettres : 3 du genre + 3 de l'espece).
INSERT INTO taxon (code, latin_name, vernacular_name_fr, group_id)
VALUES ('Pippip', 'Pipistrellus pipistrellus', 'Pipistrelle commune',
        (SELECT id FROM taxonomic_group WHERE name = 'Pipistrellus'));

INSERT INTO taxon (code, latin_name, vernacular_name_fr, group_id)
VALUES ('Nyclei', 'Nyctalus leisleri', 'Noctule de Leisler',
        (SELECT id FROM taxonomic_group WHERE name = 'Nyctalus'));

INSERT INTO taxon (code, latin_name, vernacular_name_fr, group_id)
VALUES ('Tadten', 'Tadarida teniotis', 'Molosse de Cestoni',
        (SELECT id FROM taxonomic_group WHERE name = 'Tadarida'));

INSERT INTO taxon (code, latin_name, vernacular_name_fr, group_id)
VALUES ('Rhihip', 'Rhinolophus hipposideros', 'Petit rhinolophe',
        (SELECT id FROM taxonomic_group WHERE name = 'Rhinolophus'));

-- Pseudo-taxons Tadarida (pas des especes : bruit, oiseaux).
INSERT INTO taxon (code, latin_name, vernacular_name_fr, group_id)
VALUES ('noise', NULL, 'Bruit',
        (SELECT id FROM taxonomic_group WHERE name = 'Pseudo-taxons'));

INSERT INTO taxon (code, latin_name, vernacular_name_fr, group_id)
VALUES ('piaf', NULL, 'Oiseau',
        (SELECT id FROM taxonomic_group WHERE name = 'Pseudo-taxons'));

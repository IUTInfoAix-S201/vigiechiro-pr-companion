-- ============================================================================
-- Groupe d'accueil des taxons « hors référentiel » (#audio, import tolérant).
-- Un CSV Tadarida réel contient bien plus de taxons que les 4 espèces fil rouge
-- + 2 pseudo-taxons semés (V02). Plutôt que de rejeter l'import, on auto-enregistre
-- les codes inconnus comme taxons-souches (code seul) rattachés à CE groupe, pour
-- respecter la contrainte FK observation.taxon_tadarida -> taxon(code).
-- ============================================================================

INSERT INTO taxonomic_group (level, name) VALUES ('Inconnu', 'Hors référentiel');

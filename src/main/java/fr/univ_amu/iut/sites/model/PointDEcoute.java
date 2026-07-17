package fr.univ_amu.iut.sites.model;

/// Point d'écoute : emplacement précis dans un site (C3, table `listening_point`).
///
/// Le `code` (lettre + chiffre, R2) est unique **dans le site** (`UNIQUE(site_id, code)`).
/// Les coordonnées GPS sont optionnelles (`null`).
///
/// Le drapeau `synchronise` (#1738) distingue un point **rapatrié** de la plateforme (grille STOC
/// importée en masse par la synchro « mes sites ») d'un point **ajouté à la main**. La fiche site masque
/// par défaut les points synchronisés **non utilisés** (ils encombrent tant qu'aucune nuit ne s'y
/// rattache), mais garde toujours visibles ceux qu'on a créés soi-même.
///
/// @param id clé technique, `null` avant insertion
/// @param code code du point (lettre + chiffre, R2)
/// @param latitude latitude GPS (optionnelle)
/// @param longitude longitude GPS (optionnelle)
/// @param description descriptif (optionnel)
/// @param idSite identifiant du site parent (FK → `monitoring_site.id`)
/// @param synchronise `true` si rapatrié de VigieChiro, `false` si ajouté manuellement (#1738)
public record PointDEcoute(
        Long id, String code, Double latitude, Double longitude, String description, Long idSite, boolean synchronise) {

    /// Point **ajouté à la main** (`synchronise = false`) : le cas par défaut. Les points rapatriés de la
    /// plateforme passent par le constructeur canonique avec `synchronise = true` (cf. `RapprochementSites`).
    public PointDEcoute(Long id, String code, Double latitude, Double longitude, String description, Long idSite) {
        this(id, code, latitude, longitude, description, idSite, false);
    }
}

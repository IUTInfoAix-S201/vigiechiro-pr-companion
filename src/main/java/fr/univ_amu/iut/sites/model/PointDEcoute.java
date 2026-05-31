package fr.univ_amu.iut.sites.model;

/// Point d'écoute : emplacement précis dans un site (C3, table `listening_point`).
///
/// Le `code` (lettre + chiffre, R2) est unique **dans le site** (`UNIQUE(site_id, code)`).
/// Les coordonnées GPS sont optionnelles (`null`).
///
/// @param id clé technique, `null` avant insertion
/// @param code code du point (lettre + chiffre, R2)
/// @param latitude latitude GPS (optionnelle)
/// @param longitude longitude GPS (optionnelle)
/// @param description descriptif (optionnel)
/// @param idSite identifiant du site parent (FK → `monitoring_site.id`)
public record PointDEcoute(
    Long id, String code, Double latitude, Double longitude, String description, Long idSite) {}

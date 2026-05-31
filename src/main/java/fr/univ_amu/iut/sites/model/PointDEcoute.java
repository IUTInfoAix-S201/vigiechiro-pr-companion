package fr.univ_amu.iut.sites.model;

/**
 * Point d'écoute : emplacement précis dans un site (C3, table {@code listening_point}).
 *
 * <p>Le {@code code} (lettre + chiffre, R2) est unique <b>dans le site</b> ({@code UNIQUE(site_id,
 * code)}). Les coordonnées GPS sont optionnelles ({@code null}).
 *
 * @param id clé technique, {@code null} avant insertion
 * @param code code du point (lettre + chiffre, R2)
 * @param latitude latitude GPS (optionnelle)
 * @param longitude longitude GPS (optionnelle)
 * @param description descriptif (optionnel)
 * @param idSite identifiant du site parent (FK → {@code monitoring_site.id})
 */
public record PointDEcoute(
    Long id, String code, Double latitude, Double longitude, String description, Long idSite) {}

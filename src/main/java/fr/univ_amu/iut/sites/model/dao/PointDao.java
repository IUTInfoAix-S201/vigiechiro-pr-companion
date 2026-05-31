package fr.univ_amu.iut.sites.model.dao;

import fr.univ_amu.iut.commun.persistence.DaoGenerique;
import fr.univ_amu.iut.commun.persistence.RowMapper;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.sites.model.PointDEcoute;
import java.util.List;

/// DAO de l'entité [PointDEcoute] (table `listening_point`).
///
/// Illustre le mapping de colonnes **nullable** : les coordonnées GPS (`REAL`) peuvent être
/// absentes, on les lit donc via `rs.getObject(...)` (qui renvoie `null`) plutôt que
/// `rs.getDouble(...)` (qui renverrait 0.0).
public class PointDao extends DaoGenerique<PointDEcoute, Long> {

  private static final RowMapper<PointDEcoute> MAPPER =
      rs ->
          new PointDEcoute(
              rs.getLong("id"),
              rs.getString("code"),
              (Double) rs.getObject("gps_lat"),
              (Double) rs.getObject("gps_lon"),
              rs.getString("description"),
              rs.getLong("site_id"));

  public PointDao(SourceDeDonnees source) {
    super(source);
  }

  @Override
  protected String table() {
    return "listening_point";
  }

  @Override
  protected String colonneCle() {
    return "id";
  }

  @Override
  protected RowMapper<PointDEcoute> mapper() {
    return MAPPER;
  }

  /// Points d'écoute d'un site donné, triés par code.
  public List<PointDEcoute> findBySite(Long idSite) {
    return query("SELECT * FROM listening_point WHERE site_id = ? ORDER BY code", MAPPER, idSite);
  }

  @Override
  public PointDEcoute insert(PointDEcoute point) {
    long id =
        insererEtRecupererCle(
            "INSERT INTO listening_point (code, gps_lat, gps_lon, description, site_id)"
                + " VALUES (?, ?, ?, ?, ?)",
            point.code(),
            point.latitude(),
            point.longitude(),
            point.description(),
            point.idSite());
    return new PointDEcoute(
        id, point.code(), point.latitude(), point.longitude(), point.description(), point.idSite());
  }

  @Override
  public void update(PointDEcoute point) {
    executerMaj(
        "UPDATE listening_point SET code = ?, gps_lat = ?, gps_lon = ?, description = ?, site_id = ?"
            + " WHERE id = ?",
        point.code(),
        point.latitude(),
        point.longitude(),
        point.description(),
        point.idSite(),
        point.id());
  }
}

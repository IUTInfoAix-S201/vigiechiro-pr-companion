package fr.univ_amu.iut.passage.model.dao;

import fr.univ_amu.iut.commun.persistence.DaoGenerique;
import fr.univ_amu.iut.commun.persistence.RowMapper;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.passage.model.ReleveClimatique;
import java.util.Optional;

/// DAO de l'entité [ReleveClimatique] (table `climate_log`).
///
/// Relation 0:1 avec la session (`session_id` unique mais optionnel côté session : la sonde peut
/// manquer, R20), d'où [#trouverParSession(Long)]. La série de mesures `measurements` est
/// transportée comme du `TEXT` JSON brut.
public class ReleveClimatiqueDao extends DaoGenerique<ReleveClimatique, Long> {

  private static final RowMapper<ReleveClimatique> MAPPER =
      rs ->
          new ReleveClimatique(
              rs.getLong("id"),
              rs.getString("file_path"),
              rs.getString("measurements"),
              rs.getLong("session_id"));

  public ReleveClimatiqueDao(SourceDeDonnees source) {
    super(source);
  }

  @Override
  protected String table() {
    return "climate_log";
  }

  @Override
  protected String colonneCle() {
    return "id";
  }

  @Override
  protected RowMapper<ReleveClimatique> mapper() {
    return MAPPER;
  }

  /// Le relevé climatique de la session donnée, s'il existe (relation 0:1).
  public Optional<ReleveClimatique> trouverParSession(Long idSession) {
    return queryUnique("SELECT * FROM climate_log WHERE session_id = ?", MAPPER, idSession);
  }

  @Override
  public ReleveClimatique insert(ReleveClimatique releve) {
    long id =
        insererEtRecupererCle(
            "INSERT INTO climate_log (file_path, measurements, session_id) VALUES (?, ?, ?)",
            releve.cheminFichier(),
            releve.mesures(),
            releve.idSession());
    return new ReleveClimatique(id, releve.cheminFichier(), releve.mesures(), releve.idSession());
  }

  @Override
  public void update(ReleveClimatique releve) {
    executerMaj(
        "UPDATE climate_log SET file_path = ?, measurements = ?, session_id = ? WHERE id = ?",
        releve.cheminFichier(),
        releve.mesures(),
        releve.idSession(),
        releve.id());
  }
}

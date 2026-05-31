package fr.univ_amu.iut.passage.model.dao;

import fr.univ_amu.iut.commun.persistence.DaoGenerique;
import fr.univ_amu.iut.commun.persistence.RowMapper;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.passage.model.Micro;
import java.util.List;
import java.util.Optional;

/**
 * DAO de l'entité {@link Micro} (table {@code microphone}).
 *
 * <p>Illustre le mapping d'un <b>booléen</b> ({@code is_active}) : SQLite n'a pas de type booléen,
 * on stocke {@code 0}/{@code 1} ({@code micro.actif() ? 1 : 0}) et on relit via {@code
 * rs.getInt(...) != 0}. Les dates {@code commissioned_at}/{@code decommissioned_at} et les autres
 * champs optionnels sont nullables.
 */
public class MicroDao extends DaoGenerique<Micro, Long> {

  private static final RowMapper<Micro> MAPPER =
      rs ->
          new Micro(
              rs.getLong("id"),
              rs.getString("model_ref"),
              rs.getString("bandwidth"),
              rs.getString("sensitivity"),
              rs.getString("commissioned_at"),
              rs.getString("decommissioned_at"),
              rs.getInt("is_active") != 0,
              rs.getString("comment"),
              rs.getString("recorder_id"));

  public MicroDao(SourceDeDonnees source) {
    super(source);
  }

  @Override
  protected String table() {
    return "microphone";
  }

  @Override
  protected String colonneCle() {
    return "id";
  }

  @Override
  protected RowMapper<Micro> mapper() {
    return MAPPER;
  }

  /** Micros (actifs et retirés) montés sur un enregistreur, du plus récent au plus ancien. */
  public List<Micro> findByEnregistreur(String idEnregistreur) {
    return query(
        "SELECT * FROM microphone WHERE recorder_id = ? ORDER BY id DESC", MAPPER, idEnregistreur);
  }

  /** Le micro actuellement actif sur un enregistreur (au plus un par {@code is_active = 1}). */
  public Optional<Micro> trouverActifParEnregistreur(String idEnregistreur) {
    return queryUnique(
        "SELECT * FROM microphone WHERE recorder_id = ? AND is_active = 1", MAPPER, idEnregistreur);
  }

  @Override
  public Micro insert(Micro micro) {
    long id =
        insererEtRecupererCle(
            "INSERT INTO microphone"
                + " (model_ref, bandwidth, sensitivity, commissioned_at, decommissioned_at,"
                + " is_active, comment, recorder_id)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
            micro.modeleReference(),
            micro.bandePassante(),
            micro.sensibilite(),
            micro.miseEnServiceLe(),
            micro.retireLe(),
            micro.actif() ? 1 : 0,
            micro.commentaire(),
            micro.idEnregistreur());
    return new Micro(
        id,
        micro.modeleReference(),
        micro.bandePassante(),
        micro.sensibilite(),
        micro.miseEnServiceLe(),
        micro.retireLe(),
        micro.actif(),
        micro.commentaire(),
        micro.idEnregistreur());
  }

  @Override
  public void update(Micro micro) {
    executerMaj(
        "UPDATE microphone SET"
            + " model_ref = ?, bandwidth = ?, sensitivity = ?, commissioned_at = ?,"
            + " decommissioned_at = ?, is_active = ?, comment = ?, recorder_id = ?"
            + " WHERE id = ?",
        micro.modeleReference(),
        micro.bandePassante(),
        micro.sensibilite(),
        micro.miseEnServiceLe(),
        micro.retireLe(),
        micro.actif() ? 1 : 0,
        micro.commentaire(),
        micro.idEnregistreur(),
        micro.id());
  }
}

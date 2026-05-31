package fr.univ_amu.iut.passage.model.dao;

import fr.univ_amu.iut.commun.persistence.DaoGenerique;
import fr.univ_amu.iut.commun.persistence.RowMapper;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.passage.model.SessionDEnregistrement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * DAO de l'entité {@link SessionDEnregistrement} (table {@code recording_session}).
 *
 * <p>Relation 1:1 avec le passage : {@code passage_id} est unique, d'où la requête métier {@link
 * #trouverParPassage(Long)} qui renvoie un {@link Optional}. Les volumes (champs dérivés,
 * nullables) sont lus via {@link #lireLongNullable(ResultSet, String)} : le pilote SQLite renvoie
 * un {@code Integer} pour une petite valeur mais un {@code Long} pour une grande (un volume peut
 * atteindre ~40 Go), on convertit donc systématiquement via {@link Number} plutôt que par un cast
 * {@code (Long)} fragile.
 */
public class SessionDao extends DaoGenerique<SessionDEnregistrement, Long> {

  private static final RowMapper<SessionDEnregistrement> MAPPER =
      rs ->
          new SessionDEnregistrement(
              rs.getLong("id"),
              rs.getString("root_path"),
              lireLongNullable(rs, "originals_total_bytes"),
              lireLongNullable(rs, "sequences_total_bytes"),
              rs.getLong("passage_id"));

  /** Lit une colonne {@code INTEGER} nullable en {@link Long}, en préservant le {@code null}. */
  private static Long lireLongNullable(ResultSet rs, String colonne) throws SQLException {
    Object valeur = rs.getObject(colonne);
    return valeur == null ? null : ((Number) valeur).longValue();
  }

  public SessionDao(SourceDeDonnees source) {
    super(source);
  }

  @Override
  protected String table() {
    return "recording_session";
  }

  @Override
  protected String colonneCle() {
    return "id";
  }

  @Override
  protected RowMapper<SessionDEnregistrement> mapper() {
    return MAPPER;
  }

  /** La session du passage donné (relation 1:1, {@code passage_id} unique). */
  public Optional<SessionDEnregistrement> trouverParPassage(Long idPassage) {
    return queryUnique("SELECT * FROM recording_session WHERE passage_id = ?", MAPPER, idPassage);
  }

  @Override
  public SessionDEnregistrement insert(SessionDEnregistrement session) {
    long id =
        insererEtRecupererCle(
            "INSERT INTO recording_session"
                + " (root_path, originals_total_bytes, sequences_total_bytes, passage_id)"
                + " VALUES (?, ?, ?, ?)",
            session.cheminRacine(),
            session.volumeOriginauxOctets(),
            session.volumeSequencesOctets(),
            session.idPassage());
    return new SessionDEnregistrement(
        id,
        session.cheminRacine(),
        session.volumeOriginauxOctets(),
        session.volumeSequencesOctets(),
        session.idPassage());
  }

  @Override
  public void update(SessionDEnregistrement session) {
    executerMaj(
        "UPDATE recording_session SET"
            + " root_path = ?, originals_total_bytes = ?, sequences_total_bytes = ?, passage_id = ?"
            + " WHERE id = ?",
        session.cheminRacine(),
        session.volumeOriginauxOctets(),
        session.volumeSequencesOctets(),
        session.idPassage(),
        session.id());
  }
}

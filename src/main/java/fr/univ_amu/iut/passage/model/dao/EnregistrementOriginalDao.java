package fr.univ_amu.iut.passage.model.dao;

import fr.univ_amu.iut.commun.persistence.DaoGenerique;
import fr.univ_amu.iut.commun.persistence.RowMapper;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.passage.model.EnregistrementOriginal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO de l'entité {@link EnregistrementOriginal} (table {@code original_recording}).
 *
 * <p>Colonnes numériques nullables : {@code duration_s} ({@code REAL}) est lue via {@code
 * rs.getObject(...)}, et {@code sample_rate_hz} ({@code INTEGER}) via {@link #lireIntNullable(
 * ResultSet, String)} (le pilote SQLite pouvant renvoyer {@code Integer} ou {@code Long} selon la
 * magnitude). On distingue ainsi une valeur absente d'un zéro. Rattaché à une session ({@code ON
 * DELETE CASCADE}).
 */
public class EnregistrementOriginalDao extends DaoGenerique<EnregistrementOriginal, Long> {

  private static final RowMapper<EnregistrementOriginal> MAPPER =
      rs ->
          new EnregistrementOriginal(
              rs.getLong("id"),
              rs.getString("file_name"),
              rs.getString("file_path"),
              (Double) rs.getObject("duration_s"),
              lireIntNullable(rs, "sample_rate_hz"),
              rs.getString("sha256"),
              rs.getLong("session_id"));

  /** Lit une colonne {@code INTEGER} nullable en {@link Integer}, en préservant le {@code null}. */
  private static Integer lireIntNullable(ResultSet rs, String colonne) throws SQLException {
    Object valeur = rs.getObject(colonne);
    return valeur == null ? null : ((Number) valeur).intValue();
  }

  public EnregistrementOriginalDao(SourceDeDonnees source) {
    super(source);
  }

  @Override
  protected String table() {
    return "original_recording";
  }

  @Override
  protected String colonneCle() {
    return "id";
  }

  @Override
  protected RowMapper<EnregistrementOriginal> mapper() {
    return MAPPER;
  }

  /** Enregistrements originaux d'une session, triés par nom de fichier. */
  public List<EnregistrementOriginal> findBySession(Long idSession) {
    return query(
        "SELECT * FROM original_recording WHERE session_id = ? ORDER BY file_name",
        MAPPER,
        idSession);
  }

  @Override
  public EnregistrementOriginal insert(EnregistrementOriginal original) {
    long id =
        insererEtRecupererCle(
            "INSERT INTO original_recording"
                + " (file_name, file_path, duration_s, sample_rate_hz, sha256, session_id)"
                + " VALUES (?, ?, ?, ?, ?, ?)",
            original.nomFichier(),
            original.cheminFichier(),
            original.dureeSecondes(),
            original.frequenceEchantillonnageHz(),
            original.sha256(),
            original.idSession());
    return new EnregistrementOriginal(
        id,
        original.nomFichier(),
        original.cheminFichier(),
        original.dureeSecondes(),
        original.frequenceEchantillonnageHz(),
        original.sha256(),
        original.idSession());
  }

  @Override
  public void update(EnregistrementOriginal original) {
    executerMaj(
        "UPDATE original_recording SET"
            + " file_name = ?, file_path = ?, duration_s = ?, sample_rate_hz = ?, sha256 = ?,"
            + " session_id = ?"
            + " WHERE id = ?",
        original.nomFichier(),
        original.cheminFichier(),
        original.dureeSecondes(),
        original.frequenceEchantillonnageHz(),
        original.sha256(),
        original.idSession(),
        original.id());
  }
}

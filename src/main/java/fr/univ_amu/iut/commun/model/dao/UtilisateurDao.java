package fr.univ_amu.iut.commun.model.dao;

import fr.univ_amu.iut.commun.model.Utilisateur;
import fr.univ_amu.iut.commun.persistence.DaoGenerique;
import fr.univ_amu.iut.commun.persistence.RowMapper;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;

/**
 * DAO de l'entité {@link Utilisateur} (table {@code user}).
 *
 * <p>Placé dans {@code commun.model.dao} car l'utilisateur est transverse : les sites (et plus tard
 * d'autres features) référencent l'utilisateur courant par clé étrangère.
 *
 * <p>Contrairement aux DAO à clé auto-incrémentée, {@code user} a une <b>clé naturelle</b> ({@code
 * local_id}, un UUID en TEXT) : l'insertion ne récupère donc aucune clé générée, elle utilise
 * {@link #executerMaj(String, Object...)}.
 */
public class UtilisateurDao extends DaoGenerique<Utilisateur, String> {

  private static final RowMapper<Utilisateur> MAPPER =
      rs -> new Utilisateur(rs.getString("local_id"), rs.getString("display_name"));

  public UtilisateurDao(SourceDeDonnees source) {
    super(source);
  }

  @Override
  protected String table() {
    return "user";
  }

  @Override
  protected String colonneCle() {
    return "local_id";
  }

  @Override
  protected RowMapper<Utilisateur> mapper() {
    return MAPPER;
  }

  @Override
  public Utilisateur insert(Utilisateur utilisateur) {
    executerMaj(
        "INSERT INTO user (local_id, display_name) VALUES (?, ?)",
        utilisateur.localId(),
        utilisateur.displayName());
    return utilisateur;
  }

  @Override
  public void update(Utilisateur utilisateur) {
    executerMaj(
        "UPDATE user SET display_name = ? WHERE local_id = ?",
        utilisateur.displayName(),
        utilisateur.localId());
  }
}

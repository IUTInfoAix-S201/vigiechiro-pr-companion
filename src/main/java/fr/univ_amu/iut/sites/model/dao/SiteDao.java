package fr.univ_amu.iut.sites.model.dao;

import fr.univ_amu.iut.commun.model.Protocole;
import fr.univ_amu.iut.commun.persistence.DaoGenerique;
import fr.univ_amu.iut.commun.persistence.RowMapper;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.sites.model.Site;
import java.util.List;

/// DAO de l'entité [Site] (table `monitoring_site`). DAO de référence : c'est le patron à
/// recopier pour les autres entités à clé auto-incrémentée.
///
/// `findAll` / `findById` / `delete` sont hérités de [DaoGenerique]. Seules les écritures
/// dépendantes des colonnes sont écrites ici, en [java.sql.PreparedStatement] (le SQL métier
/// vit uniquement dans les DAO de feature).
///
/// Le [Protocole] est sérialisé via son `libelle()` dans la colonne `protocol`, et relu via
/// [Protocole#parLibelle(String)] : c'est le patron de mapping d'un énum.
public class SiteDao extends DaoGenerique<Site, Long> {

  private static final RowMapper<Site> MAPPER =
      rs ->
          new Site(
              rs.getLong("id"),
              rs.getString("square_number"),
              rs.getString("friendly_name"),
              Protocole.parLibelle(rs.getString("protocol")),
              rs.getString("comment"),
              rs.getString("created_at"),
              rs.getString("user_id"));

  public SiteDao(SourceDeDonnees source) {
    super(source);
  }

  @Override
  protected String table() {
    return "monitoring_site";
  }

  @Override
  protected String colonneCle() {
    return "id";
  }

  @Override
  protected RowMapper<Site> mapper() {
    return MAPPER;
  }

  /// Sites appartenant à un utilisateur donné, triés par numéro de carré.
  public List<Site> findByUtilisateur(String idUtilisateur) {
    return query(
        "SELECT * FROM monitoring_site WHERE user_id = ? ORDER BY square_number",
        MAPPER,
        idUtilisateur);
  }

  @Override
  public Site insert(Site site) {
    long id =
        insererEtRecupererCle(
            "INSERT INTO monitoring_site"
                + " (square_number, friendly_name, protocol, comment, created_at, user_id)"
                + " VALUES (?, ?, ?, ?, ?, ?)",
            site.numeroCarre(),
            site.nomConvivial(),
            site.protocole().libelle(),
            site.commentaire(),
            site.dateCreation(),
            site.idUtilisateur());
    return new Site(
        id,
        site.numeroCarre(),
        site.nomConvivial(),
        site.protocole(),
        site.commentaire(),
        site.dateCreation(),
        site.idUtilisateur());
  }

  @Override
  public void update(Site site) {
    executerMaj(
        "UPDATE monitoring_site SET"
            + " square_number = ?, friendly_name = ?, protocol = ?, comment = ?,"
            + " created_at = ?, user_id = ?"
            + " WHERE id = ?",
        site.numeroCarre(),
        site.nomConvivial(),
        site.protocole().libelle(),
        site.commentaire(),
        site.dateCreation(),
        site.idUtilisateur(),
        site.id());
  }
}

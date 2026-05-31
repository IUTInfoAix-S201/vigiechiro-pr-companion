package fr.univ_amu.iut.multisite.model.dao;

import fr.univ_amu.iut.commun.persistence.DaoGenerique;
import fr.univ_amu.iut.commun.persistence.RowMapper;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.multisite.model.SavedView;
import java.util.List;

/// DAO de l'entité [SavedView] (table `saved_view`). CRUD simple sur une table autonome
/// (sans clé étrangère) : le contenu des critères est conservé tel quel dans la colonne JSON
/// `filters_json`.
///
/// `findAll` / `findById` / `delete` sont hérités de [DaoGenerique].
/// Seules les écritures dépendantes des colonnes (`insert` / `update`) et la requête
/// métier [#findByNom(String)] sont écrites ici, en [java.sql.PreparedStatement].
public class SavedViewDao extends DaoGenerique<SavedView, Long> {

  private static final RowMapper<SavedView> MAPPER =
      rs -> new SavedView(rs.getLong("id"), rs.getString("name"), rs.getString("filters_json"));

  public SavedViewDao(SourceDeDonnees source) {
    super(source);
  }

  @Override
  protected String table() {
    return "saved_view";
  }

  @Override
  protected String colonneCle() {
    return "id";
  }

  @Override
  protected RowMapper<SavedView> mapper() {
    return MAPPER;
  }

  /// Vues enregistrées sous un nom donné, triées par identifiant (le nom n'est pas unique).
  public List<SavedView> findByNom(String nom) {
    return query("SELECT * FROM saved_view WHERE name = ? ORDER BY id", MAPPER, nom);
  }

  @Override
  public SavedView insert(SavedView vue) {
    long id =
        insererEtRecupererCle(
            "INSERT INTO saved_view (name, filters_json) VALUES (?, ?)",
            vue.nom(),
            vue.filtresJson());
    return new SavedView(id, vue.nom(), vue.filtresJson());
  }

  @Override
  public void update(SavedView vue) {
    executerMaj(
        "UPDATE saved_view SET name = ?, filters_json = ? WHERE id = ?",
        vue.nom(),
        vue.filtresJson(),
        vue.id());
  }
}

package fr.univ_amu.iut.sites;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.univ_amu.iut.commun.model.Protocole;
import fr.univ_amu.iut.commun.model.Utilisateur;
import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.commun.model.dao.UtilisateurDao;
import fr.univ_amu.iut.commun.persistence.DataAccessException;
import fr.univ_amu.iut.commun.persistence.MigrationSchema;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.sites.model.PointDEcoute;
import fr.univ_amu.iut.sites.model.Site;
import fr.univ_amu.iut.sites.model.dao.PointDao;
import fr.univ_amu.iut.sites.model.dao.SiteDao;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * CRUD du {@link PointDao} + contraintes : unicité {@code (site_id, code)}, FK vers le site, et
 * suppression en cascade quand le site parent est supprimé ({@code ON DELETE CASCADE}).
 */
class PointDaoTest {

  @TempDir Path dossier;
  private PointDao dao;
  private SiteDao siteDao;
  private Long idSite;

  @BeforeEach
  void preparer() {
    SourceDeDonnees source = new SourceDeDonnees(new Workspace(dossier));
    new MigrationSchema(source).migrer();
    new UtilisateurDao(source).insert(new Utilisateur("u-1", "Testeur"));
    siteDao = new SiteDao(source);
    Site site =
        siteDao.insert(
            new Site(null, "640380", "Étang", Protocole.STANDARD, null, "2026-05-01", "u-1"));
    idSite = site.id();
    dao = new PointDao(source);
  }

  private PointDEcoute nouveauPoint(String code) {
    return new PointDEcoute(null, code, 43.52, 5.45, "Lisière", idSite);
  }

  @Test
  void inserer_attribue_un_id_et_rend_le_point_relisible() {
    PointDEcoute insere = dao.insert(nouveauPoint("A1"));

    assertThat(insere.id()).isNotNull();
    PointDEcoute relu = dao.findById(insere.id()).orElseThrow();
    assertThat(relu.code()).isEqualTo("A1");
    assertThat(relu.latitude()).isEqualTo(43.52);
    assertThat(dao.findBySite(idSite)).extracting(PointDEcoute::code).containsExactly("A1");
  }

  @Test
  void coordonnees_gps_nulles_sont_persistees_comme_null() {
    PointDEcoute sansGps = new PointDEcoute(null, "B2", null, null, null, idSite);

    PointDEcoute relu = dao.findById(dao.insert(sansGps).id()).orElseThrow();

    assertThat(relu.latitude()).isNull();
    assertThat(relu.longitude()).isNull();
    assertThat(relu.description()).isNull();
  }

  @Test
  void unicite_du_code_dans_le_site_est_garantie() {
    dao.insert(nouveauPoint("A1"));

    assertThatThrownBy(() -> dao.insert(nouveauPoint("A1")))
        .as("UNIQUE(site_id, code) interdit deux points de même code dans un site")
        .isInstanceOf(DataAccessException.class);
  }

  @Test
  void clef_etrangere_active_un_site_inconnu_est_rejete() {
    PointDEcoute orphelin = new PointDEcoute(null, "C3", null, null, null, 9999L);

    assertThatThrownBy(() -> dao.insert(orphelin))
        .as("PRAGMA foreign_keys=ON doit refuser une FK vers un site absent")
        .isInstanceOf(DataAccessException.class);
  }

  @Test
  void supprimer_le_site_supprime_ses_points_en_cascade() {
    dao.insert(nouveauPoint("A1"));
    dao.insert(nouveauPoint("Z4"));
    assertThat(dao.findBySite(idSite)).hasSize(2);

    siteDao.delete(idSite);

    assertThat(dao.findBySite(idSite))
        .as("ON DELETE CASCADE doit avoir supprimé les points du site")
        .isEmpty();
  }
}

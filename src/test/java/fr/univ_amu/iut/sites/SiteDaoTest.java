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
import fr.univ_amu.iut.sites.model.Site;
import fr.univ_amu.iut.sites.model.dao.SiteDao;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * CRUD du {@link SiteDao} sur une base SQLite jetable (@TempDir), initialisée par {@link
 * MigrationSchema}. Vérifie aussi les contraintes d'intégrité (FK {@code foreign_keys=ON},
 * unicité).
 */
class SiteDaoTest {

  private static final String ID_USER = "u-1";

  @TempDir Path dossier;
  private SiteDao dao;

  @BeforeEach
  void preparer() {
    SourceDeDonnees source = new SourceDeDonnees(new Workspace(dossier));
    new MigrationSchema(source).migrer();
    // L'utilisateur propriétaire doit exister (FK monitoring_site.user_id).
    new UtilisateurDao(source).insert(new Utilisateur(ID_USER, "Testeur"));
    dao = new SiteDao(source);
  }

  private Site nouveauSite(String carre) {
    return new Site(null, carre, "Étang", Protocole.STANDARD, null, "2026-05-01", ID_USER);
  }

  @Test
  void inserer_attribue_un_id_et_rend_le_site_relisible() {
    Site insere = dao.insert(nouveauSite("640380"));

    assertThat(insere.id()).as("la clé auto-incrémentée est renseignée").isNotNull();
    Site relu = dao.findById(insere.id()).orElseThrow();
    assertThat(relu.numeroCarre()).isEqualTo("640380");
    assertThat(relu.protocole()).isEqualTo(Protocole.STANDARD);
    assertThat(dao.findByUtilisateur(ID_USER)).extracting(Site::numeroCarre).contains("640380");
  }

  @Test
  void mettre_a_jour_modifie_les_champs() {
    Site insere = dao.insert(nouveauSite("640380"));

    dao.update(
        new Site(
            insere.id(),
            "640380",
            "Étang rénové",
            Protocole.RECHERCHE,
            "Capteur déplacé",
            "2026-04-20",
            ID_USER));

    Site relu = dao.findById(insere.id()).orElseThrow();
    assertThat(relu.nomConvivial()).isEqualTo("Étang rénové");
    assertThat(relu.protocole()).isEqualTo(Protocole.RECHERCHE);
    assertThat(relu.commentaire()).isEqualTo("Capteur déplacé");
  }

  @Test
  void supprimer_retire_le_site() {
    Site insere = dao.insert(nouveauSite("123456"));
    assertThat(dao.findById(insere.id())).isPresent();

    dao.delete(insere.id());

    assertThat(dao.findById(insere.id())).isEmpty();
  }

  @Test
  void clef_etrangere_active_un_utilisateur_inconnu_est_rejete() {
    Site orphelin =
        new Site(null, "777777", null, Protocole.STANDARD, null, "2026-05-01", "inconnu");

    assertThatThrownBy(() -> dao.insert(orphelin))
        .as("PRAGMA foreign_keys=ON doit refuser une FK vers un utilisateur absent")
        .isInstanceOf(DataAccessException.class);
  }

  @Test
  void unicite_du_carre_par_utilisateur_est_garantie() {
    dao.insert(nouveauSite("888888"));

    assertThatThrownBy(() -> dao.insert(nouveauSite("888888")))
        .as("UNIQUE(user_id, square_number) interdit deux fois le même carré pour un user")
        .isInstanceOf(DataAccessException.class);
  }
}

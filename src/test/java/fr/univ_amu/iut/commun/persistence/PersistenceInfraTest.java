package fr.univ_amu.iut.commun.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.univ_amu.iut.commun.model.Utilisateur;
import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.commun.model.dao.UtilisateurDao;
import fr.univ_amu.iut.sites.model.Site;
import fr.univ_amu.iut.sites.model.dao.SiteDao;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/// Tests de l'infrastructure de persistance : migration/idempotence et atomicité transactionnelle.
class PersistenceInfraTest {

    @TempDir
    Path dossier;

    private int compterTaxons(SourceDeDonnees source) {
        try (Connection c = source.getConnection();
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM taxon")) {
            return rs.next() ? rs.getInt(1) : -1;
        } catch (SQLException e) {
            throw new DataAccessException("comptage taxons", e);
        }
    }

    @Test
    @DisplayName("La base est créée si absente puis réutilisée (migration idempotente)")
    void base_creee_puis_reutilisee() {
        Workspace workspace = new Workspace(dossier);
        SourceDeDonnees premiere = new SourceDeDonnees(workspace);
        new MigrationSchema(premiere).migrer();

        assertThat(Files.exists(workspace.cheminBaseDeDonnees())).isTrue();
        assertThat(compterTaxons(premiere))
                .as("4 taxons fil rouge + noise + piaf")
                .isEqualTo(6);

        // Réouverture (simule un redémarrage) : re-migrer ne doit pas re-seeder.
        SourceDeDonnees seconde = new SourceDeDonnees(workspace);
        new MigrationSchema(seconde).migrer();
        assertThat(compterTaxons(seconde))
                .as("pas de doublons après re-migration")
                .isEqualTo(6);
    }

    @Test
    @DisplayName("Une transaction qui échoue est intégralement annulée (rollback)")
    void transaction_rollback_atomique() {
        SourceDeDonnees source = new SourceDeDonnees(new Workspace(dossier));
        new MigrationSchema(source).migrer();
        new UtilisateurDao(source).insert(new Utilisateur("u-1", "Testeur"));
        UniteDeTravail uniteDeTravail = new UniteDeTravail(source);

        assertThatThrownBy(() -> uniteDeTravail.executer(connexion -> {
                    try (PreparedStatement ps = connexion.prepareStatement("INSERT INTO monitoring_site"
                            + " (square_number, protocol, created_at, user_id)"
                            + " VALUES ('999999', 'PointFixeStandard', '2026-01-01', 'u-1')")) {
                        ps.executeUpdate();
                    }
                    throw new IllegalStateException("panne simulée après insertion");
                }))
                .isInstanceOf(DataAccessException.class);

        assertThat(new SiteDao(source).findByUtilisateur("u-1"))
                .as("le rollback doit avoir annulé l'insertion du site")
                .isEmpty();
    }

    @Test
    @DisplayName("Une transaction réussie est bien validée (commit)")
    void transaction_commit() {
        SourceDeDonnees source = new SourceDeDonnees(new Workspace(dossier));
        new MigrationSchema(source).migrer();
        new UtilisateurDao(source).insert(new Utilisateur("u-1", "Testeur"));
        SiteDao siteDao = new SiteDao(source);

        new UniteDeTravail(source).executer(connexion -> {
            try (PreparedStatement ps = connexion.prepareStatement("INSERT INTO monitoring_site"
                    + " (square_number, protocol, created_at, user_id)"
                    + " VALUES ('111111', 'PointFixeStandard', '2026-01-01', 'u-1')")) {
                ps.executeUpdate();
            }
        });

        assertThat(siteDao.findByUtilisateur("u-1"))
                .extracting(Site::numeroCarre)
                .containsExactly("111111");
    }
}

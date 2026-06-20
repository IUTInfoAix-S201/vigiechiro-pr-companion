package fr.univ_amu.iut.commun.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.commun.model.Utilisateur;
import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.commun.model.dao.UtilisateurDao;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/// Vérifie le `COUNT(*)` générique [DaoGenerique#compter] sur une table réelle (la plus simple,
/// `utilisateur`) : base SQLite jetable sur `@TempDir`, schéma migré.
class DaoCompterTest {

    @Test
    @DisplayName("compter() renvoie 0 sur table vide puis le nombre de lignes insérées")
    void compter_compte_les_lignes(@TempDir Path tmp) {
        SourceDeDonnees source = new SourceDeDonnees(new Workspace(tmp));
        new MigrationSchema(source).migrer();
        UtilisateurDao dao = new UtilisateurDao(source);

        assertThat(dao.compter()).isZero();

        dao.insert(new Utilisateur("u1", "Alice"));
        dao.insert(new Utilisateur("u2", "Bob"));

        assertThat(dao.compter()).isEqualTo(2L);
    }
}

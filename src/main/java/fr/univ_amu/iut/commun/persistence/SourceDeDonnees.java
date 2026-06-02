package fr.univ_amu.iut.commun.persistence;

import fr.univ_amu.iut.commun.model.Workspace;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

/// Fournit des [Connection] vers la base SQLite du [Workspace].
///
/// C'est l'unique endroit qui connaît l'URL JDBC : les DAO, l'unité de travail et la migration de
/// schéma reçoivent une `SourceDeDonnees` et ignorent tout du driver. La source est binder en
/// **singleton** Guice (une seule base pour toute l'application).
///
/// Chaque connexion active l'intégrité référentielle (`PRAGMA foreign_keys = ON`) : SQLite
/// n'applique les clés étrangères que si on le demande explicitement (objectif qualité intégrité
/// O7). On le fait à deux niveaux par sécurité : via [SQLiteConfig#enforceForeignKeys(boolean)] et
/// via un `PRAGMA` explicite à l'ouverture.
public class SourceDeDonnees {

    private final Workspace workspace;
    private final SQLiteDataSource dataSource;

    /// Crée une source pointant vers `<workspace>/vigiechiro.db`. En test, on passe un `Workspace`
    /// construit sur un `@TempDir` (base jetable) ; en production, c'est le workspace
    /// par défaut.
    public SourceDeDonnees(Workspace workspace) {
        this.workspace = workspace;
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);
        SQLiteDataSource source = new SQLiteDataSource(config);
        source.setUrl("jdbc:sqlite:" + workspace.cheminBaseDeDonnees());
        this.dataSource = source;
    }

    /// Ouvre une nouvelle connexion (clés étrangères activées). L'appelant est responsable de la
    /// fermer (idéalement dans un `try-with-resources`).
    public Connection getConnection() {
        try {
            // Création paresseuse du workspace : la base ne peut exister sans son dossier.
            Files.createDirectories(workspace.racine());
            Connection connexion = dataSource.getConnection();
            try (Statement st = connexion.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
            return connexion;
        } catch (SQLException | IOException e) {
            throw new DataAccessException("Connexion SQLite impossible (" + workspace + ")", e);
        }
    }

    /// Workspace adossé à cette source (utile pour résoudre les chemins de sessions).
    public Workspace workspace() {
        return workspace;
    }
}

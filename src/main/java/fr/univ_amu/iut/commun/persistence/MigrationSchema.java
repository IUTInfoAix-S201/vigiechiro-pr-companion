package fr.univ_amu.iut.commun.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Applique les scripts de migration versionnés {@code src/main/resources/db/migration/V0x__*.sql}
 * et trace les versions appliquées dans la table {@code schema_version}.
 *
 * <p>Au premier lancement, la base est vide : {@code V01__schema.sql} crée toutes les tables (dont
 * {@code schema_version}), puis {@code V02__seed_taxons.sql} insère les données de référence. À la
 * réouverture d'une base existante, les versions déjà présentes sont ignorées (migration
 * idempotente, objectif disponibilité 5.2 : « base présente → réutilisée »).
 *
 * <p>Pour ajouter une migration : créer le fichier {@code V0n__xxx.sql} dans {@code db/migration/}
 * <b>et</b> ajouter son nom à {@link #MIGRATIONS} (l'ordre fait foi).
 */
public class MigrationSchema {

  /** Migrations appliquées dans l'ordre. Le préfixe {@code V0n} porte le numéro de version. */
  static final String[] MIGRATIONS = {"V01__schema.sql", "V02__seed_taxons.sql"};

  private static final String DOSSIER = "/db/migration/";

  private final SourceDeDonnees source;

  public MigrationSchema(SourceDeDonnees source) {
    this.source = source;
  }

  /** Applique toutes les migrations non encore enregistrées dans {@code schema_version}. */
  public void migrer() {
    Set<Integer> dejaAppliquees = versionsAppliquees();
    for (String fichier : MIGRATIONS) {
      int version = numeroVersion(fichier);
      if (dejaAppliquees.contains(version)) {
        continue;
      }
      executerScript(DOSSIER + fichier);
      enregistrerVersion(version);
    }
  }

  private Set<Integer> versionsAppliquees() {
    Set<Integer> versions = new HashSet<>();
    try (Connection connexion = source.getConnection();
        Statement st = connexion.createStatement();
        ResultSet rs = st.executeQuery("SELECT version FROM schema_version")) {
      while (rs.next()) {
        versions.add(rs.getInt(1));
      }
    } catch (SQLException tableAbsente) {
      // Premier lancement : la table schema_version n'existe pas encore. Aucune version appliquée.
      return Set.of();
    }
    return versions;
  }

  private void enregistrerVersion(int version) {
    String sql = "INSERT OR IGNORE INTO schema_version(version, applied_at) VALUES (?, ?)";
    try (Connection connexion = source.getConnection();
        PreparedStatement ps = connexion.prepareStatement(sql)) {
      ps.setInt(1, version);
      ps.setString(2, LocalDateTime.now().toString());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new DataAccessException("Impossible d'enregistrer la version " + version, e);
    }
  }

  private void executerScript(String ressource) {
    String contenu = lireRessource(ressource);
    try (Connection connexion = source.getConnection();
        Statement st = connexion.createStatement()) {
      for (String instruction : decouperInstructions(contenu)) {
        st.execute(instruction);
      }
    } catch (SQLException e) {
      throw new DataAccessException("Échec du script de migration " + ressource, e);
    }
  }

  private int numeroVersion(String fichier) {
    String numero = fichier.substring(1, fichier.indexOf("__"));
    return Integer.parseInt(numero);
  }

  private static String lireRessource(String chemin) {
    try (InputStream in = MigrationSchema.class.getResourceAsStream(chemin)) {
      if (in == null) {
        throw new IllegalStateException("Migration introuvable : " + chemin);
      }
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("Lecture impossible : " + chemin, e);
    }
  }

  /** Retire les lignes de commentaire pur ({@code --}) et découpe le script sur les {@code ;}. */
  private static String[] decouperInstructions(String sql) {
    StringBuilder sansCommentaires = new StringBuilder();
    for (String ligne : sql.split("\n")) {
      if (!ligne.strip().startsWith("--")) {
        sansCommentaires.append(ligne).append('\n');
      }
    }
    return Arrays.stream(sansCommentaires.toString().split(";"))
        .map(String::strip)
        .filter(instruction -> !instruction.isEmpty())
        .toArray(String[]::new);
  }
}

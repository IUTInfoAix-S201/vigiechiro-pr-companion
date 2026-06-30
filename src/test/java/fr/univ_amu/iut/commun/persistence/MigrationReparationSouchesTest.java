package fr.univ_amu.iut.commun.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.commun.model.Workspace;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/// Régression de la migration de réparation `V06__reparer_souches_referentiel.sql`.
///
/// Des imports antérieurs au seed officiel (V05) ont pu auto-enregistrer un vrai taxon (p. ex. `Rhifer`)
/// comme **souche** « Hors référentiel » (latin nul, vernaculaire « Hors référentiel »). Comme V05 et le
/// réimport posent leurs taxons en `INSERT OR IGNORE`, cette souche **masque** l'entrée officielle et n'est
/// jamais corrigée. On vérifie ici que rejouer le script V06 (UPSERT) **répare** une telle souche : le
/// code retrouve son nom latin, son vernaculaire et son groupe officiels, et plus aucun taxon ne conserve
/// le libellé trompeur « Hors référentiel ».
class MigrationReparationSouchesTest {

    private static final String V06 = "/db/migration/V06__reparer_souches_referentiel.sql";

    @TempDir
    Path dossier;

    private SourceDeDonnees source;

    @BeforeEach
    void preparer() {
        source = new SourceDeDonnees(new Workspace(dossier));
        new MigrationSchema(source).migrer();
    }

    @Test
    @DisplayName("V06 ré-affilie une souche qui masquait un taxon du référentiel officiel")
    void repare_une_souche_masquante() {
        // Simule l'état laissé par un import pré-V05 : Rhifer rétrogradé en souche « Hors référentiel ».
        executer("UPDATE taxon SET latin_name = NULL, vernacular_name_fr = 'Hors référentiel',"
                + " group_id = (SELECT id FROM taxonomic_group WHERE name = 'Hors référentiel')"
                + " WHERE code = 'Rhifer'");
        assertThat(groupeDe("Rhifer")).isEqualTo("Hors référentiel");

        // Rejoue la vraie migration de réparation.
        rejouerScript(V06);

        // Rhifer a retrouvé son identité officielle, et plus aucune souche ne porte l'ancien libellé.
        assertThat(vernaculaireDe("Rhifer")).isEqualTo("Grand Rhinolophe");
        assertThat(groupeDe("Rhifer")).isEqualTo("Chiroptères");
        assertThat(compter("SELECT COUNT(*) FROM taxon WHERE vernacular_name_fr = 'Hors référentiel'"))
                .isZero();
    }

    private void rejouerScript(String ressource) {
        for (String instruction : decouperInstructions(lireRessource(ressource))) {
            executer(instruction);
        }
    }

    private void executer(String sql) {
        try (Connection connexion = source.getConnection();
                Statement st = connexion.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new IllegalStateException("Échec SQL : " + sql, e);
        }
    }

    private String vernaculaireDe(String code) {
        return lireChaine("SELECT vernacular_name_fr FROM taxon WHERE code = '" + code + "'");
    }

    private String groupeDe(String code) {
        return lireChaine("SELECT g.name FROM taxon t JOIN taxonomic_group g ON g.id = t.group_id" + " WHERE t.code = '"
                + code + "'");
    }

    private String lireChaine(String sql) {
        try (Connection connexion = source.getConnection();
                Statement st = connexion.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getString(1) : null;
        } catch (SQLException e) {
            throw new IllegalStateException("Échec SQL : " + sql, e);
        }
    }

    private long compter(String sql) {
        try (Connection connexion = source.getConnection();
                Statement st = connexion.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            throw new IllegalStateException("Échec SQL : " + sql, e);
        }
    }

    private static String lireRessource(String chemin) {
        try (InputStream in = MigrationReparationSouchesTest.class.getResourceAsStream(chemin)) {
            return new String(Optional.ofNullable(in).orElseThrow().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Lecture impossible : " + chemin, e);
        }
    }

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

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

/// Régression du back-fill de `V27__verdict_par_fichier.sql` (#1524, lot 5) : à l'ajout de la colonne
/// `selection_sequence.verdict`, le verdict déjà rendu **au niveau du passage** est diffusé vers ses
/// séquences d'écoute (OK → Bon, Douteux → Mauvais, « À jeter » → Inexploitable ; « À vérifier » et non
/// vérifiés restent NULL). On seed des passages hérités puis on rejoue **l'UPDATE** du script (l'ALTER a
/// déjà eu lieu au `migrer()`).
class BackfillVerdictMigrationTest {

    private static final String V27 = "/db/migration/V27__verdict_par_fichier.sql";

    @TempDir
    Path dossier;

    private SourceDeDonnees source;

    @BeforeEach
    void preparer() {
        source = new SourceDeDonnees(new Workspace(dossier));
        new MigrationSchema(source).migrer();
    }

    @Test
    @DisplayName("V27 diffuse le verdict du passage vers ses séquences, préserve NULL")
    void backfill_diffuse_le_verdict_passage() {
        seedPassageAvecSequence(1, "À jeter", 10);
        seedPassageAvecSequence(2, "OK", 20);
        seedPassageAvecSequence(3, "Douteux", 30);
        seedPassageAvecSequence(4, "À vérifier", 40);
        seedPassageAvecSequence(5, null, 50);

        rejouerBackfill();

        assertThat(verdictSequence(10)).isEqualTo("Inexploitable");
        assertThat(verdictSequence(20)).isEqualTo("Bon");
        assertThat(verdictSequence(30)).isEqualTo("Mauvais");
        assertThat(verdictSequence(40)).as("À vérifier → non jugé (NULL)").isNull();
        assertThat(verdictSequence(50))
                .as("passage non vérifié → non jugé (NULL)")
                .isNull();
    }

    /// Seed d'un passage + sa sélection + une séquence rattachée (verdict NULL), FK désactivées : le
    /// back-fill est un UPDATE, la validité référentielle est hors sujet.
    private void seedPassageAvecSequence(int passageId, String verdictPassage, int sequenceId) {
        String verdictSql = verdictPassage == null ? "NULL" : "'" + verdictPassage + "'";
        seed(
                "INSERT INTO passage (id, passage_number, year, recording_date, start_time, end_time,"
                        + " workflow_status, verification_verdict, point_id, recorder_id) VALUES ("
                        + passageId + ", " + passageId + ", 2026, '2026-05-01', '20:00', '06:00', 'Vérifié', "
                        + verdictSql + ", 1, 'SM4')",
                "INSERT INTO listening_selection (id, selection_method, size, passage_id) VALUES (" + passageId
                        + ", 'Répartition temporelle', 1, " + passageId + ")",
                "INSERT INTO selection_sequence (selection_id, sequence_id, position, listened) VALUES (" + passageId
                        + ", " + sequenceId + ", 0, 0)");
    }

    private void seed(String... inserts) {
        try (Connection connexion = source.getConnection();
                Statement st = connexion.createStatement()) {
            st.execute("PRAGMA foreign_keys = OFF");
            for (String sql : inserts) {
                st.execute(sql);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Échec du seed", e);
        }
    }

    private String verdictSequence(int sequenceId) {
        try (Connection connexion = source.getConnection();
                Statement st = connexion.createStatement();
                ResultSet rs =
                        st.executeQuery("SELECT verdict FROM selection_sequence WHERE sequence_id = " + sequenceId)) {
            return rs.next() ? rs.getString(1) : null;
        } catch (SQLException e) {
            throw new IllegalStateException("Échec de lecture", e);
        }
    }

    /// Rejoue les seules instructions UPDATE de V27 (l'ALTER a déjà été appliqué au `migrer()`).
    private void rejouerBackfill() {
        for (String instruction : decouperInstructions(lireRessource())) {
            if (!instruction.toUpperCase(java.util.Locale.ROOT).startsWith("UPDATE")) {
                continue;
            }
            try (Connection connexion = source.getConnection();
                    Statement st = connexion.createStatement()) {
                st.execute(instruction);
            } catch (SQLException e) {
                throw new IllegalStateException("Échec SQL : " + instruction, e);
            }
        }
    }

    private static String lireRessource() {
        try (InputStream in = BackfillVerdictMigrationTest.class.getResourceAsStream(V27)) {
            return new String(Optional.ofNullable(in).orElseThrow().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Lecture impossible : " + V27, e);
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

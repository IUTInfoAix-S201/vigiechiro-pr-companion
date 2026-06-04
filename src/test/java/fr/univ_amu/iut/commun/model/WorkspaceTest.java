package fr.univ_amu.iut.commun.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Tests unitaires du socle [Workspace] : résolution des chemins (base SQLite R21, dossiers de
/// session `bruts`/`transformes` R22), workspace par défaut, racine rendue absolue.
class WorkspaceTest {

    private static final String PREFIXE = "Car640380-2026-Pass1-A1";
    private final Workspace ws = new Workspace(Path.of("relatif/ws"));

    @Test
    @DisplayName("La racine fournie est rendue absolue")
    void racine_absolue() {
        assertThat(ws.racine().isAbsolute()).isTrue();
        assertThat(ws.racine()).isEqualTo(Path.of("relatif/ws").toAbsolutePath());
    }

    @Test
    @DisplayName("Le chemin de la base est <racine>/vigiechiro.db")
    void chemin_base() {
        assertThat(ws.cheminBaseDeDonnees()).isEqualTo(ws.racine().resolve("vigiechiro.db"));
    }

    @Test
    @DisplayName("Les dossiers d'une session : <racine>/<prefixe>/{bruts,transformes}")
    void dossiers_session() {
        Path session = ws.racine().resolve(PREFIXE);
        assertThat(ws.dossierSession(PREFIXE)).isEqualTo(session);
        assertThat(ws.dossierBruts(PREFIXE)).isEqualTo(session.resolve("bruts"));
        assertThat(ws.dossierTransformes(PREFIXE)).isEqualTo(session.resolve("transformes"));
    }

    @Test
    @DisplayName("Le workspace par défaut est sous ~/Documents/VigieChiro-Companion (R21)")
    void par_defaut() {
        Path racine = Workspace.parDefaut().racine();
        assertThat(racine.isAbsolute()).isTrue();
        // On compare via java.nio.file.Path.endsWith (purement lexical) et non via
        // AssertJ assertThat(path).endsWith(...), qui canonicalise le chemin reel
        // (toRealPath) et leve NoSuchFileException quand le dossier n'existe pas encore
        // sur le disque - typiquement sur un runner CI neuf.
        assertThat(racine.endsWith(Path.of("Documents", "VigieChiro-Companion")))
                .as("le workspace par defaut est sous ~/Documents/VigieChiro-Companion")
                .isTrue();
    }

    @Test
    @DisplayName("toString mentionne la racine")
    void to_string() {
        assertThat(ws.toString()).startsWith("Workspace[").contains("ws");
    }
}

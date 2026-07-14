package fr.univ_amu.iut.cli;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Injector;
import fr.univ_amu.iut.commun.model.ModeValidation;
import fr.univ_amu.iut.commun.model.Protocole;
import fr.univ_amu.iut.commun.model.StatutWorkflow;
import fr.univ_amu.iut.commun.model.Utilisateur;
import fr.univ_amu.iut.commun.model.dao.UtilisateurDao;
import fr.univ_amu.iut.commun.persistence.MigrationSchema;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.commun.persistence.UniteDeTravail;
import fr.univ_amu.iut.passage.model.EnregistrementOriginal;
import fr.univ_amu.iut.passage.model.Enregistreur;
import fr.univ_amu.iut.passage.model.Passage;
import fr.univ_amu.iut.passage.model.SequenceDEcoute;
import fr.univ_amu.iut.passage.model.SessionDEnregistrement;
import fr.univ_amu.iut.passage.model.dao.EnregistrementOriginalDao;
import fr.univ_amu.iut.passage.model.dao.EnregistreurDao;
import fr.univ_amu.iut.passage.model.dao.PassageDao;
import fr.univ_amu.iut.passage.model.dao.SequenceDao;
import fr.univ_amu.iut.passage.model.dao.SessionDao;
import fr.univ_amu.iut.sites.model.PointDEcoute;
import fr.univ_amu.iut.sites.model.Site;
import fr.univ_amu.iut.sites.model.dao.PointDao;
import fr.univ_amu.iut.sites.model.dao.SiteDao;
import fr.univ_amu.iut.validation.model.MessageObservation;
import fr.univ_amu.iut.validation.model.Observation;
import fr.univ_amu.iut.validation.model.dao.MessageObservationDao;
import fr.univ_amu.iut.validation.model.dao.ObservationDao;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/// `discussion` (#1418) : la **parité CLI** du fil d'échange avec le validateur — le lire, et y répondre.
///
/// ⚠️ Aucun envoi réel n'est tiré ici : l'injecteur applicatif n'est pas connecté, et le test s'arrête
/// **avant** l'appel réseau. Ce qu'il protège, c'est le garde-fou : **rien ne part sans `--confirmer`**,
/// sur une route qui ne sait pas revenir en arrière.
class CliDiscussionTest {

    @TempDir
    Path workspaceDir;

    private Injector injecteur;
    private Cli cli;
    private ByteArrayOutputStream tamponSortie;
    private ByteArrayOutputStream tamponErreur;
    private PrintStream sortie;
    private PrintStream erreur;

    @BeforeEach
    void preparer() {
        System.setProperty("vigiechiro.workspace", workspaceDir.toString());
        injecteur = Cli.injecteurApplicatif();
        cli = new Cli(injecteur);
        injecteur.getInstance(MigrationSchema.class).migrer();
        tamponSortie = new ByteArrayOutputStream();
        tamponErreur = new ByteArrayOutputStream();
        sortie = new PrintStream(tamponSortie, true, StandardCharsets.UTF_8);
        erreur = new PrintStream(tamponErreur, true, StandardCharsets.UTF_8);
    }

    @AfterEach
    void nettoyer() {
        System.clearProperty("vigiechiro.workspace");
    }

    @Test
    @DisplayName("#1418 : --message sans --confirmer n'envoie RIEN — on n'écrit pas l'irréversible par une"
            + " option laissée traîner dans un script")
    void message_exige_confirmer() {
        int code = cli.executer(
                new String[] {"discussion", "--observation", "7", "--message", "Je doute."}, sortie, erreur);

        assertThat(code).isEqualTo(2);
        assertThat(tamponErreur.toString(StandardCharsets.UTF_8))
                .contains("ne pourra PAS être supprimé")
                .contains("--confirmer");
    }

    @Test
    @DisplayName("#1418 : aucun message sur l'observation → la commande le DIT (lecture seule, code 0)")
    void fil_vide() {
        int code = cli.executer(new String[] {"discussion", "--observation", "7"}, sortie, erreur);

        assertThat(code).isZero();
        assertThat(tamponSortie.toString(StandardCharsets.UTF_8)).contains("Aucun message");
    }

    @Test
    @DisplayName("#1417 : le fil se lit en CLI, dans l'ordre du serveur, avec auteur et date")
    void fil_lisible() throws SQLException {
        long idObservation = semerUnFil();

        int code = cli.executer(
                new String[] {"discussion", "--observation", String.valueOf(idObservation)}, sortie, erreur);

        assertThat(code).isZero();
        assertThat(tamponSortie.toString(StandardCharsets.UTF_8))
                .as("l'ordre du serveur ($push) est l'ordre chronologique : la CLI le rend tel quel")
                .containsSubsequence("u-validateur", "C'est un Pipnat.", "u-moi", "Je repasse le son.");
    }

    /// Une observation, et deux messages sur son fil. Le fil est un **reflet du serveur** (c'est l'import
    /// qui l'y met) : on l'écrit donc directement en base. L'observation, elle, doit exister pour de bon —
    /// la clé étrangère du schéma y veille, et c'est heureux : un fil sans détection ne veut rien dire.
    private long semerUnFil() throws SQLException {
        SourceDeDonnees source = injecteur.getInstance(SourceDeDonnees.class);
        new UtilisateurDao(source).insert(new Utilisateur("u-1", "Testeur"));
        Site site = new SiteDao(source)
                .insert(new Site(null, "130711", "Test", Protocole.STANDARD, null, "2026-01-01", "u-1"));
        Long idPoint = new PointDao(source)
                .insert(new PointDEcoute(null, "Z41", null, null, null, site.id()))
                .id();
        new EnregistreurDao(source).insert(new Enregistreur("1925492", null, null));
        Passage passage = new PassageDao(source)
                .insert(new Passage(
                        null,
                        1,
                        2026,
                        "2026-07-03",
                        "22:00",
                        "06:00",
                        null,
                        StatutWorkflow.IMPORTE,
                        null,
                        null,
                        null,
                        null,
                        idPoint,
                        "1925492"));
        Long idSession = new SessionDao(source)
                .insert(new SessionDEnregistrement(null, "/ws/session", null, null, passage.id()))
                .id();
        Long idOriginal = new EnregistrementOriginalDao(source)
                .insert(new EnregistrementOriginal(null, "brut.wav", "/ws/brut.wav", 5.0, 384000, null, idSession))
                .id();
        Long idSequence = new SequenceDao(source)
                .insert(new SequenceDEcoute(null, "seq.wav", idOriginal, 0, 0.0, 5.0, "/ws/seq.wav", false, idSession))
                .id();
        Long idObservation = new ObservationDao(source)
                .insert(new Observation(
                        null,
                        idSequence,
                        0.1,
                        0.4,
                        45,
                        "Pipkuh",
                        0.9,
                        null,
                        null,
                        null,
                        null,
                        false,
                        ModeValidation.NON_VALIDE,
                        null,
                        false,
                        "d-1",
                        3,
                        null,
                        null,
                        null))
                .id();

        MessageObservationDao dao = new MessageObservationDao(source);
        new UniteDeTravail(source)
                .executer(connexion -> dao.remplacerFil(
                        connexion,
                        idObservation,
                        List.of(
                                new MessageObservation(
                                        null,
                                        idObservation,
                                        0,
                                        "u-validateur",
                                        "C'est un Pipnat.",
                                        Instant.parse("2026-07-11T21:04:00Z")),
                                new MessageObservation(null, idObservation, 1, "u-moi", "Je repasse le son.", null))));
        return idObservation;
    }
}

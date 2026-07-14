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
import fr.univ_amu.iut.validation.model.Observation;
import fr.univ_amu.iut.validation.model.dao.ObservationDao;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/// Les gestes de revue en ligne de commande (#1311) : `valider-observations`, `corriger-observations`.
///
/// Ce que ces tests protègent, dans l'ordre d'importance :
///
/// 1. **Le refus.** Viser un passage **sans filtre**, c'est viser la nuit entière. Sans `--confirmer`, la
///    commande **refuse** - c'est le test qui compte le plus, parce qu'un filtre oublié dans un script ne
///    se distingue de rien.
/// 2. **L'effet réel** : la base a changé, et pas « un mock a été appelé ».
/// 3. **Le même choix que la liste** : les filtres d'un geste sont ceux de `lister-observations`.
class CliGestesRevueTest {

    @TempDir
    Path workspaceDir;

    private Injector injecteur;
    private Cli cli;
    private SourceDeDonnees source;
    private ByteArrayOutputStream tamponSortie;
    private ByteArrayOutputStream tamponErreur;
    private PrintStream sortie;
    private PrintStream erreur;

    private long idPassage;
    private long idPipkuhA;
    private long idPipkuhB;
    private long idNyclei;

    @BeforeEach
    void preparer() {
        System.setProperty("vigiechiro.workspace", workspaceDir.toString());
        injecteur = Cli.injecteurApplicatif();
        cli = new Cli(injecteur);
        injecteur.getInstance(MigrationSchema.class).migrer();
        source = injecteur.getInstance(SourceDeDonnees.class);
        tamponSortie = new ByteArrayOutputStream();
        tamponErreur = new ByteArrayOutputStream();
        sortie = new PrintStream(tamponSortie, true, StandardCharsets.UTF_8);
        erreur = new PrintStream(tamponErreur, true, StandardCharsets.UTF_8);
        semer();
    }

    @AfterEach
    void nettoyer() {
        System.clearProperty("vigiechiro.workspace");
    }

    @Test
    @DisplayName("LE REFUS : --passage sans aucun filtre viserait la nuit ENTIÈRE — la commande refuse sans"
            + " --confirmer, et rien n'est touché")
    void passage_entier_sans_confirmer_est_refuse() {
        int code = cli.executer(
                new String[] {"valider-observations", "--passage", String.valueOf(idPassage)}, sortie, erreur);

        assertThat(code).isNotZero();
        assertThat(tamponErreur.toString(StandardCharsets.UTF_8))
                .contains("TOUTES les observations")
                .contains("--confirmer");
        assertThat(taxonObservateur(idPipkuhA))
                .as("rien ne doit avoir bougé : un refus n'est pas une exécution partielle")
                .isNull();
    }

    @Test
    @DisplayName("Avec --confirmer, le passage entier est bien validé (l'utilisateur l'a dit)")
    void passage_entier_avec_confirmer() {
        int code = cli.executer(
                new String[] {"valider-observations", "--passage", String.valueOf(idPassage), "--confirmer"},
                sortie,
                erreur);

        assertThat(code).isZero();
        assertThat(tamponSortie.toString(StandardCharsets.UTF_8)).contains("passage ENTIER");
        assertThat(taxonObservateur(idPipkuhA)).isEqualTo("Pipkuh");
        assertThat(taxonObservateur(idNyclei)).isEqualTo("Nyclei");
    }

    @Test
    @DisplayName("Par identifiants : seules les lignes désignées bougent")
    void valider_par_identifiants() {
        int code = cli.executer(
                new String[] {"valider-observations", "--observation", idPipkuhA + "," + idPipkuhB}, sortie, erreur);

        assertThat(code).isZero();
        assertThat(taxonObservateur(idPipkuhA)).isEqualTo("Pipkuh");
        assertThat(taxonObservateur(idPipkuhB)).isEqualTo("Pipkuh");
        assertThat(taxonObservateur(idNyclei))
                .as("la ligne non désignée n'a pas été touchée")
                .isNull();
    }

    @Test
    @DisplayName("Par filtre : corriger-observations touche EXACTEMENT ce que lister-observations montrait")
    void corriger_par_filtre_touche_ce_que_la_liste_montrait() {
        // Le filtre --taxon-tadarida Pipkuh désigne les deux Pipkuh, pas le Nyclei : c'est ce que
        // `lister-observations --passage N --taxon Pipkuh` afficherait, et c'est le même code qui choisit.
        int code = cli.executer(
                new String[] {
                    "corriger-observations",
                    "--taxon",
                    "Pippip",
                    "--passage",
                    String.valueOf(idPassage),
                    "--taxon-tadarida",
                    "Pipkuh"
                },
                sortie,
                erreur);

        assertThat(code).isZero();
        assertThat(taxonObservateur(idPipkuhA)).isEqualTo("Pippip");
        assertThat(taxonObservateur(idPipkuhB)).isEqualTo("Pippip");
        assertThat(taxonObservateur(idNyclei)).isNull();
    }

    @Test
    @DisplayName("Un taxon inconnu du référentiel arrête TOUT avant la moindre écriture")
    void taxon_inconnu_n_ecrit_rien() {
        int code = cli.executer(
                new String[] {"corriger-observations", "--taxon", "Zzzzzz", "--observation", String.valueOf(idPipkuhA)},
                sortie,
                erreur);

        assertThat(code).isNotZero();
        assertThat(taxonObservateur(idPipkuhA))
                .as("une correction en masse vers un taxon inexistant serait un dégât silencieux")
                .isNull();
    }

    @Test
    @DisplayName("Un filtre qui ne retient rien LÈVE, au lieu de répondre « 0 observation traitée »")
    void filtre_vide_leve() {
        int code = cli.executer(
                new String[] {
                    "valider-observations", "--passage", String.valueOf(idPassage), "--taxon-tadarida", "Rhihip"
                },
                sortie,
                erreur);

        assertThat(code).isNotZero();
        assertThat(tamponErreur.toString(StandardCharsets.UTF_8)).contains("Aucune observation");
    }

    private String taxonObservateur(long idObservation) {
        return new ObservationDao(source).findById(idObservation).orElseThrow().taxonObservateur();
    }

    /// Trois observations : deux que Tadarida croit `Pipkuh`, une `Nyclei`. Les taxons existent au
    /// référentiel (sans quoi `corriger` refuserait, à raison).
    private void semer() {
        // Pipkuh, Pippip et Nyclei viennent du VRAI référentiel (migration V02__seed_taxons.sql) : les
        // réinsérer violerait la clé primaire. Le test s'appuie donc sur le référentiel réel, ce qui est
        // aussi ce qui rend le cas « taxon inconnu » significatif : Zzzzzz n'y est vraiment pas.
        new UtilisateurDao(source).insert(new Utilisateur("u-1", "Testeur"));
        Site site = new SiteDao(source)
                .insert(new Site(null, "130711", "Test", Protocole.STANDARD, null, "2026-01-01", "u-1"));
        Long idPoint = new PointDao(source)
                .insert(new PointDEcoute(null, "Z41", null, null, null, site.id()))
                .id();
        new EnregistreurDao(source).insert(new Enregistreur("1925492", null, null));
        idPassage = new PassageDao(source)
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
                        "1925492"))
                .id();
        Long idSession = new SessionDao(source)
                .insert(new SessionDEnregistrement(null, "/ws/session", null, null, idPassage))
                .id();
        Long idOriginal = new EnregistrementOriginalDao(source)
                .insert(new EnregistrementOriginal(null, "brut.wav", "/ws/brut.wav", 5.0, 384000, null, idSession))
                .id();

        idPipkuhA = observation(idSession, idOriginal, 0, "Pipkuh");
        idPipkuhB = observation(idSession, idOriginal, 1, "Pipkuh");
        idNyclei = observation(idSession, idOriginal, 2, "Nyclei");
    }

    private long observation(Long idSession, Long idOriginal, int rang, String taxonTadarida) {
        Long idSequence = new SequenceDao(source)
                .insert(new SequenceDEcoute(
                        null, "seq" + rang + ".wav", idOriginal, rang, 0.0, 5.0, "/ws/seq.wav", false, idSession))
                .id();
        return new ObservationDao(source)
                .insert(new Observation(
                        null,
                        idSequence,
                        0.1,
                        0.4,
                        45,
                        taxonTadarida,
                        0.9,
                        null,
                        null,
                        null,
                        null,
                        false,
                        ModeValidation.NON_VALIDE,
                        null,
                        false,
                        null,
                        null,
                        null,
                        null,
                        null))
                .id();
    }
}

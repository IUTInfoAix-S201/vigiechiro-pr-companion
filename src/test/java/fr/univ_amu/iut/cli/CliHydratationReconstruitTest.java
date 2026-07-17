package fr.univ_amu.iut.cli;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Injector;
import fr.univ_amu.iut.commun.model.FichierWav;
import fr.univ_amu.iut.commun.model.Prefixe;
import fr.univ_amu.iut.commun.model.Protocole;
import fr.univ_amu.iut.commun.model.StatutWorkflow;
import fr.univ_amu.iut.commun.model.Utilisateur;
import fr.univ_amu.iut.commun.model.dao.UtilisateurDao;
import fr.univ_amu.iut.commun.persistence.MigrationSchema;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.importation.model.SequenceProduite;
import fr.univ_amu.iut.importation.model.TransformationAudio;
import fr.univ_amu.iut.importation.model.TransformationOriginal;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/// Parité CLI ↔ IHM du chantier « réactiver un passage reconstruit depuis les bruts » (EPIC #1653) : la
/// commande `reactiver` **hydrate** un passage reconstruit exactement comme le bouton de M-Passage
/// (même service, même compte rendu). Un passage reconstruit n'a pas d'inventaire d'originaux ; désigner
/// le dossier de ses bruts (log + WAV) régénère les séquences, les rebranche sur preuve structurelle
/// (#1682) et remplace le placeholder par les vrais originaux (#1651).
///
/// Injecteur **applicatif complet** (comme [CliArchivageTest]) : les ports `InventaireBrutsSource`,
/// `RegenerationSequences` et l'adoption des originaux sont réellement câblés, on exerce donc la vraie
/// chaîne d'hydratation, pas un double.
class CliHydratationReconstruitTest {

    private static final String SERIE = "1925492";
    private static final Prefixe PREFIXE = new Prefixe("040962", 2026, 1, "A1");
    private static final String NOM_SD_BRUT = "PaRec_20260620_213000.wav";
    private static final String NOM_R6_BRUT = "Car040962-2026-Pass1-A1-PaRec_20260620_213000.wav";
    private static final int FREQUENCE_ACQUISITION_HZ = 40_000; // Fe du log ; en-tête à Fe/10
    private static final double DUREE_BRUT_S = 8.0; // 8 s réelles → 2 tranches (5 s + 3 s)

    @TempDir
    Path workspace;

    private Cli cli;
    private ByteArrayOutputStream tamponSortie;
    private PrintStream sortie;
    private PrintStream erreur;
    private SourceDeDonnees source;
    private Path transformes;
    private Path bruts;
    private Long idPassage;
    private Long idSession;

    @BeforeEach
    void preparer() throws IOException {
        System.setProperty("vigiechiro.workspace", workspace.toString());
        Injector injecteur = Cli.injecteurApplicatif();
        cli = new Cli(injecteur);
        injecteur.getInstance(MigrationSchema.class).migrer();
        tamponSortie = new ByteArrayOutputStream();
        sortie = new PrintStream(tamponSortie, true, StandardCharsets.UTF_8);
        erreur = new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8);
        source = injecteur.getInstance(SourceDeDonnees.class);
        transformes = Files.createDirectories(
                workspace.resolve(PREFIXE.nomDossierSession()).resolve("transformes"));
        bruts = Files.createDirectories(workspace.resolve("carte-sd"));
    }

    @AfterEach
    void nettoyer() {
        System.clearProperty("vigiechiro.workspace");
    }

    private String texte() {
        return tamponSortie.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("reactiver hydrate un passage reconstruit depuis ses bruts + log : séquences régénérées, sortie 0")
    void reactiver_hydrate_un_passage_reconstruit() throws IOException {
        List<String> noms = semerReconstruit();

        int code = cli.executer(
                new String[] {"reactiver", "--passage", idPassage.toString(), "--source", bruts.toString()},
                sortie,
                erreur);

        assertThat(code).as("l'audio est intégralement revenu").isZero();
        assertThat(texte())
                .contains("régénérées")
                .contains(noms.size() + " séquence(s) réactivée(s)")
                .contains("COMPLETE");
        assertThat(noms).allSatisfy(nom -> assertThat(transformes.resolve(nom)).exists());
        // #1651 : le placeholder a laissé place aux vrais originaux (avec fréquence), déclarés purgés.
        assertThat(new EnregistrementOriginalDao(source).findBySession(idSession))
                .isNotEmpty()
                .allSatisfy(original ->
                        assertThat(original.frequenceEchantillonnageHz()).isNotNull());
        assertThat(new SessionDao(source)
                        .trouverParPassage(idPassage)
                        .orElseThrow()
                        .originauxPurges())
                .isTrue();
    }

    // --- Fixture ---------------------------------------------------------------------------------

    /// Sème un passage **reconstruit** : un placeholder à la place des originaux, des séquences nommées
    /// comme les tranches d'un brut (produites par la vraie transformation, puis effacées : passage
    /// archivé), et le brut + son log déposés dans le dossier « carte SD ».
    private List<String> semerReconstruit() throws IOException {
        new UtilisateurDao(source).insert(new Utilisateur("u-1", "Testeur"));
        Site site = new SiteDao(source)
                .insert(new Site(null, "040962", null, Protocole.STANDARD, null, "2026-05-01", "u-1"));
        Long idPoint = new PointDao(source)
                .insert(new PointDEcoute(null, "A1", null, null, null, site.id()))
                .id();
        new EnregistreurDao(source).insert(new Enregistreur(SERIE, null, null));
        idPassage = new PassageDao(source)
                .insert(new Passage(
                        null,
                        1,
                        2026,
                        "2026-06-20",
                        "21:30:00",
                        "05:15:00",
                        null,
                        StatutWorkflow.DEPOSE,
                        null,
                        null,
                        null,
                        null,
                        idPoint,
                        SERIE))
                .id();
        Path racineSession = workspace.resolve(PREFIXE.nomDossierSession());
        SessionDao sessionDao = new SessionDao(source);
        idSession = sessionDao
                .insert(new SessionDEnregistrement(null, racineSession.toString(), 0L, 0L, idPassage))
                .id();
        EnregistrementOriginalDao originalDao = new EnregistrementOriginalDao(source);
        Long idPlaceholder = originalDao
                .insert(new EnregistrementOriginal(
                        null, PREFIXE.prefixeFichier() + "reconstruit.wav", "", null, null, null, idSession))
                .id();

        // Le brut, transformé pour connaître les noms EXACTS de ses tranches (ceux que la base connaîtra).
        Path brut = workspace.resolve("origine").resolve(NOM_SD_BRUT);
        ecrireBrut(brut);
        TransformationOriginal transformation = new TransformationAudio()
                .transformer(brut, NOM_R6_BRUT, transformes, PREFIXE, FREQUENCE_ACQUISITION_HZ);

        SequenceDao sequenceDao = new SequenceDao(source);
        List<String> noms = new ArrayList<>();
        int index = 0;
        for (SequenceProduite produite : transformation.sequences()) {
            noms.add(produite.nomFichier());
            sequenceDao.insert(new SequenceDEcoute(
                    null,
                    produite.nomFichier(),
                    idPlaceholder,
                    index,
                    null,
                    null,
                    transformes.resolve(produite.nomFichier()).toString(),
                    false,
                    idSession,
                    null,
                    null,
                    null));
            Files.delete(produite.chemin()); // archivage : les tranches quittent le disque
            index++;
        }
        sessionDao.marquerArchivee(idSession, LocalDateTime.of(2026, 7, 16, 18, 30));

        // Ce que l'utilisateur a gardé : son brut (nom de carte SD) et le log de l'enregistreur.
        Files.copy(brut, bruts.resolve(NOM_SD_BRUT));
        ecrireLog(bruts.resolve("LogPR" + SERIE + ".txt"), FREQUENCE_ACQUISITION_HZ / 1000);
        Files.delete(brut);
        return noms;
    }

    /// Brut synthétique de [#DUREE_BRUT_S] secondes réelles, en-tête à Fe/10 (comme l'écrit l'enregistreur).
    private void ecrireBrut(Path fichier) throws IOException {
        int echantillons = (int) Math.round(DUREE_BRUT_S * FREQUENCE_ACQUISITION_HZ);
        byte[] pcm = new byte[echantillons * 2];
        int valeur = 42;
        for (int n = 0; n < echantillons; n++) {
            valeur = valeur * 31 + 17;
            short amplitude = (short) (valeur % 8000);
            pcm[2 * n] = (byte) (amplitude & 0xFF);
            pcm[2 * n + 1] = (byte) ((amplitude >> 8) & 0xFF);
        }
        Files.createDirectories(fichier.getParent());
        FichierWav.ecrire(fichier, 1, FREQUENCE_ACQUISITION_HZ / 10, 16, pcm, 0, pcm.length);
    }

    /// Journal minimal de l'enregistreur : une ligne « Paramètres » porte la fréquence `Fe…kHz`.
    private void ecrireLog(Path fichier, int frequenceKhz) throws IOException {
        Files.write(
                fichier,
                List.of(
                        "20/06/26 - 21:30:00 PR" + SERIE + " Démarrage v1.0",
                        "20/06/26 - 21:30:01 PR" + SERIE + " Paramètres : Acquisi. 21:30-05:15, Fe" + frequenceKhz
                                + "kHz, S. R. Med, Bd. Freq. 8-120kHz"),
                StandardCharsets.UTF_8);
    }
}

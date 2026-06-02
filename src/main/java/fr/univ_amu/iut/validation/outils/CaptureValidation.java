// --solution-only--
package fr.univ_amu.iut.validation.outils;

import com.google.inject.Guice;
import com.google.inject.Injector;
import fr.univ_amu.iut.commun.di.CommunModule;
import fr.univ_amu.iut.commun.di.PersistenceModule;
import fr.univ_amu.iut.commun.model.StatutWorkflow;
import fr.univ_amu.iut.commun.model.Utilisateur;
import fr.univ_amu.iut.commun.model.Verdict;
import fr.univ_amu.iut.commun.model.dao.UtilisateurDao;
import fr.univ_amu.iut.commun.outils.ApercuFx;
import fr.univ_amu.iut.commun.persistence.MigrationSchema;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.passage.di.PassageModule;
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
import fr.univ_amu.iut.validation.di.ValidationModule;
import fr.univ_amu.iut.validation.model.Observation;
import fr.univ_amu.iut.validation.model.ResultatsIdentification;
import fr.univ_amu.iut.validation.model.ServiceValidation;
import fr.univ_amu.iut.validation.model.dao.ObservationDao;
import fr.univ_amu.iut.validation.view.ValidationController;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;

/// OUTIL ENSEIGNANT (hors version etudiante, retire en passe A2).
///
/// Capture l'écran M-Vision-Tadarida en PNG pour le comparer à la maquette du brief, en **deux
/// états** afin d'en montrer les particularités :
///
/// - `apercu-validation-import.png` : **état d'entrée**, passage déposé sans résultats importés —
///   bouton « Importer un CSV Tadarida » actif, message d'état neutre, table vide ;
/// - `apercu-validation-revue.png` : **état de revue**, après import + valider/corriger, avec un
///   mélange de statuts (validée / corrigée / à revoir), une ligne sélectionnée (panneau de détail)
///   et l'export `_Vu` désormais disponible.
///
/// On seede une base SQLite temporaire (utilisateur, site/point en SQL brut, passage `DEPOSE`,
/// session avec trois séquences). La première capture est rendue **avant** import ; on importe
/// ensuite un petit CSV Tadarida via [ServiceValidation], on valide / corrige deux observations,
/// puis on rend la seconde. Chaque vue est chargée avec une `controllerFactory` Guice (socle +
/// passage + validation) et rendue hors-écran par [ApercuFx].
///
/// Le site et le point (cibles de clé étrangère) sont insérés en SQL brut, sans les DAO de la
/// feature `sites` : `validation` ne doit pas en dépendre (cycle ArchUnit `features_sans_cycle`).
///
/// Lancement headless : `.github/assets/capture-screenshots.sh` (Headless Platform JavaFX 26).
public final class CaptureValidation {

    private static final String ID_UTILISATEUR = "demo-enseignant";
    private static final String ENREGISTREUR = "1925492";
    private static final String NUMERO_CARRE = "640380";
    private static final String CODE_POINT = "A1";
    private static final String NOM_SITE = "Étang de la Tuilière";
    private static final String NYCLEI = "Nyclei"; // taxon semé, réutilisé dans le CSV + correction

    private CaptureValidation() {}

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch fini = new CountDownLatch(1);
        AtomicReference<Throwable> erreur = new AtomicReference<>();
        Platform.startup(() -> {
            try {
                capturer();
            } catch (RuntimeException | IOException probleme) {
                erreur.set(probleme);
            } finally {
                fini.countDown();
            }
        });
        fini.await();
        Platform.exit();
        if (erreur.get() != null) {
            erreur.get().printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }

    private static void capturer() throws IOException {
        Path workspace = Files.createTempDirectory("vc-capture-validation");
        System.setProperty("vigiechiro.workspace", workspace.toString());
        Path sortie = Path.of(System.getProperty("capture.outDir", ".github/assets"));

        Injector injecteur = Guice.createInjector(
                new CommunModule(), new PersistenceModule(), new PassageModule(), new ValidationModule());
        SourceDeDonnees source = injecteur.getInstance(SourceDeDonnees.class);
        new MigrationSchema(source).migrer();

        long idPassage = seeder(source, workspace);

        // 1) État d'entrée : aucun import encore, le bouton « Importer un CSV Tadarida » est le point
        // d'accès. Aucune ligne sélectionnée (-1).
        rendre(injecteur, idPassage, -1, sortie.resolve("apercu-validation-import.png"));

        // Import puis revue (mélange de statuts pour montrer la colonne « Statut » et la progression).
        ServiceValidation service = injecteur.getInstance(ServiceValidation.class);
        ResultatsIdentification resultats = service.importer(idPassage, ecrireCsv(workspace));
        List<Observation> observations = new ObservationDao(source).findByResults(resultats.id());
        service.valider(observations.get(0).id()); // noise → validée
        service.corriger(observations.get(1).id(), NYCLEI, 0.88); // Pippip → corrigée en Nyclei

        // 2) État de revue : table peuplée, ligne corrigée sélectionnée (index 1) → panneau de détail.
        rendre(injecteur, idPassage, 1, sortie.resolve("apercu-validation-revue.png"));
    }

    /// Charge `Validation.fxml`, l'ouvre sur le passage, sélectionne éventuellement une ligne (index
    /// `>= 0`) pour peupler le panneau de détail, puis rend la scène hors-écran en PNG.
    private static void rendre(Injector injecteur, long idPassage, int ligneSelectionnee, Path fichier)
            throws IOException {
        FXMLLoader loader = new FXMLLoader(ValidationController.class.getResource("Validation.fxml"));
        loader.setControllerFactory(injecteur::getInstance);
        Parent vue = loader.load();
        ValidationController controleur = loader.getController();
        controleur.ouvrirSur(idPassage);

        if (ligneSelectionnee >= 0
                && vue.lookup("#tableObservations") instanceof TableView<?> table
                && table.getItems().size() > ligneSelectionnee) {
            table.getSelectionModel().select(ligneSelectionnee);
        }

        ApercuFx.enregistrerPng(new Scene(vue, 1100, 640), fichier);
        System.out.println("Apercu ecrit dans " + fichier.toAbsolutePath());
    }

    /// Seede un passage déposé avec sa session et trois séquences (`seqA/B/C_000.wav`), nommées pour
    /// correspondre au CSV Tadarida importé ensuite. Renvoie l'identifiant du passage.
    private static long seeder(SourceDeDonnees source, Path workspace) {
        new UtilisateurDao(source).insert(new Utilisateur(ID_UTILISATEUR, "Capitaine Chiro (demo)"));
        PassageDao passageDao = new PassageDao(source);
        SessionDao sessionDao = new SessionDao(source);
        EnregistrementOriginalDao originalDao = new EnregistrementOriginalDao(source);
        SequenceDao sequenceDao = new SequenceDao(source);
        new EnregistreurDao(source).insert(new Enregistreur(ENREGISTREUR, "V1.01", null));

        long idPoint = seederSiteEtPoint(source);
        Passage passage = passageDao.insert(new Passage(
                null,
                2,
                2026,
                "2026-06-22",
                "20:25:00",
                "07:47:00",
                null,
                StatutWorkflow.DEPOSE,
                Verdict.OK,
                null,
                null,
                null,
                idPoint,
                ENREGISTREUR));
        SessionDEnregistrement session = sessionDao.insert(
                new SessionDEnregistrement(null, workspace.resolve("session").toString(), null, null, passage.id()));

        for (String base : List.of("seqA", "seqB", "seqC")) {
            EnregistrementOriginal original = originalDao.insert(new EnregistrementOriginal(
                    null,
                    base + ".wav",
                    workspace.resolve("bruts").resolve(base + ".wav").toString(),
                    5.0,
                    384000,
                    null,
                    session.id()));
            sequenceDao.insert(new SequenceDEcoute(
                    null,
                    base + "_000.wav",
                    original.id(),
                    0,
                    0.0,
                    5.0,
                    workspace.resolve("transformes").resolve(base + "_000.wav").toString(),
                    false,
                    session.id()));
        }
        return passage.id();
    }

    /// Écrit un petit CSV Tadarida « Brut » (tout guillemeté) référençant les trois séquences seedées
    /// et des taxons déjà semés (`noise`, `Pippip`, `Nyclei`). Renvoie son chemin.
    private static Path ecrireCsv(Path workspace) {
        String contenu = ligne(
                        "nom du fichier",
                        "temps_debut",
                        "temps_fin",
                        "frequence_mediane",
                        "tadarida_taxon",
                        "tadarida_probabilite",
                        "tadarida_taxon_autre",
                        "observateur_taxon",
                        "observateur_probabilite",
                        "validateur_taxon",
                        "validateur_probabilite")
                + ligne("seqA_000", "0.3", "3.9", "18", "noise", "0.93", "", "", "", "", "")
                + ligne("seqA_000", "0.4", "4.1", "45", "Pippip", "0.80", NYCLEI, "", "", "", "")
                + ligne("seqB_000", "1.0", "2.0", "42", "Pippip", "0.62", "", "", "", "", "")
                + ligne("seqC_000", "0.0", "5.0", "27", NYCLEI, "0.71", "", "", "", "", "");
        Path fichier = workspace.resolve("nuit-observations.csv");
        try {
            Files.writeString(fichier, contenu, StandardCharsets.UTF_8);
        } catch (IOException echec) {
            throw new UncheckedIOException(echec);
        }
        return fichier;
    }

    private static String ligne(String... champs) {
        return "\"" + String.join("\";\"", champs) + "\"\n";
    }

    /// Insère en SQL brut un site (`monitoring_site`) et son point d'écoute (`listening_point`),
    /// cibles de clé étrangère du passage, et renvoie l'`id` du point. Volontairement sans les DAO de
    /// la feature `sites` : `validation` ne doit pas en dépendre (cycle ArchUnit).
    private static long seederSiteEtPoint(SourceDeDonnees source) {
        String insertSite = "INSERT INTO monitoring_site"
                + "(square_number, friendly_name, protocol, comment, created_at, user_id)"
                + " VALUES (?, ?, ?, ?, ?, ?)";
        String insertPoint =
                "INSERT INTO listening_point(code, gps_lat, gps_lon, description, site_id)" + " VALUES (?, ?, ?, ?, ?)";
        try (Connection cx = source.getConnection()) {
            long idSite;
            try (PreparedStatement ps = cx.prepareStatement(insertSite, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, NUMERO_CARRE);
                ps.setString(2, NOM_SITE);
                ps.setString(3, "STANDARD");
                ps.setString(4, "Aix-en-Provence");
                ps.setString(5, "2026-01-01");
                ps.setString(6, ID_UTILISATEUR);
                ps.executeUpdate();
                idSite = cleGeneree(ps);
            }
            try (PreparedStatement ps = cx.prepareStatement(insertPoint, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, CODE_POINT);
                ps.setDouble(2, 43.5298);
                ps.setDouble(3, 5.4474);
                ps.setString(4, "Près du grand chêne");
                ps.setLong(5, idSite);
                ps.executeUpdate();
                return cleGeneree(ps);
            }
        } catch (SQLException echec) {
            throw new IllegalStateException("Seed SQL du site/point impossible", echec);
        }
    }

    private static long cleGeneree(PreparedStatement ps) throws SQLException {
        try (ResultSet cles = ps.getGeneratedKeys()) {
            if (!cles.next()) {
                throw new IllegalStateException("Aucune clé générée renvoyée par l'INSERT");
            }
            return cles.getLong(1);
        }
    }
}

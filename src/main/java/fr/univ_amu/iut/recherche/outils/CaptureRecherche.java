package fr.univ_amu.iut.recherche.outils;

import com.google.inject.Injector;
import fr.univ_amu.iut.commun.di.RacineInjecteur;
import fr.univ_amu.iut.commun.model.Protocole;
import fr.univ_amu.iut.commun.model.RechercheGlobale;
import fr.univ_amu.iut.commun.model.ResultatRecherche;
import fr.univ_amu.iut.commun.model.StatutWorkflow;
import fr.univ_amu.iut.commun.model.Utilisateur;
import fr.univ_amu.iut.commun.model.Verdict;
import fr.univ_amu.iut.commun.model.dao.UtilisateurDao;
import fr.univ_amu.iut.commun.outils.ApercuFx;
import fr.univ_amu.iut.commun.persistence.MigrationSchema;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.passage.model.Enregistreur;
import fr.univ_amu.iut.passage.model.Passage;
import fr.univ_amu.iut.passage.model.dao.EnregistreurDao;
import fr.univ_amu.iut.passage.model.dao.PassageDao;
import fr.univ_amu.iut.sites.model.PointDEcoute;
import fr.univ_amu.iut.sites.model.Site;
import fr.univ_amu.iut.sites.model.dao.PointDao;
import fr.univ_amu.iut.sites.model.dao.SiteDao;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/// Outil de capture/mesure, utilisable tel quel.
///
/// Capture la **recherche globale** du chrome (#144) : le champ « Rechercher » et sa liste déroulante de
/// résultats groupés (Sites / Points / Passages). La recherche n'a pas d'écran propre — elle vit dans le
/// chrome (`MainView.fxml`) — donc on rend le **chrome complet** avec le popup ouvert.
///
/// Démarche : injecteur applicatif complet ([RacineInjecteur#creer()]) pour que le chrome se charge avec
/// toutes ses cartes ; base SQLite jetable **seedée** (un utilisateur, deux sites avec points et passages)
/// pour que la recherche retourne des résultats ; on charge le chrome, on **force l'ouverture** du popup
/// (saisie + résultats du service [RechercheGlobale], panneau rendu visible) puis on rend hors-écran par
/// [ApercuFx]. Le seed précède toute résolution de l'utilisateur courant (premier utilisateur en base),
/// pour que les données seedées lui appartiennent.
///
/// Lancement headless : `.github/assets/capture-screenshots.sh` (Headless Platform JavaFX 26).
public final class CaptureRecherche {

    private static final String ID_UTILISATEUR = "demo-enseignant";
    private static final String ENREGISTREUR = "1925492";
    private static final String CHROME = "/fr/univ_amu/iut/commun/view/MainView.fxml";
    private static final String REQUETE = "640380";

    private CaptureRecherche() {}

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
        Throwable probleme = erreur.get();
        if (probleme != null) {
            probleme.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }

    private static void capturer() throws IOException {
        Path workspace = Files.createTempDirectory("vc-capture-recherche");
        System.setProperty("vigiechiro.workspace", workspace.toString());
        Path sortie = Path.of(System.getProperty("capture.outDir", ".github/assets"));

        Injector injecteur = RacineInjecteur.creer();
        SourceDeDonnees source = injecteur.getInstance(SourceDeDonnees.class);
        new MigrationSchema(source).migrer();
        seeder(source); // AVANT toute résolution de l'utilisateur courant (premier utilisateur en base).

        List<ResultatRecherche> resultats =
                injecteur.getInstance(RechercheGlobale.class).rechercher(REQUETE);

        FXMLLoader loader = new FXMLLoader(CaptureRecherche.class.getResource(CHROME));
        loader.setControllerFactory(injecteur::getInstance);
        Parent chrome = loader.load();
        ouvrirPopup(chrome, resultats);

        ApercuFx.enregistrerPng(new Scene(chrome, 1100, 720), sortie.resolve("apercu-recherche.png"));
        System.out.println("Apercu de recherche ecrit dans " + sortie.toAbsolutePath());
    }

    /// Force l'ouverture de la liste de résultats sur le chrome chargé : pose la saisie, les résultats du
    /// service et rend le panneau visible. On contourne ainsi l'anti-rebond (180 ms) de la saisie réelle,
    /// pour un rendu **déterministe** du popup.
    @SuppressWarnings("unchecked")
    private static void ouvrirPopup(Parent chrome, List<ResultatRecherche> resultats) {
        TextField champ = (TextField) chrome.lookup("#champRecherche");
        VBox panneau = (VBox) chrome.lookup("#panneauResultats");
        ListView<ResultatRecherche> liste = (ListView<ResultatRecherche>) chrome.lookup("#listeResultats");
        champ.setText(REQUETE);
        liste.getItems().setAll(resultats);
        liste.getSelectionModel().select(0);
        panneau.setVisible(true);
        panneau.setManaged(true);
        champ.requestFocus();
        champ.positionCaret(REQUETE.length());
    }

    /// Seede un utilisateur et deux sites (carrés 640380 / 640381) avec leurs points et quelques passages,
    /// pour que la recherche sur « 640380 » retourne des sites, des points et des passages.
    private static void seeder(SourceDeDonnees source) {
        new UtilisateurDao(source).insert(new Utilisateur(ID_UTILISATEUR, "Capitaine Chiro (demo)"));
        new EnregistreurDao(source).insert(new Enregistreur(ENREGISTREUR, "V1.01", null));
        SiteDao siteDao = new SiteDao(source);
        PointDao pointDao = new PointDao(source);
        PassageDao passageDao = new PassageDao(source);

        Site tuiliere = siteDao.insert(new Site(
                null, "640380", "Étang de la Tuilière", Protocole.STANDARD, null, "2026-01-01", ID_UTILISATEUR));
        siteDao.insert(
                new Site(null, "640381", "Bois des Chênes", Protocole.STANDARD, null, "2026-01-01", ID_UTILISATEUR));
        // Description mentionnant le carré : la recherche « 640380 » fait alors aussi remonter le POINT
        // (les points matchent par code OU description), pour illustrer les trois groupes de résultats.
        Long pointA = pointDao.insert(new PointDEcoute(
                        null, "A1", 43.4010, -1.5740, "Carré 640380 · près du grand chêne", tuiliere.id()))
                .id();

        passage(passageDao, 2, 2026, "2026-06-22", StatutWorkflow.DEPOSE, Verdict.OK, pointA);
        passage(passageDao, 1, 2026, "2026-06-08", StatutWorkflow.VERIFIE, Verdict.DOUTEUX, pointA);
    }

    private static void passage(
            PassageDao dao, int numero, int annee, String date, StatutWorkflow statut, Verdict verdict, Long idPoint) {
        dao.insert(new Passage(
                null,
                numero,
                annee,
                date,
                "20:25:00",
                "07:47:00",
                null,
                statut,
                verdict,
                null,
                null,
                null,
                idPoint,
                ENREGISTREUR));
    }
}

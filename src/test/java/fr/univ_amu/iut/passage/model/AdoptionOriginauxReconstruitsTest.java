package fr.univ_amu.iut.passage.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.univ_amu.iut.commun.model.HorlogeFigee;
import fr.univ_amu.iut.commun.model.Protocole;
import fr.univ_amu.iut.commun.model.StatutWorkflow;
import fr.univ_amu.iut.commun.model.Utilisateur;
import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.commun.model.dao.UtilisateurDao;
import fr.univ_amu.iut.commun.persistence.DataAccessException;
import fr.univ_amu.iut.commun.persistence.MigrationSchema;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.commun.persistence.UniteDeTravail;
import fr.univ_amu.iut.passage.model.dao.EnregistrementOriginalDao;
import fr.univ_amu.iut.passage.model.dao.EnregistreurDao;
import fr.univ_amu.iut.passage.model.dao.PassageDao;
import fr.univ_amu.iut.passage.model.dao.SequenceDao;
import fr.univ_amu.iut.passage.model.dao.SessionDao;
import fr.univ_amu.iut.sites.model.PointDEcoute;
import fr.univ_amu.iut.sites.model.Site;
import fr.univ_amu.iut.sites.model.dao.PointDao;
import fr.univ_amu.iut.sites.model.dao.SiteDao;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/// Garde-fou d'**atomicité** de l'adoption des originaux d'une nuit reconstruite (#1959, #1968).
///
/// L'adoption inscrit les enregistrements retrouvés et y rattache leurs séquences - des milliers d'ordres
/// sur une vraie nuit. Ils sont groupés dans une [UniteDeTravail] : le motif était le **coût** (près de
/// 6700 commits auto-commités, plus de deux minutes d'attente), l'atomicité est venue avec.
///
/// C'est elle que ce test tient : une écriture qui échoue en cours de route ne doit laisser **aucun**
/// original à moitié adopté. Sans transaction, les originaux déjà insérés survivraient à l'échec, et la
/// nuit se retrouverait avec des enregistrements dont les séquences ne pointent nulle part.
class AdoptionOriginauxReconstruitsTest {

    private static final String ID_USER = "u-1";
    private static final String SERIE = "1925492";
    private static final String PLACEHOLDER = "reconstruit.wav";
    private static final int FREQUENCE_HZ = 384_000;

    @TempDir
    Path dossier;

    private SourceDeDonnees source;
    private SessionDao sessionDao;
    private EnregistrementOriginalDao originalDao;
    private SequenceDao sequenceDao;
    private SessionDEnregistrement session;

    @BeforeEach
    void preparer() {
        source = new SourceDeDonnees(new Workspace(dossier));
        new MigrationSchema(source).migrer();
        new UtilisateurDao(source).insert(new Utilisateur(ID_USER, "Testeur"));
        Site site = new SiteDao(source)
                .insert(new Site(null, "640380", null, Protocole.STANDARD, null, "2026-05-01", ID_USER));
        Long idPoint = new PointDao(source)
                .insert(new PointDEcoute(null, "A1", null, null, null, site.id()))
                .id();
        new EnregistreurDao(source).insert(new Enregistreur(SERIE, null, null));
        Long idPassage = new PassageDao(source)
                .insert(new Passage(
                        null,
                        1,
                        2026,
                        "2026-06-20",
                        "21:30:00",
                        "05:15:00",
                        null,
                        StatutWorkflow.IMPORTE,
                        null,
                        null,
                        null,
                        null,
                        idPoint,
                        SERIE))
                .id();
        sessionDao = new SessionDao(source);
        originalDao = new EnregistrementOriginalDao(source);
        sequenceDao = new SequenceDao(source);
        session = sessionDao.insert(
                new SessionDEnregistrement(null, dossier.resolve("session") + "", null, null, idPassage));
    }

    @Test
    @DisplayName("#1959 : une écriture qui échoue en route ne laisse aucun original à moitié adopté")
    void une_ecriture_qui_echoue_ne_laisse_rien_de_moitie_adopte() throws IOException {
        EnregistrementOriginal placeholder = insererPlaceholder();
        List<BrutRebranche> bruts = List.of(
                brutAvecSaSequence("PaRec_001.wav", placeholder.id()),
                brutAvecSaSequence("PaRec_002.wav", placeholder.id()));
        // Échoue au SECOND rattachement : le premier original est donc déjà inséré quand la panne survient.
        AdoptionOriginauxReconstruits adoption = adoptionAvec(new SequenceDaoQuiLache(source, 2));

        assertThatThrownBy(() -> adoption.adopter(session, List.of(placeholder), bruts, FREQUENCE_HZ))
                .as("l'échec d'écriture remonte, il n'est pas avalé")
                .isInstanceOf(RuntimeException.class);

        assertThat(originalDao.findBySession(session.id()))
                .as("tout est annulé : seul le placeholder subsiste, aucun original n'a été adopté à moitié")
                .extracting(EnregistrementOriginal::nomFichier)
                .containsExactly(PLACEHOLDER);
    }

    @Test
    @DisplayName("Le chemin nominal adopte les originaux et retire le placeholder devenu orphelin")
    void le_chemin_nominal_adopte_et_nettoie() throws IOException {
        EnregistrementOriginal placeholder = insererPlaceholder();
        List<BrutRebranche> bruts = List.of(
                brutAvecSaSequence("PaRec_001.wav", placeholder.id()),
                brutAvecSaSequence("PaRec_002.wav", placeholder.id()));

        adoptionAvec(sequenceDao).adopter(session, List.of(placeholder), bruts, FREQUENCE_HZ);

        assertThat(originalDao.findBySession(session.id()))
                .extracting(EnregistrementOriginal::nomFichier)
                .as("les deux bruts sont adoptés, le placeholder vidé est parti")
                .containsExactlyInAnyOrder("PaRec_001.wav", "PaRec_002.wav");
    }

    // --- Fixture ---------------------------------------------------------------------------------

    private AdoptionOriginauxReconstruits adoptionAvec(SequenceDao dao) {
        return new AdoptionOriginauxReconstruits(
                originalDao,
                dao,
                sessionDao,
                new UniteDeTravail(source),
                new HorlogeFigee(LocalDateTime.of(2026, 7, 19, 12, 0)));
    }

    private EnregistrementOriginal insererPlaceholder() {
        // Un passage reconstruit ne porte qu'un original sans fréquence ni fichier : c'est ce que l'adoption
        // remplace.
        return originalDao.insert(
                new EnregistrementOriginal(null, PLACEHOLDER, PLACEHOLDER, null, null, null, session.id(), null));
    }

    /// Un brut sur disque, et **sa** séquence en base, rattachée au placeholder comme après une
    /// reconstruction.
    private BrutRebranche brutAvecSaSequence(String nomBrut, Long idPlaceholder) throws IOException {
        Path bruts = Files.createDirectories(dossier.resolve("session").resolve("bruts"));
        Path source = Files.write(bruts.resolve(nomBrut), new byte[4096]);
        SequenceDEcoute sequence = sequenceDao.insert(new SequenceDEcoute(
                null,
                nomBrut.replace(".wav", "_000.wav"),
                idPlaceholder,
                0,
                0.0,
                5.0,
                dossier.resolve("transformes").resolve(nomBrut).toString(),
                false,
                session.id(),
                null,
                null,
                null));
        return new BrutRebranche(new BrutInventorie(source, nomBrut, 2048L), List.of(sequence), "empreinte-" + nomBrut);
    }

    /// `SequenceDao` qui lâche au n-ième rattachement, pour éprouver le rollback.
    ///
    /// Il compte les **deux** variantes, transactionnelle ou non, et lâche sur l'une comme sur l'autre. Sans
    /// quoi le garde-fou ne verrait rien d'une adoption qui repasserait aux écritures auto-commitées : la
    /// panne ne se déclencherait plus, et le test échouerait pour la mauvaise raison.
    private static final class SequenceDaoQuiLache extends SequenceDao {

        private final int rangDeLaPanne;
        private int rattachements;

        private SequenceDaoQuiLache(SourceDeDonnees source, int rangDeLaPanne) {
            super(source);
            this.rangDeLaPanne = rangDeLaPanne;
        }

        @Override
        public void majOriginal(Connection connexion, long idSequence, long idEnregistrementOriginal)
                throws SQLException {
            if (lachePeutEtre()) {
                throw new SQLException("Panne simulée au rattachement " + rattachements);
            }
            super.majOriginal(connexion, idSequence, idEnregistrementOriginal);
        }

        @Override
        public void majOriginal(long idSequence, long idEnregistrementOriginal) {
            if (lachePeutEtre()) {
                throw new DataAccessException("Panne simulée au rattachement " + rattachements, null);
            }
            super.majOriginal(idSequence, idEnregistrementOriginal);
        }

        private boolean lachePeutEtre() {
            rattachements++;
            return rattachements == rangDeLaPanne;
        }
    }
}

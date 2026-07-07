package fr.univ_amu.iut.commun.persistence;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.model.Horloge;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import org.sqlite.SQLiteDataSource;

/// **Sauvegarde et restauration** de la base SQLite (#148) : la base concentre tout le travail
/// (sites, passages, observations), sans filet natif. Ce service permet d'en écrire une copie cohérente
/// et de repartir d'une sauvegarde.
///
/// - **Sauvegarde** : `VACUUM INTO` produit un **instantané cohérent** de la base dans un fichier
///   horodaté, même si une connexion est ouverte (contrairement à une copie brute qui pourrait rater le
///   journal WAL). Le fichier obtenu est une base SQLite autonome et compacte.
/// - **Restauration** : on vérifie d'abord que le fichier est une base **lisible**, on met de côté la base
///   courante (**filet de sécurité** avant écrasement, critère #148), on la remplace, on purge les fichiers
///   annexes (`-wal`/`-shm`/`-journal`) puis on **rejoue la migration** (idempotente) pour garantir un schéma
///   à jour — un état cohérent quelle que soit l'ancienneté de la sauvegarde.
///
/// Les connexions du socle sont **de courte durée** (ouvertes/fermées par opération, cf. [SourceDeDonnees]) :
/// aucune connexion longue à fermer pour remplacer le fichier. La restauration reste une action délibérée,
/// à faire hors opération concurrente.
public class ServiceSauvegarde {

    private static final String PREFIXE = "vigiechiro-sauvegarde-";
    private static final String EXTENSION = ".db";
    private static final String SUFFIXE_FILET = ".avant-restauration";
    private static final DateTimeFormatter HORODATAGE = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final SourceDeDonnees source;
    private final Horloge horloge;

    @Inject
    public ServiceSauvegarde(SourceDeDonnees source, Horloge horloge) {
        this.source = Objects.requireNonNull(source, "source");
        this.horloge = Objects.requireNonNull(horloge, "horloge");
    }

    /// Écrit une sauvegarde cohérente de la base dans `dossierDestination` (créé au besoin), nommée
    /// `vigiechiro-sauvegarde-AAAAMMJJ-HHMMSS.db`. Renvoie le fichier créé. `dossierDestination` **choisi
    /// par l'appelant** rend l'emplacement configurable (critère #148).
    public Path sauvegarder(Path dossierDestination) {
        Objects.requireNonNull(dossierDestination, "dossierDestination");
        try {
            Files.createDirectories(dossierDestination);
            Path cible = fichierLibre(dossierDestination);
            try (Connection cx = source.getConnection();
                    Statement st = cx.createStatement()) {
                st.execute("VACUUM INTO " + litteralSql(cible));
            }
            return cible;
        } catch (IOException | SQLException echec) {
            throw new DataAccessException("Sauvegarde de la base impossible vers " + dossierDestination, echec);
        }
    }

    /// Restaure la base depuis `sauvegarde`. Vérifie que le fichier est une base lisible, **met de côté**
    /// la base courante (`vigiechiro.db.avant-restauration`), la remplace, purge les fichiers annexes puis
    /// **migre** pour garantir un schéma à jour.
    ///
    /// @throws IllegalArgumentException si `sauvegarde` n'existe pas
    /// @throws DataAccessException si le fichier n'est pas une base SQLite lisible, ou en cas d'échec d'E/S
    public void restaurer(Path sauvegarde) {
        Objects.requireNonNull(sauvegarde, "sauvegarde");
        if (!Files.isRegularFile(sauvegarde)) {
            throw new IllegalArgumentException("Fichier de sauvegarde introuvable : " + sauvegarde);
        }
        verifierBaseLisible(sauvegarde);
        Path base = source.workspace().cheminBaseDeDonnees();
        try {
            Files.createDirectories(base.getParent());
            if (Files.exists(base)) {
                Files.copy(
                        base,
                        base.resolveSibling(base.getFileName() + SUFFIXE_FILET),
                        StandardCopyOption.REPLACE_EXISTING);
            }
            Files.copy(sauvegarde, base, StandardCopyOption.REPLACE_EXISTING);
            purgerAnnexe(base, "-wal");
            purgerAnnexe(base, "-shm");
            purgerAnnexe(base, "-journal");
        } catch (IOException echec) {
            throw new DataAccessException("Restauration de la base impossible depuis " + sauvegarde, echec);
        }
        new MigrationSchema(source).migrer();
    }

    /// Dossier de sauvegarde **par défaut** (`<workspace>/sauvegardes`) : proposé quand l'utilisateur ne
    /// choisit pas d'emplacement. L'emplacement reste configurable (paramètre de [#sauvegarder]).
    public Path dossierParDefaut() {
        return source.workspace().racine().resolve("sauvegardes");
    }

    /// Premier nom de fichier libre dans `dossier` : base horodatée, suffixée `-1`, `-2`… si l'horodatage
    /// à la seconde entre en collision (deux sauvegardes dans la même seconde).
    private Path fichierLibre(Path dossier) {
        String base = PREFIXE + HORODATAGE.format(horloge.maintenant());
        Path candidat = dossier.resolve(base + EXTENSION);
        int suffixe = 1;
        while (Files.exists(candidat)) {
            candidat = dossier.resolve(base + "-" + suffixe++ + EXTENSION);
        }
        return candidat;
    }

    /// Vérifie que `fichier` est une base SQLite **intègre** (`PRAGMA quick_check` renvoie `ok`), via une
    /// source jetable pointant dessus. Lève [DataAccessException] sinon.
    private static void verifierBaseLisible(Path fichier) {
        SQLiteDataSource source = new SQLiteDataSource();
        source.setUrl("jdbc:sqlite:" + fichier);
        try (Connection cx = source.getConnection();
                Statement st = cx.createStatement();
                ResultSet rs = st.executeQuery("PRAGMA quick_check")) {
            if (!rs.next() || !"ok".equalsIgnoreCase(rs.getString(1))) {
                throw new DataAccessException("Le fichier n'est pas une sauvegarde valide : " + fichier, null);
            }
        } catch (SQLException echec) {
            throw new DataAccessException("Fichier de sauvegarde illisible : " + fichier, echec);
        }
    }

    /// Supprime un fichier annexe SQLite (`base-wal`, `base-shm`, `base-journal`) s'il existe, pour ne pas
    /// laisser un journal périmé masquer la base restaurée.
    private static void purgerAnnexe(Path base, String suffixe) throws IOException {
        Files.deleteIfExists(base.resolveSibling(base.getFileName() + suffixe));
    }

    /// Littéral chaîne SQL à partir d'un chemin (apostrophes doublées) pour l'ordre `VACUUM INTO`.
    private static String litteralSql(Path chemin) {
        return "'" + chemin.toString().replace("'", "''") + "'";
    }
}

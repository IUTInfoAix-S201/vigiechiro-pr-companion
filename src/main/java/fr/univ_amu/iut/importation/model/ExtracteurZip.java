package fr.univ_amu.iut.importation.model;

import fr.univ_amu.iut.commun.model.RegleMetierException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/// Décompresse une **archive `.zip`** de carte SD vers un **dossier temporaire**, pour que l'import
/// (#139) accepte un zip aussi bien qu'un dossier déjà décompressé. La source d'origine n'est jamais
/// modifiée (cohérent R9 : on lit le zip, on écrit ailleurs).
///
/// **Sur disque, pas en RAM** : le dossier d'extraction est créé **sous le workspace** (passé en
/// paramètre), et non dans `java.io.tmpdir`. Sur la plupart des postes `/tmp` est un *tmpfs* monté en
/// RAM, souvent borné à quelques Go : une vraie nuit de terrain (~10 Go décompressés) le saturerait et
/// l'extraction échouerait en `ENOSPC`. Le workspace vit sur le disque, là où l'import recopie ensuite
/// les fichiers — l'extraction et l'import partagent donc le même volume.
///
/// **Mémoire bornée** (#104) : chaque entrée est recopiée en **flux** ([java.io.InputStream#transferTo],
/// tampon interne), jamais chargée entière en mémoire — un zip volumineux ne sature donc pas le tas.
///
/// **Garde « zip-slip »** : une entrée dont le chemin s'évaderait du dossier d'extraction (`../…`) est
/// refusée, pour ne pas écrire hors de la zone temporaire.
public final class ExtracteurZip {

    private ExtracteurZip() {}

    /// `true` si `chemin` désigne une archive `.zip` (par l'extension, insensible à la casse).
    public static boolean estZip(Path chemin) {
        return chemin != null && chemin.getFileName().toString().toLowerCase().endsWith(".zip");
    }

    /// Variante sans suivi de progression (extraction silencieuse), pour les appels qui n'affichent rien.
    public static Path extraireVersDossierTemporaire(Path archiveZip, Path dossierBase) {
        return extraireVersDossierTemporaire(archiveZip, dossierBase, p -> {});
    }

    /// Extrait `archiveZip` vers un **dossier temporaire neuf créé sous `dossierBase`** (le workspace,
    /// sur disque) et renvoie ce dossier (à inspecter puis importer comme une carte SD). En cas d'échec,
    /// le dossier partiellement extrait est nettoyé.
    ///
    /// **Progression déterminée** (#146) : le nombre total de fichiers est lu d'abord dans l'index du zip
    /// (`ZipFile`, instantané : seul le répertoire central en fin d'archive est lu, pas les 10 Go), puis
    /// `surProgression` est notifié après chaque fichier extrait (« Décompression : X / N fichiers… »).
    /// Le callback peut être invoqué **hors du fil JavaFX** : l'appelant le marshale lui-même.
    ///
    /// @param dossierBase volume d'accueil de l'extraction (workspace disque), créé s'il manque
    /// @param surProgression notifié à chaque fichier extrait (avancement déterminé)
    /// @throws RegleMetierException si une entrée tente de s'évader du dossier (zip-slip)
    public static Path extraireVersDossierTemporaire(
            Path archiveZip, Path dossierBase, Consumer<Progression> surProgression) {
        Path racine = creerDossierExtraction(dossierBase);
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(Files.newInputStream(archiveZip)))) {
            int total = compterFichiers(archiveZip);
            int faits = 0;
            ZipEntry entree;
            while ((entree = zis.getNextEntry()) != null) {
                boolean estFichier = !entree.isDirectory();
                extraireUneEntree(zis, racine, entree);
                zis.closeEntry();
                if (estFichier) {
                    surProgression.accept(progression(++faits, total));
                }
            }
        } catch (IOException e) {
            supprimerRecursivement(racine);
            // On expose la cause (ex. « Aucun espace disponible sur le périphérique ») : sans elle,
            // l'utilisateur ne saurait pas qu'il s'agit d'un manque de place disque.
            throw new UncheckedIOException(
                    "Décompression du zip impossible : " + archiveZip + " (" + e.getMessage() + ")", e);
        } catch (RuntimeException e) {
            supprimerRecursivement(racine);
            throw e;
        }
        return racine;
    }

    private static Path creerDossierExtraction(Path dossierBase) {
        try {
            Files.createDirectories(dossierBase);
            return Files.createTempDirectory(dossierBase, "import-zip-");
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Création du dossier d'extraction impossible sous " + dossierBase + " (" + e.getMessage() + ")", e);
        }
    }

    /// Nombre d'entrées « fichier » (hors dossiers) de l'archive, lu dans le répertoire central
    /// (`ZipFile`) sans décompresser : sert de dénominateur à la progression « X / N ».
    private static int compterFichiers(Path archiveZip) throws IOException {
        try (ZipFile zf = new ZipFile(archiveZip.toFile())) {
            return (int) zf.stream().filter(e -> !e.isDirectory()).count();
        }
    }

    private static Progression progression(int faits, int total) {
        if (total <= 0) {
            return new Progression("Décompression : " + faits + " fichier(s)…", 1.0);
        }
        return new Progression("Décompression : " + faits + " / " + total + " fichiers…", (double) faits / total);
    }

    private static void extraireUneEntree(ZipInputStream zis, Path racine, ZipEntry entree) throws IOException {
        Path cible = racine.resolve(entree.getName()).normalize();
        if (!cible.startsWith(racine)) {
            throw new RegleMetierException(
                    "Archive zip invalide : l'entrée « " + entree.getName() + " » sort du dossier d'extraction.");
        }
        if (entree.isDirectory()) {
            Files.createDirectories(cible);
            return;
        }
        Files.createDirectories(cible.getParent());
        try (OutputStream os = Files.newOutputStream(cible)) {
            zis.transferTo(os); // recopie en flux : mémoire bornée (#104)
        }
    }

    /// Supprime récursivement `dossier` (nettoyage du temporaire après import, succès ou échec).
    /// **Best-effort** : on n'interrompt pas le flux si un fichier résiste (le temporaire système sera
    /// de toute façon recyclé). Sans effet si `dossier` est `null` ou absent.
    public static void supprimerRecursivement(Path dossier) {
        if (dossier == null || !Files.exists(dossier)) {
            return;
        }
        try (Stream<Path> chemins = Files.walk(dossier)) {
            chemins.sorted(Comparator.reverseOrder()).forEach(ExtracteurZip::supprimerSilencieux);
        } catch (IOException ignore) {
            // Best-effort : un nettoyage incomplet du temporaire n'est pas une erreur métier.
        }
    }

    private static void supprimerSilencieux(Path chemin) {
        try {
            Files.deleteIfExists(chemin);
        } catch (IOException ignore) {
            // Best-effort.
        }
    }
}

package fr.univ_amu.iut.commun.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/// Lit et écrit **où vivent** l'espace de travail et la base (#1038, [ADR 1038]). C'est le versant
/// métier de l'onglet « Emplacements » des réglages et de la commande CLI équivalente : ni l'un ni
/// l'autre ne touche directement la [ConfigurationAmorcage].
///
/// ## Ce qu'il ne fait pas
///
/// Il ne **déplace rien**. Écrire un nouvel emplacement ne fait que changer le **pointeur** lu au
/// prochain démarrage : les données restent là où elles sont. Déplacer le pointeur de la base vers un
/// dossier vide fait démarrer l'application sur une base **neuve**, l'ancienne restant intacte à son
/// ancien emplacement. C'est à l'utilisateur de copier son `.db` s'il veut l'emporter - même principe
/// que pour son audio (ADR 0048).
///
/// ## La sonde
///
/// [#sonder(Path)] **essaie réellement d'écrire** dans le dossier candidat plutôt que de se fier à
/// `Files.isWritable`, dont le verdict est peu fiable sur les partages réseau et sous Windows. Un
/// emplacement non inscriptible se refuse ainsi **au moment du choix**, pas au prochain démarrage où
/// l'on ne saurait plus rien en faire d'utile.
///
/// La configuration lue et écrite est celle de [ConfigurationAmorcage#dossier()], la même que
/// [Workspace#resolu()] consulte : les deux ne peuvent donc pas diverger.
public final class ServiceEmplacements {

    /// Verdict de la sonde d'accessibilité d'un dossier candidat.
    public enum Accessibilite {
        /// Le dossier existe (ou a pu être créé) et on a pu y écrire.
        ACCESSIBLE,
        /// Le chemin désigne un fichier existant, pas un dossier.
        PAS_UN_DOSSIER,
        /// Le dossier n'existe pas et n'a pas pu être créé.
        INEXISTANT_NON_CREABLE,
        /// Le dossier existe mais on n'a pas pu y écrire.
        NON_INSCRIPTIBLE
    }

    /// Les emplacements effectifs et leurs défauts, tels que l'écran les affiche.
    ///
    /// @param espaceDeTravail dossier de travail effectif au prochain démarrage
    /// @param base fichier de base effectif au prochain démarrage
    /// @param espaceDeTravailParDefaut ce que vaudrait l'espace de travail sans configuration
    /// @param baseParDefaut ce que vaudrait la base sans configuration
    /// @param personnalise `true` si une configuration d'amorçage est écrite (emplacements choisis)
    public record Emplacements(
            Path espaceDeTravail, Path base, Path espaceDeTravailParDefaut, Path baseParDefaut, boolean personnalise) {}

    /// Emplacements effectifs au prochain démarrage, avec leurs défauts et le fait qu'ils soient ou non
    /// personnalisés.
    public Emplacements emplacementsCourants() {
        Workspace effectif = Workspace.resolu();
        Workspace defaut = Workspace.parDefaut();
        boolean personnalise = !ConfigurationAmorcage.lue().equals(ConfigurationAmorcage.vide());
        return new Emplacements(
                effectif.racine(),
                effectif.cheminBaseDeDonnees(),
                defaut.racine(),
                defaut.cheminBaseDeDonnees(),
                personnalise);
    }

    /// Éprouve un dossier candidat en y écrivant un fichier témoin, qu'elle efface ensuite. Le crée s'il
    /// manque : désigner un dossier encore inexistant est un usage normal (« ranger la base dans un
    /// nouveau dossier »).
    public Accessibilite sonder(Path dossier) {
        Objects.requireNonNull(dossier, "dossier");
        if (Files.exists(dossier) && !Files.isDirectory(dossier)) {
            return Accessibilite.PAS_UN_DOSSIER;
        }
        try {
            Files.createDirectories(dossier);
        } catch (IOException inexistant) {
            return Accessibilite.INEXISTANT_NON_CREABLE;
        }
        try {
            Path temoin = Files.createTempFile(dossier, "vigiechiro-sonde", ".tmp");
            Files.delete(temoin);
            return Accessibilite.ACCESSIBLE;
        } catch (IOException nonInscriptible) {
            return Accessibilite.NON_INSCRIPTIBLE;
        }
    }

    /// Écrit le choix de l'utilisateur : le dossier de travail, et le dossier **où ranger la base** (le
    /// fichier `vigiechiro.db` y est nommé automatiquement). Prend effet au prochain démarrage.
    public void enregistrer(Path dossierEspaceDeTravail, Path dossierBase) throws IOException {
        Objects.requireNonNull(dossierEspaceDeTravail, "dossierEspaceDeTravail");
        Objects.requireNonNull(dossierBase, "dossierBase");
        Path base = dossierBase.resolve(Workspace.FICHIER_BASE);
        new ConfigurationAmorcage(Optional.of(dossierEspaceDeTravail), Optional.of(base))
                .enregistrerDans(ConfigurationAmorcage.dossier());
    }

    /// Efface la configuration d'amorçage : l'application retrouve ses emplacements par défaut au
    /// prochain démarrage. Sans effet si aucune configuration n'est écrite.
    public void reinitialiser() throws IOException {
        ConfigurationAmorcage.vide().enregistrerDans(ConfigurationAmorcage.dossier());
    }
}

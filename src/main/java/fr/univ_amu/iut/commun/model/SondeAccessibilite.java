package fr.univ_amu.iut.commun.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/// Éprouve qu'un dossier de destination est **réellement utilisable** avant qu'on s'y engage.
///
/// Née pour l'onglet « Emplacements » (#1038), remontée ici (#2258, harmonisation) parce que **tout
/// geste qui désigne un dossier de destination** a le même besoin : la sauvegarde, le reset, l'export.
/// Refuser un dossier inutilisable **au moment du choix** vaut mieux que de lancer une opération qui
/// échouera à mi-parcours - surtout longue (une sauvegarde complète) ou destructive (un reset).
///
/// Elle **écrit vraiment** un fichier témoin plutôt que de se fier à [Files#isWritable(Path)], dont le
/// verdict est peu fiable sur les partages réseau et sous Windows. Elle **crée** le dossier s'il manque :
/// désigner un dossier encore inexistant est un usage normal (« ranger la base dans un nouveau
/// dossier »).
public final class SondeAccessibilite {

    /// Verdict d'accessibilité d'un dossier candidat.
    public enum Verdict {
        /// Le dossier existe (ou a pu être créé) et on a pu y écrire.
        ACCESSIBLE("accessible"),
        /// Le chemin désigne un fichier existant, pas un dossier.
        PAS_UN_DOSSIER("un fichier, pas un dossier"),
        /// Le dossier n'existe pas et n'a pas pu être créé.
        INEXISTANT_NON_CREABLE("dossier introuvable et non créable"),
        /// Le dossier existe mais on n'a pas pu y écrire.
        NON_INSCRIPTIBLE("dossier non inscriptible");

        private final String motif;

        Verdict(String motif) {
            this.motif = motif;
        }

        /// Raison lisible du verdict, à afficher à l'utilisateur. Vide de sens pour [#ACCESSIBLE].
        public String motif() {
            return motif;
        }

        /// Vrai si le dossier peut servir de destination.
        public boolean accessible() {
            return this == ACCESSIBLE;
        }
    }

    private SondeAccessibilite() {}

    /// Éprouve `dossier` en y écrivant un fichier témoin, qu'elle efface ensuite.
    public static Verdict sonder(Path dossier) {
        Objects.requireNonNull(dossier, "dossier");
        if (Files.exists(dossier) && !Files.isDirectory(dossier)) {
            return Verdict.PAS_UN_DOSSIER;
        }
        try {
            Files.createDirectories(dossier);
        } catch (IOException inexistant) {
            return Verdict.INEXISTANT_NON_CREABLE;
        }
        try {
            Path temoin = Files.createTempFile(dossier, "vigiechiro-sonde", ".tmp");
            Files.delete(temoin);
            return Verdict.ACCESSIBLE;
        } catch (IOException nonInscriptible) {
            return Verdict.NON_INSCRIPTIBLE;
        }
    }
}

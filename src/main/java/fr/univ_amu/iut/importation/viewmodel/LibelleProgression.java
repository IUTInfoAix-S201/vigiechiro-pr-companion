package fr.univ_amu.iut.importation.viewmodel;

/// Compose le **libellé de progression** d'une opération longue (import ou décompression, #146) en y
/// joignant une **estimation du temps restant** (ETA). Logique purement textuelle, extraite de
/// [ImportationViewModel] (qui gardait sinon trop de responsabilités) : aucune dépendance JavaFX, donc
/// directement testable.
final class LibelleProgression {

    private LibelleProgression() {}

    /// Joint au `libelle` une estimation du temps restant par **extrapolation linéaire**
    /// (restant ≈ écoulé × (1−fraction)/fraction). N'ajoute rien tant que l'avancement est nul, déjà
    /// terminé, ou trop récent pour estimer (évite un ETA absurde au tout premier point).
    static String avecTempsRestant(String libelle, double fraction, long ecouleNanos) {
        if (fraction <= 0.0 || fraction >= 1.0 || ecouleNanos <= 0) {
            return libelle;
        }
        long restantSecondes = Math.round((ecouleNanos / 1_000_000_000.0) * (1.0 - fraction) / fraction);
        return libelle + " · " + formaterDuree(restantSecondes) + " restant";
    }

    /// Formate une durée en `~X s` ou `~X min Y s` (estimation, d'où le `~`).
    static String formaterDuree(long secondes) {
        if (secondes < 60) {
            return "~" + secondes + " s";
        }
        long minutes = secondes / 60;
        long reste = secondes % 60;
        return reste == 0 ? "~" + minutes + " min" : "~" + minutes + " min " + reste + " s";
    }
}

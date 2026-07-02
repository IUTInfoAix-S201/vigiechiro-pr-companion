package fr.univ_amu.iut.importation.viewmodel;

import fr.univ_amu.iut.importation.model.DetectionRalenti;

/// Rédige l'avertissement **« enregistrements déjà ralentis »** destiné à l'utilisateur à l'aperçu,
/// quand la fréquence d'échantillonnage des originaux est trop basse pour un ultrason brut (source déjà
/// expansée ×10, cf. [DetectionRalenti]). Ces fichiers seront **rejetés** à l'import (garde-fou double
/// expansion) : mieux vaut le signaler AVANT.
///
/// Même esprit que [AvertissementNuitExistante] : la mise en phrase (présentation) est une
/// responsabilité de la couche `viewmodel`. Avertissement **informatif** (non bloquant) — il n'empêche
/// pas de lancer l'import, mais prévient que ces originaux ne produiront rien.
final class AvertissementFichiersRalentis {

    private AvertissementFichiersRalentis() {}

    /// Construit l'avertissement, ou une chaîne **vide** si les originaux ne sont pas ralentis (ou si la
    /// fréquence d'en-tête est inconnue).
    ///
    /// @param frequenceEnTeteHz fréquence lue dans l'en-tête d'un original représentatif (`null` inconnu)
    /// @param frequenceLogHz fréquence d'acquisition du log (`null` en mode dégradé)
    static String rediger(Integer frequenceEnTeteHz, Integer frequenceLogHz) {
        if (frequenceEnTeteHz == null || !DetectionRalenti.estDejaRalenti(frequenceEnTeteHz, frequenceLogHz)) {
            return "";
        }
        String reference = frequenceLogHz != null
                ? "le log annonce une acquisition à " + frequenceLogHz + " Hz"
                : "un ultrason brut est échantillonné à au moins " + DetectionRalenti.FREQUENCE_ACQUISITION_MIN_HZ
                        + " Hz";
        return "⚠ Les enregistrements semblent déjà ralentis (en-tête à " + frequenceEnTeteHz + " Hz, alors que "
                + reference + ") : ce ne sont pas des bruts, ils seront rejetés à l'import."
                + " Importez les vrais fichiers de l'enregistreur (~384 kHz).";
    }
}

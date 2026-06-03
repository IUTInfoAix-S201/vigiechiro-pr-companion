package fr.univ_amu.iut.importation.viewmodel;

import fr.univ_amu.iut.importation.model.AnalyseMelange;

/// Rédige l'avertissement **« mélange »** (#33) destiné à l'utilisateur à partir d'une
/// [AnalyseMelange]. Extrait du [ImportationViewModel] pour garder ce dernier focalisé sur l'état de
/// l'assistant : la mise en phrase (présentation) est une responsabilité à part, mais reste dans la
/// couche `viewmodel` (le `model` ne porte pas de texte d'IHM, à l'image de `messageErreur` ou
/// `resumeJournal`, construits côté VM).
final class AvertissementMelange {

    private AvertissementMelange() {}

    /// Construit l'avertissement à afficher, ou une chaîne **vide** si le dossier paraît homogène (une
    /// nuit, un enregistreur). Le message est informatif : il n'empêche pas l'import.
    static String rediger(AnalyseMelange melange) {
        if (!melange.melange()) {
            return "";
        }
        StringBuilder message = new StringBuilder("⚠ Ce dossier semble mélanger ");
        if (melange.plusieursEnregistreurs()) {
            message.append("plusieurs enregistreurs (séries ")
                    .append(String.join(", ", melange.series()))
                    .append(")");
            if (melange.plusieursNuits()) {
                message.append(" et ");
            }
        }
        if (melange.plusieursNuits()) {
            message.append("plusieurs nuits (").append(melange.nuits().size()).append(" dates d'acquisition)");
        }
        return message.append(" : vérifiez qu'il correspond bien à une seule nuit avant d'importer.")
                .toString();
    }
}

package fr.univ_amu.iut.importation.viewmodel;

import fr.univ_amu.iut.importation.model.AnalyseCoherence;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/// Rédige l'avertissement **« incohérence »** (#33) destiné à l'utilisateur à partir d'une
/// [AnalyseCoherence]. Comme [AvertissementMelange], la mise en phrase est isolée hors du
/// [ImportationViewModel] (responsabilité de présentation) tout en restant dans la couche `viewmodel`.
final class AvertissementIncoherence {

    private static final DateTimeFormatter JOUR = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRANCE);

    private AvertissementIncoherence() {}

    /// Construit l'avertissement à afficher, ou une chaîne **vide** si l'identité déclarée concorde
    /// avec les enregistrements. Le message reste informatif : il n'empêche pas l'import.
    static String rediger(AnalyseCoherence coherence) {
        if (!coherence.incoherent()) {
            return "";
        }
        StringBuilder message = new StringBuilder("⚠ Le journal du capteur ne correspond pas aux enregistrements : ");
        String liaison = "";
        if (coherence.serieIncoherente()) {
            message.append("la série déclarée (")
                    .append(String.join(", ", coherence.seriesDeclareesAbsentes()))
                    .append(") est absente des fichiers (série ")
                    .append(String.join(", ", coherence.seriesFichiers()))
                    .append(")");
            liaison = " et ";
        }
        if (coherence.dateIncoherente()) {
            message.append(liaison)
                    .append("la date du journal (")
                    .append(coherence.dateJournal().map(JOUR::format).orElse("?"))
                    .append(") ne tombe pas dans la nuit des fichiers (")
                    .append(datesFichiers(coherence))
                    .append(")");
        }
        return message.append(". Vérifiez que le journal et les fichiers proviennent bien de la même nuit.")
                .toString();
    }

    private static String datesFichiers(AnalyseCoherence coherence) {
        StringBuilder dates = new StringBuilder();
        for (LocalDate nuit : coherence.nuitsFichiers()) {
            if (dates.length() > 0) {
                dates.append(", ");
            }
            dates.append(JOUR.format(nuit));
        }
        return dates.toString();
    }
}

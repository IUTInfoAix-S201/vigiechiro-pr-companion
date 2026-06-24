package fr.univ_amu.iut.importation.model;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/// Reconstitue une **identité de repli** (#107) quand aucun journal LogPR n'est présent : un
/// [JournalParse] minimal porté par les **noms des WAV** (`PaRecPR<série>_<date>_…`, même motif que
/// [AnalyseMelange]). Permet d'importer en **mode dégradé** sans bloquer — l'absence de journal est par
/// ailleurs signalée à l'inspection (avertissement non bloquant).
///
/// Extrait de [ServiceImport] pour le garder cohésif (la reconstitution d'identité est une
/// préoccupation autonome).
final class JournalDeRepli {

    /// Série inscrite si aucun nom de WAV n'est exploitable (la colonne `recorder.serial_number` est
    /// `NOT NULL`). Cas rare : les noms bruts portent presque toujours la série.
    static final String SERIE_INCONNUE = "PR-INCONNU";

    private JournalDeRepli() {}

    /// Journal de repli : série + date extraites des noms d'`originaux` ; tous les autres champs
    /// (paramètres d'acquisition, micro, heures…) restent nuls, ce que la construction du passage et du
    /// micro tolère déjà.
    static JournalParse depuis(List<Path> originaux) {
        AnalyseMelange analyse = AnalyseMelange.depuis(originaux);
        String serie =
                analyse.series().isEmpty() ? SERIE_INCONNUE : analyse.series().first();
        LocalDate date = analyse.nuits().isEmpty() ? null : analyse.nuits().first();
        return new JournalParse(serie, null, date, null, null, null, null, null, false, null, List.of(), List.of());
    }
}

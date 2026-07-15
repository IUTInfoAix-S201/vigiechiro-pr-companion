package fr.univ_amu.iut.commun.view;

import fr.univ_amu.iut.commun.model.OperationAnnuleeException;
import fr.univ_amu.iut.commun.model.RegleMetierException;
import java.util.logging.Level;
import java.util.logging.Logger;

/// Journalise le `Throwable` qu'une [ExecuteurTache] route vers son callback d'échec (#1523), **avant**
/// que l'écran n'en affiche le message : un échec ne doit jamais être sans trace, surtout quand
/// `getMessage()` est nul (le message disparaissait alors sans rien laisser).
///
/// Les issues **normales** d'une opération longue restent discrètes (FINE) : une annulation
/// ([OperationAnnuleeException]) et un refus métier ([RegleMetierException] - point inconnu, analyse non
/// terminée…) ne sont pas des bugs, et les tracer bruyamment noierait le signal. Seul un `Throwable`
/// **inattendu** part en SEVERE **avec sa trace** : c'est exactement la classe de bug qu'on ne voyait pas.
final class JournalisationTache {

    private static final Logger LOG = Logger.getLogger(JournalisationTache.class.getName());

    private JournalisationTache() {}

    /// Consigne `erreur` au niveau qui correspond à sa nature. Sans effet observable pour l'appelant :
    /// l'aiguillage vers `succes`/`annule`/`echec` reste entièrement à la charge de l'[ExecuteurTache].
    static void consigner(Throwable erreur) {
        if (erreur instanceof OperationAnnuleeException) {
            LOG.fine("Tâche de fond annulée par l'utilisateur.");
        } else if (erreur instanceof RegleMetierException) {
            LOG.fine(() -> "Refus métier d'une tâche de fond : " + erreur.getMessage());
        } else {
            LOG.log(Level.SEVERE, erreur, () -> "Échec inattendu d'une tâche de fond (routé vers l'écran).");
        }
    }
}

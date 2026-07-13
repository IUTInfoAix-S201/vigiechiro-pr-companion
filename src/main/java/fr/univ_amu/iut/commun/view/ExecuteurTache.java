package fr.univ_amu.iut.commun.view;

import com.google.inject.ImplementedBy;
import fr.univ_amu.iut.commun.model.OperationAnnuleeException;
import fr.univ_amu.iut.commun.model.Progression;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

/// Exécute un **travail lourd hors du fil JavaFX** puis en applique le résultat (ou l'erreur) **sur le
/// fil JavaFX** (#1014). Généralise le patron répété dans les contrôleurs
/// (`Thread.ofVirtual()… Platform.runLater(…)`) en une primitive maîtrisant le fil d'exécution : en
/// production le travail tourne en arrière-plan (l'IHM ne gèle pas), en test tout est **synchrone**
/// (déterministe). Sœur de [ExecuteurFiche], spécialisée aux traitements lourds génériques.
///
/// Pour les opérations **longues** (#1252), le socle couvre aussi :
///
/// - la **progression déterminée** : [#relaisProgression] fabrique le `Consumer<Progression>` à passer
///   au service, chaque point revenant sur le fil JavaFX ([#surFilJavaFx()] pour tout autre événement
///   de suivi) ;
/// - l'**annulation coopérative** : la surcharge [#executer(Supplier, Consumer, Runnable, Consumer)]
///   conclut par `annule` quand le travail s'interrompt via
///   [fr.univ_amu.iut.commun.model.JetonAnnulation#leverSiAnnule()] - le jeton appartient à
///   l'appelant, qui le câble sur son bouton « Annuler » ;
/// - la **désactivation d'un bouton** pendant la tâche : pas d'API dédiée, un binding suffit -
///   `bouton.disableProperty().bind(occupation.enCoursProperty())` (patron #1254, cf.
///   [IndicateurOccupation#enCoursProperty()]).
///
/// [ImplementedBy] fixe l'exécution synchrone comme défaut (tests) ; l'application complète surcharge
/// par [ExecuteurTacheAsynchrone] (`CommunModule`).
@ImplementedBy(ExecuteurTacheSynchrone.class)
public interface ExecuteurTache {

    /// Exécute `travail` (jamais sur le fil JavaFX en production), puis remet son résultat à `succes`,
    /// ou, si `travail` lève, l'erreur à `echec` - `succes` et `echec` s'exécutant sur le **fil
    /// JavaFX**. Exactement l'un des deux est appelé. `echec` route typiquement le message vers le filet
    /// d'erreurs de l'écran (#795).
    <T> void executer(Supplier<T> travail, Consumer<T> succes, Consumer<Throwable> echec);

    /// Variante **annulable** (#1252) : comme [#executer(Supplier, Consumer, Consumer)], mais un travail
    /// qui s'interrompt en levant [OperationAnnuleeException] (annulation coopérative demandée sur le
    /// jeton de l'appelant) conclut par `annule` - sur le fil JavaFX, comme les deux autres callbacks.
    /// Exactement l'un des trois est appelé ; une annulation n'est **pas** un échec.
    default <T> void executer(Supplier<T> travail, Consumer<T> succes, Runnable annule, Consumer<Throwable> echec) {
        executer(travail, succes, erreur -> {
            if (erreur instanceof OperationAnnuleeException) {
                annule.run();
            } else {
                echec.accept(erreur);
            }
        });
    }

    /// Exécuteur du **fil JavaFX** (#1252) : reposte en production (`Platform.runLater`), immédiat en
    /// test (synchrone, déterministe). Base des relais d'événements émis par le travail hors fil -
    /// progression ([#relaisProgression]), suivi par fichier ou par archive - sans recopier
    /// `Platform.runLater` dans chaque relais.
    Executor surFilJavaFx();

    /// Fabrique le relais de **progression déterminée** (#1252) à passer au service : chaque point émis
    /// par le travail hors fil est appliqué par `application` **sur le fil JavaFX** (immédiatement en
    /// test), dans l'ordre d'émission.
    default Consumer<Progression> relaisProgression(Consumer<Progression> application) {
        return point -> surFilJavaFx().execute(() -> application.accept(point));
    }
}

package fr.univ_amu.iut.importation.viewmodel;

import fr.univ_amu.iut.importation.model.Progression;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

/// Suivi de la **progression déterminée** d'une opération longue de M-Import (#33/#146) : fraction
/// `[0, 1]` pour la barre + libellé d'étape complété d'une **estimation du temps restant** (#146). Extrait
/// de [ImportationViewModel] pour isoler cette préoccupation (état + ETA) de l'orchestration.
///
/// L'estimation part du **début de l'opération** (posé par [#demarrer]) : sans référence temporelle, pas
/// d'ETA (évite un temps restant aberrant calculé depuis l'origine de `System.nanoTime()`). VM agnostique
/// de l'IHM (règle ArchUnit `viewmodel_sans_javafx_ui`) : seul `javafx.beans` est importé.
public final class ProgressionImport {

    private final ReadOnlyDoubleWrapper fraction = new ReadOnlyDoubleWrapper(this, "fraction", 0.0);
    private final ReadOnlyStringWrapper message = new ReadOnlyStringWrapper(this, "message", "");

    /// Horodatage (nanos) du début de l'opération courante, pour l'ETA. `0` = pas d'opération en cours.
    private long debutNanos;

    /// Démarre le suivi : fraction à 0, libellé initial, et **pose la référence temporelle** de l'ETA.
    /// Appelé sur le fil JavaFX au lancement (import ou décompression).
    public void demarrer(String messageInitial) {
        fraction.set(0.0);
        message.set(messageInitial);
        debutNanos = System.nanoTime();
    }

    /// Applique un point de progression (#33) : met à jour la fraction et le libellé d'étape (complété de
    /// l'ETA). À appeler sur le fil JavaFX (le callback du service s'exécute hors-thread).
    public void appliquer(Progression point) {
        fraction.set(point.fraction());
        message.set(avecTempsRestant(point.libelle(), point.fraction()));
    }

    /// Remet le suivi à zéro (fin ou erreur) : fraction à 0 et libellé vide.
    public void reinitialiser() {
        fraction.set(0.0);
        message.set("");
    }

    /// Complète le libellé par une **estimation du temps restant** (#146, déléguée à [LibelleProgression])
    /// déduite du temps écoulé depuis [#demarrer]. Sans début posé, pas d'ETA.
    private String avecTempsRestant(String libelle, double fraction) {
        long ecoule = debutNanos == 0L ? 0L : System.nanoTime() - debutNanos;
        return LibelleProgression.avecTempsRestant(libelle, fraction, ecoule);
    }

    /// Fraction de progression `[0, 1]` (barre déterminée).
    public ReadOnlyDoubleProperty fractionProperty() {
        return fraction.getReadOnlyProperty();
    }

    /// Libellé d'étape en cours (« Copie X/N », « Transformation X/N », avec ETA).
    public ReadOnlyStringProperty messageProperty() {
        return message.getReadOnlyProperty();
    }
}

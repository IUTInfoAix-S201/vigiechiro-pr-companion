package fr.univ_amu.iut.audio.viewmodel;

import fr.univ_amu.iut.validation.model.LigneObservationAudio;
import fr.univ_amu.iut.validation.model.StatutObservation;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import javafx.collections.transformation.FilteredList;

/// Filtres **composables** de la table audio (#470) : plusieurs prédicats nommés combinés en **ET**,
/// appliqués à la [FilteredList] affichée. À chaque changement, on recompose la conjonction puis on
/// **notifie l'appelant** (`apresApplication`) — c'est là que le [AudioViewModel] recalcule les compteurs
/// sur le sous-ensemble effectivement affiché. Extrait du view-model pour tenir les seuils de cohésion PMD.
final class FiltresAudio {

    /// Clé du filtre de **statut de revue** (les filtres unitaires suivants — chauves-souris, taxon,
    /// références, probabilité, texte — se branchent via [#definir] avec leur propre clé).
    static final String STATUT = "statut";

    private final FilteredList<LigneObservationAudio> affichees;
    private final Runnable apresApplication;
    private final Map<String, Predicate<LigneObservationAudio>> actifs = new LinkedHashMap<>();

    FiltresAudio(FilteredList<LigneObservationAudio> affichees, Runnable apresApplication) {
        this.affichees = Objects.requireNonNull(affichees, "affichees");
        this.apresApplication = Objects.requireNonNull(apresApplication, "apresApplication");
    }

    /// Filtre de **statut de revue** : ne garde que les observations au statut donné (ou **aucun** filtre
    /// de statut si `statut` est `null`).
    void definirStatut(StatutObservation statut) {
        definir(STATUT, statut == null ? null : ligne -> ligne.statut() == statut);
    }

    /// Définit (ou **retire** si `predicat` est `null`) le filtre identifié par `nom`, puis réapplique.
    void definir(String nom, Predicate<LigneObservationAudio> predicat) {
        Objects.requireNonNull(nom, "nom");
        if (predicat == null) {
            actifs.remove(nom);
        } else {
            actifs.put(nom, predicat);
        }
        appliquer();
    }

    /// Réapplique la **conjonction** des filtres actifs (ou aucun prédicat si vide) à la liste affichée,
    /// puis notifie l'appelant pour qu'il recalcule ce qui dépend du sous-ensemble affiché (compteurs).
    void appliquer() {
        affichees.setPredicate(actifs.values().stream().reduce(Predicate::and).orElse(null));
        apresApplication.run();
    }
}

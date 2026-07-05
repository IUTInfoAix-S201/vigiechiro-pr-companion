package fr.univ_amu.iut.audio.view;

import fr.univ_amu.iut.audio.viewmodel.FormatLigneAudio;
import fr.univ_amu.iut.validation.model.LigneObservationAudio;
import fr.univ_amu.iut.validation.model.StatutObservation;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

/// Catalogue des **critères de filtrage** de la table audio (patron « à la Notion »). Chaque critère est
/// une entrée du menu « + Filtre » qui s'ajoute comme puce. Pour l'instant : **Statut** (les suivants —
/// chauves-souris, taxon, références, proba — s'ajouteront ici, un par PR).
final class CriteresAudio {

    private CriteresAudio() {}

    /// Critère **Statut de revue** : éditeur = liste déroulante (À revoir / Validée / Corrigée) dans la
    /// puce ; par défaut **À revoir** (le plus utile pour la revue), appliqué dès l'ajout.
    static CritereFiltre statut() {
        return new CritereFiltre() {
            @Override
            public String nom() {
                return "statut";
            }

            @Override
            public String libelle() {
                return "Statut";
            }

            @Override
            public Node editeur(Consumer<Predicate<LigneObservationAudio>> applique) {
                ComboBox<StatutObservation> choix = new ComboBox<>();
                choix.getItems().setAll(StatutObservation.values());
                choix.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(StatutObservation statut) {
                        return statut == null ? "" : FormatLigneAudio.libelleStatut(statut);
                    }

                    @Override
                    public StatutObservation fromString(String libelle) {
                        return null; // liste non éditable
                    }
                });
                choix.valueProperty()
                        .addListener((obs, avant, statut) ->
                                applique.accept(statut == null ? ligne -> true : ligne -> ligne.statut() == statut));
                choix.setValue(StatutObservation.NON_TOUCHEE); // déclenche l'application initiale (À revoir)
                return choix;
            }
        };
    }
}

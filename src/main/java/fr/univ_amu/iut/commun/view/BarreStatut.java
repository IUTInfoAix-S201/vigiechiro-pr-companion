package fr.univ_amu.iut.commun.view;

import fr.univ_amu.iut.commun.viewmodel.NavigationViewModel;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/// Câblage de la **barre de statut à 3 zones** du chrome (#495) : lie les trois libellés (contexte /
/// résumé / compteurs) aux zones de [NavigationViewModel#zonesStatutProperty] et **masque** le conteneur
/// tant qu'aucune zone n'a de contenu, pour ne pas laisser un bandeau sans information.
///
/// Extrait de [MainController] : cette logique de pied forme un tout cohérent et isolable, et l'en sortir
/// garde le controller du chrome sous le seuil de taille (PMD `NcssCount`).
final class BarreStatut {

    private BarreStatut() {}

    /// Lie le conteneur et ses trois libellés aux zones de statut du modèle de navigation. Le conteneur
    /// est masqué (et retiré du layout) tant que les trois zones sont vides.
    static void lier(BorderPane conteneur, Label gauche, Label centre, Label droite, NavigationViewModel navigation) {
        gauche.textProperty()
                .bind(Bindings.createStringBinding(
                        () -> navigation.getZonesStatut().gauche(), navigation.zonesStatutProperty()));
        centre.textProperty()
                .bind(Bindings.createStringBinding(
                        () -> navigation.getZonesStatut().centre(), navigation.zonesStatutProperty()));
        droite.textProperty()
                .bind(Bindings.createStringBinding(
                        () -> navigation.getZonesStatut().droite(), navigation.zonesStatutProperty()));
        var present = Bindings.createBooleanBinding(
                () -> !navigation.getZonesStatut().estVide(), navigation.zonesStatutProperty());
        conteneur.visibleProperty().bind(present);
        conteneur.managedProperty().bind(present);
    }
}

package fr.univ_amu.iut.commun.view;

import fr.univ_amu.iut.commun.viewmodel.ZonesStatut;
import javafx.beans.property.ReadOnlyObjectProperty;

/// Contrat socle **optionnel** d'un écran : fournir un **résumé** à afficher dans la **barre de statut**
/// (pied du chrome) tant que l'écran est au sommet de la navigation.
///
/// Même esprit que [EmplacementNavigation] (fil d'Ariane) : le [Navigateur] lit ce contrat par
/// `instanceof` sur le controller de l'écran courant et **lie** le pied de page à sa propriété ; quand on
/// change d'écran, le pied revient au défaut. Ainsi une info vivante (par exemple « N observation(s) »
/// en centre et « X / N revues » à droite pour la vue audio) occupe une barre autrement figée, sans que
/// chaque écran ait à gérer son nettoyage.
///
/// Le résumé est **zoné** ([ZonesStatut], #495) : gauche = contexte de l'écran, centre = résumé de
/// l'écran, droite = compteurs / état vivant. Un écran ne renseigne que les zones qui le concernent ; si
/// aucune zone n'a de contenu, le chrome masque la barre de statut.
public interface ResumeStatut {

    /// Zones de statut de l'écran, observées par le chrome et affichées dans la barre de statut. Peuvent
    /// changer au fil de la vie de l'écran (le pied se met à jour en direct).
    ReadOnlyObjectProperty<ZonesStatut> zonesStatutProperty();
}

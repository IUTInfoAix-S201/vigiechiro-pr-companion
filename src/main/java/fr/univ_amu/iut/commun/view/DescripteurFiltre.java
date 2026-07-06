package fr.univ_amu.iut.commun.view;

import java.util.List;

/// Instantané **sémantique et transportable** d'une barre de filtres (#537 étape 2) : la recherche
/// texte courante et la valeur sémantique de chaque puce active ([DescripteurCritere]), dans l'ordre
/// d'ajout. Produit par [GestionnaireFiltres#decrire()].
///
/// Base d'un **partage de filtres entre vues** (ex. rouvrir la carte avec les mêmes filtres, #476) et,
/// à terme, d'une persistance des vues sauvegardées. Distinct de [EtatFiltres] (index de contrôles,
/// mémoire de session #484) : ici tout est en clair, donc réapplicable ailleurs.
///
/// @param texte contenu de la recherche texte permanente (jamais `null`, vide si aucune)
/// @param criteres puces actives décrites sémantiquement, dans l'ordre d'ajout
public record DescripteurFiltre(String texte, List<DescripteurCritere> criteres) {

    public DescripteurFiltre {
        criteres = List.copyOf(criteres);
    }
}

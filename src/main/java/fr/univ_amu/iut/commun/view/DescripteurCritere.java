package fr.univ_amu.iut.commun.view;

import java.util.List;

/// Valeur **sémantique** d'un critère de filtre actif, sous une forme **transportable** entre vues
/// (#537 étape 2) : le nom du critère et sa/ses valeur(s) exprimées en clair (ex. `["VALIDEE"]`,
/// `["Chiroptères"]`, `["0.5"]`, `["21", "6"]`), et non en index de contrôle d'IHM.
///
/// Contrairement à [EtatCritere] (index de contrôles, pour la mémoire de session #484), ce descripteur
/// est **indépendant de l'éditeur** : il peut être appliqué à une **autre** vue qui possède un critère
/// de même [#nom()] (base de « Voir sur la carte », #476). Liste de valeurs **vide** pour un critère
/// booléen (sa seule présence active le filtre).
///
/// @param nom clé stable du critère (identique entre vues)
/// @param valeurs valeur(s) sémantique(s) courante(s), éventuellement vide
public record DescripteurCritere(String nom, List<String> valeurs) {

    public DescripteurCritere {
        valeurs = List.copyOf(valeurs);
    }
}

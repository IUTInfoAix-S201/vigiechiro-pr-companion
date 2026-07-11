package fr.univ_amu.iut.commun.view;

import java.util.List;

/// État **persistable** de la disposition des colonnes d'une table (#994) : la liste **ordonnée** des
/// colonnes gérées, chacune décrite par son **libellé** (clé stable, déjà portée par
/// [GestionnaireColonnes.Colonne] et valable aussi pour les colonnes construites par programme, sans
/// `fx:id`) et sa **visibilité**. L'ordre de la liste est l'ordre d'affichage voulu.
///
/// Miroir de [DescripteurFiltre] pour les colonnes : c'est la forme sémantique produite par
/// [GestionnaireColonnes#decrire] et rejouée par [GestionnaireColonnes#restaurer], sérialisable en JSON
/// (cf. [DescripteurColonnesJson]) pour être **transportée** entre vues et **stockée** (vues mémorisées
/// #623, préférence par écran).
///
/// Les colonnes **verrouillées** (identité) y figurent aussi : elles portent leur **position**, mais leur
/// visibilité effective reste forcée à l'affichage par [GestionnaireColonnes#restaurer].
///
/// @param colonnes colonnes gérées, dans l'ordre d'affichage
public record DescripteurColonnes(List<EtatColonne> colonnes) {

    public DescripteurColonnes {
        colonnes = List.copyOf(colonnes);
    }

    /// Une colonne dans le descripteur : son **libellé** (clé de correspondance) et sa **visibilité**.
    ///
    /// @param libelle libellé lisible de la colonne (clé stable, cf. [GestionnaireColonnes.Colonne#libelle])
    /// @param visible `true` si la colonne doit être affichée
    public record EtatColonne(String libelle, boolean visible) {}
}

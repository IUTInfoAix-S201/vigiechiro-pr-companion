package fr.univ_amu.iut.commun.model;

import java.util.List;

/// Dépôt des **vues mémorisées** (#623) : l'abstraction de persistance dont dépend le composant d'onglets
/// [fr.univ_amu.iut.commun.view.GestionnaireVues]. Interface **du domaine** (paquet `model`), implémentée
/// par l'infrastructure (`commun.model.dao.VueSauvegardeeDao`) : la couche `view` passe par elle plutôt que
/// par le DAO concret, pour respecter la règle d'architecture « la vue ne touche jamais `model.dao`/JDBC ».
public interface DepotVues {

    /// Vues mémorisées d'une feature (écran/table), pour n'afficher que les onglets de cet écran.
    List<VueSauvegardee> findByFeature(String feature);

    /// Enregistre une nouvelle vue et la renvoie avec son `id` généré.
    VueSauvegardee insert(VueSauvegardee vue);

    /// Met à jour une vue existante (nom et/ou descripteur).
    void update(VueSauvegardee vue);

    /// Supprime la vue d'identifiant `id`.
    void delete(Long id);
}

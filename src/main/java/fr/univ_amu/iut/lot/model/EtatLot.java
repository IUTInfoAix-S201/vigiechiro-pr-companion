package fr.univ_amu.iut.lot.model;

import fr.univ_amu.iut.commun.model.Alerte;
import fr.univ_amu.iut.commun.model.StatutWorkflow;
import java.util.List;

/// Projection de lecture pour l'écran **M-Lot** : l'état de dépôt d'un passage, **sans** le
/// transitionner (contrairement à [ServiceLot#preparerLot] / [ServiceLot#marquerDepose]).
///
/// Permet à l'IHM de savoir quoi proposer selon l'avancement : préparer le lot (depuis `Vérifié`),
/// marquer déposé (depuis `Prêt à déposer`), ou simplement constater le dépôt (`Déposé`). Le chemin
/// du dossier (R22) est la cible du téléversement manuel, le volume et le nombre de séquences en
/// donnent l'ordre de grandeur, et les éventuelles alertes bloquantes (R14) expliquent pourquoi la
/// préparation serait refusée.
///
/// @param statut statut workflow courant du passage
/// @param cheminDossier chemin du dossier de session à téléverser (R22), `null` si pas de session
/// @param nombreSequences nombre de séquences transformées du lot
/// @param volumeSequencesOctets volume total des séquences en octets, `null` si non calculé
/// @param alertesBloquantes alertes de cohérence empêchant la préparation (R14), vide si conforme
/// @param deposeLe date de dépôt (ISO), `null` tant que le passage n'est pas déposé
public record EtatLot(
        StatutWorkflow statut,
        String cheminDossier,
        int nombreSequences,
        Long volumeSequencesOctets,
        List<Alerte> alertesBloquantes,
        String deposeLe) {

    public EtatLot {
        alertesBloquantes = List.copyOf(alertesBloquantes);
    }
}

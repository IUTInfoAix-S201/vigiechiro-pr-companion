package fr.univ_amu.iut.commun.api;

import fr.univ_amu.iut.commun.model.CertitudeObservateur;

/// Une observation (détection) Tadarida d'un fichier, telle que renvoyée par
/// `GET /participations/#id/donnees` (#719, axe 4.2). Vue destinée à alimenter une
/// `LigneObservation` locale (mêmes unités que le CSV Tadarida : fréquence en kHz, temps en secondes).
///
/// @param indiceServeur indice **brut** de l'observation dans le tableau `observations` de sa donnée
///     (#1139) : l'identifiant positionnel attendu par `PATCH /donnees/{id}/observations/{index}`
///     (contrat #1203). Capturé sur le tableau JSON complet : il peut différer de la position dans la
///     liste parsée, qui filtre les observations sans taxon Tadarida
/// @param taxonTadarida code du taxon proposé par Tadarida (`tadarida_taxon.libelle_court`)
/// @param probabilite probabilité Tadarida dans `[0,1]` (`tadarida_probabilite`), ou `null`
/// @param frequenceMediane fréquence médiane en kHz (`frequence_mediane`), ou `null`
/// @param tempsDebut début dans le fichier en secondes (`temps_debut`), ou `null`
/// @param tempsFin fin dans le fichier en secondes (`temps_fin`), ou `null`
/// @param taxonAutre 2e proposition Tadarida (`tadarida_taxon_autre[0].taxon.libelle_court`), ou `null`
/// @param taxonObservateur code retenu par l'observateur sur la plateforme (`observateur_taxon`), ou
///     `null` tant qu'aucune correction n'a été poussée
/// @param certitudeObservateur certitude déclarée par l'observateur (`observateur_probabilite`,
///     énumération `SUR|PROBABLE|POSSIBLE` côté serveur, contrat #1203), ou `null`
public record ObservationVigieChiro(
        int indiceServeur,
        String taxonTadarida,
        Double probabilite,
        Double frequenceMediane,
        Double tempsDebut,
        Double tempsFin,
        String taxonAutre,
        String taxonObservateur,
        CertitudeObservateur certitudeObservateur) {}

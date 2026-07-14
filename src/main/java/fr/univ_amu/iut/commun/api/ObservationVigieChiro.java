package fr.univ_amu.iut.commun.api;

import fr.univ_amu.iut.commun.model.Certitude;
import java.util.List;

/// Une observation (détection) Tadarida d'un fichier, telle que renvoyée par
/// `GET /participations/#id/donnees` (#719, axe 4.2). Vue destinée à alimenter une
/// `LigneObservation` locale (mêmes unités que le CSV Tadarida : fréquence en kHz, temps en secondes).
///
/// Elle porte les **trois avis** que la plateforme distingue sur une même détection : Tadarida
/// *propose* (`tadarida_*`), l'observateur *corrige* (`observateur_*`), le validateur du MNHN
/// *tranche* (`validateur_*`, #1417) — plus le **fil de discussion** qui les relie. Tout cela arrive
/// dans la **même** charge utile : l'application le recevait déjà et le jetait.
///
/// La certitude partage le **même domaine fermé** côté serveur pour l'observateur et pour le
/// validateur (`SUR | PROBABLE | POSSIBLE`, contrat #1203) : d'où le même type ici, dont le nom
/// ([Certitude]) est resté celui de son premier usage.
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
/// @param taxonValidateur code **tranché par le validateur** du MNHN (`validateur_taxon`), ou `null`
///     tant qu'aucun expert ne s'est prononcé. En **lecture seule** : le serveur refuse (403) qu'un
///     jeton de rôle `Observateur` pose ce champ (spike de #724)
/// @param certitudeValidateur certitude déclarée par le validateur (`validateur_probabilite`), ou `null`
/// @param messages **fil de discussion** de l'observation, dans l'ordre du serveur ; jamais `null`
///     (liste vide si le fil n'a jamais été ouvert, ce qui est le cas courant)
public record ObservationVigieChiro(
        int indiceServeur,
        String taxonTadarida,
        Double probabilite,
        Double frequenceMediane,
        Double tempsDebut,
        Double tempsFin,
        String taxonAutre,
        String taxonObservateur,
        Certitude certitudeObservateur,
        String taxonValidateur,
        Certitude certitudeValidateur,
        List<MessageVigieChiro> messages) {

    /// Fil **immuable** et tolérant au `null` : un fil absent est un fil vide, pas une erreur. Le record
    /// est une valeur — son fil ne doit pas pouvoir muter dans le dos de qui l'a reçu.
    public ObservationVigieChiro {
        messages = messages == null ? List.of() : List.copyOf(messages);
    }
}

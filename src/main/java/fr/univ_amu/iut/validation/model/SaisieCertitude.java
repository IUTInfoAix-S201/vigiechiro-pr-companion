package fr.univ_amu.iut.validation.model;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.model.Certitude;
import fr.univ_amu.iut.commun.model.RegleMetierException;
import fr.univ_amu.iut.validation.model.dao.ObservationDao;
import java.util.List;
import java.util.Objects;

/// Pose (ou efface) la **certitude observateur** (#1139) d'une ou plusieurs observations : le jugement
/// `SUR | PROBABLE | POSSIBLE` que l'observateur déclare **manuellement** à la revue, en miroir du site
/// VigieChiro (« Confiance observateur », vide par défaut). C'est la valeur que la plateforme exigera
/// avec le taxon au moment de pousser une correction (#723) ; localement elle reste effaçable
/// (`certitude = null` = « non renseignée »), l'API, elle, ne sait pas retirer une certitude poussée.
///
/// Classe **dédiée** (et non une n-ième méthode de [ServiceValidation], déjà au plafond de cohésion),
/// sur le modèle de [MarquageDouteux] pour l'unitaire et de [RevueEnLot] pour le lot : lectures avant
/// la transaction, écritures groupées atomiquement via [ObservationDao#updateTout].
public class SaisieCertitude {

    private final ObservationDao observationDao;

    @Inject
    public SaisieCertitude(ObservationDao observationDao) {
        this.observationDao = Objects.requireNonNull(observationDao, "observationDao");
    }

    /// Pose `certitude` (`null` = effacer) sur l'observation `idObservation`.
    ///
    /// @return l'observation relue, à jour
    /// @throws RegleMetierException si l'observation est introuvable
    public Observation poser(Long idObservation, Certitude certitude) {
        Observation mise = charger(idObservation).avecCertitude(certitude);
        observationDao.update(mise);
        return mise;
    }

    /// Pose `certitude` (`null` = effacer) sur toutes les observations `ids`, en **une transaction
    /// atomique** (tout réussit ou tout est annulé, comme les actions de [RevueEnLot]). Renvoie le
    /// nombre d'observations écrites.
    public int poser(List<Long> ids, Certitude certitude) {
        List<Observation> mises = ids.stream()
                .map(this::charger)
                .map(o -> o.avecCertitude(certitude))
                .toList();
        observationDao.updateTout(mises);
        return mises.size();
    }

    private Observation charger(Long idObservation) {
        return observationDao
                .findById(idObservation)
                .orElseThrow(() -> new RegleMetierException("Observation introuvable : " + idObservation));
    }
}

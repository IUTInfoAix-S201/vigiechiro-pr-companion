package fr.univ_amu.iut.lot.viewmodel;

import fr.univ_amu.iut.commun.model.RegleMetierException;
import fr.univ_amu.iut.lot.model.BilanDepot;
import fr.univ_amu.iut.lot.model.DepotVigieChiro;
import fr.univ_amu.iut.lot.model.ServiceLot;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/// ViewModel du **téléversement d'une nuit sur VigieChiro** (#142), distinct de [LotViewModel] : le dépôt
/// est un concern à part (et [LotViewModel] est déjà volumineux). Coordonne la résolution des séquences
/// (via [ServiceLot]) et le dépôt lui-même (via [DepotVigieChiro]).
///
/// Le dépôt est **optionnel** : `depot` est vide dans les injecteurs partiels de capture (feature `lot`
/// sans `connexion`, donc sans client HTTP) ; dans l'application complète il est présent (cf.
/// `DepotVigieChiroModule`). VM agnostique de l'IHM (règle ArchUnit `viewmodel_sans_javafx_ui`).
public class DepotViewModel {

    private final ServiceLot service;
    private final Optional<DepotVigieChiro> depot;

    public DepotViewModel(ServiceLot service, Optional<DepotVigieChiro> depot) {
        this.service = Objects.requireNonNull(service, "service");
        this.depot = Objects.requireNonNull(depot, "depot");
    }

    /// `true` si le téléversement est **disponible** dans ce contexte (application complète connectée à
    /// VigieChiro) : permet à l'IHM de masquer / désactiver l'action là où le dépôt n'a pas de sens.
    public boolean disponible() {
        return depot.isPresent();
    }

    /// Téléverse les **séquences transformées** du passage sur VigieChiro : crée la participation puis envoie
    /// les fichiers. **Bloquant** (réseau) : à appeler **hors du fil JavaFX** (le controller l'enveloppe dans
    /// un fil virtuel, comme la génération d'archives). Lève une [RegleMetierException] si le dépôt est
    /// indisponible dans ce contexte, ou s'il n'y a aucune séquence à déposer.
    ///
    /// @param idPassage passage (nuit) à déposer
    /// @return le bilan du dépôt (participation créée, fichiers déposés / en échec)
    public BilanDepot televerser(Long idPassage) {
        Objects.requireNonNull(idPassage, "idPassage");
        DepotVigieChiro depotVigieChiro =
                depot.orElseThrow(() -> new RegleMetierException("Dépôt VigieChiro indisponible dans ce contexte."));
        List<Path> fichiers = service.sequencesADeposer(idPassage);
        if (fichiers.isEmpty()) {
            throw new RegleMetierException("Aucune séquence transformée à déposer pour ce passage.");
        }
        return depotVigieChiro.deposer(idPassage, fichiers);
    }
}

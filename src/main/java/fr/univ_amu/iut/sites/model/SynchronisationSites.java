package fr.univ_amu.iut.sites.model;

import fr.univ_amu.iut.commun.api.ClientVigieChiro;
import fr.univ_amu.iut.commun.api.RapportSynchro;
import java.util.Objects;
import java.util.Optional;

/// **Synchronisation des sites à la demande** (#1045) : rejoue le pull [RapprochementSites]
/// (exécuté sinon à la connexion uniquement) depuis M-Sites, sans se reconnecter. Même sémantique
/// conservatrice : liste distante vide → no-op, best-effort par site, jamais d’écrasement de
/// données locales. Activée par `OptionalBinder` (absente hors app complète), patron de
/// `SynchronisationParticipation` (#937).
public final class SynchronisationSites {

    private final RapprochementSites rapprochement;
    private final ClientVigieChiro client;

    public SynchronisationSites(RapprochementSites rapprochement, ClientVigieChiro client) {
        this.rapprochement = Objects.requireNonNull(rapprochement, "rapprochement");
        this.client = Objects.requireNonNull(client, "client");
    }

    /// Rejoue le rapprochement des sites. À appeler **hors du fil JavaFX** (réseau + écritures base).
    public Optional<RapportSynchro> synchroniser() {
        return rapprochement.synchroniser(client);
    }
}

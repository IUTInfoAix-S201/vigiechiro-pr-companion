package fr.univ_amu.iut.connexion.viewmodel;

import fr.univ_amu.iut.commun.api.ClientVigieChiro;
import fr.univ_amu.iut.commun.api.ProfilVigieChiro;
import fr.univ_amu.iut.commun.api.RapprochementVigieChiro;
import fr.univ_amu.iut.connexion.model.StockageConnexion;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

/// ViewModel de la connexion VigieChiro (#727) : reflète l'état de connexion (identité en cache) et
/// pilote la connexion (vérifier un token collé via `GET /moi`) et la déconnexion.
///
/// Agnostique de l'IHM (`javafx.beans` uniquement). L'appel réseau de [#connecter] est **bloquant** :
/// le controller le lance hors du fil JavaFX, puis rafraîchit l'état affiché via [#rafraichir].
public class ConnexionViewModel {

    private final StockageConnexion stockage;
    private final ClientVigieChiro client;
    private final Set<RapprochementVigieChiro> rapprocheurs;

    private final ReadOnlyStringWrapper identite = new ReadOnlyStringWrapper(this, "identite", "");
    private final ReadOnlyBooleanWrapper connecte = new ReadOnlyBooleanWrapper(this, "connecte", false);

    public ConnexionViewModel(
            StockageConnexion stockage, ClientVigieChiro client, Set<RapprochementVigieChiro> rapprocheurs) {
        this.stockage = Objects.requireNonNull(stockage, "stockage");
        this.client = Objects.requireNonNull(client, "client");
        this.rapprocheurs = Set.copyOf(Objects.requireNonNull(rapprocheurs, "rapprocheurs"));
    }

    /// Recalcule l'état affiché depuis le stockage local (sans réseau). À appeler sur le fil JavaFX.
    public void rafraichir() {
        Optional<ProfilVigieChiro> profil = stockage.profil();
        connecte.set(profil.isPresent());
        identite.set(profil.map(ConnexionViewModel::libelle).orElse("Non connecté"));
    }

    /// **Vérifie et enregistre** un token (opération réseau : `GET /moi`). Renvoie l'identité si le
    /// token est valide (et la persiste), sinon efface la connexion et renvoie vide. À lancer hors du
    /// fil JavaFX ; ne touche à aucune propriété (le controller appelle [#rafraichir] ensuite).
    public Optional<ProfilVigieChiro> connecter(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        String propre = token.trim();
        stockage.enregistrer(propre, null);
        Optional<ProfilVigieChiro> profil = client.moi();
        if (profil.isPresent()) {
            stockage.enregistrer(propre, profil.get());
            amorcerRapprochements();
        } else {
            stockage.effacer();
        }
        return profil;
    }

    /// Amorce les correspondances locales ↔ VigieChiro (taxons, sites) juste après une connexion réussie
    /// (#728). Chaque rapprocheur est **best-effort** (il avale ses propres erreurs) : un échec ne
    /// compromet pas la connexion. Déjà hors du fil JavaFX (appelé depuis [#connecter]).
    private void amorcerRapprochements() {
        for (RapprochementVigieChiro rapprocheur : rapprocheurs) {
            rapprocheur.synchroniser(client);
        }
    }

    /// Efface la connexion locale. À suivre d'un [#rafraichir].
    public void deconnecter() {
        stockage.effacer();
    }

    private static String libelle(ProfilVigieChiro profil) {
        String pseudo = profil.pseudo() == null ? "?" : profil.pseudo();
        String role = profil.role() == null ? "" : " (" + profil.role() + ")";
        return "Connecté : " + pseudo + role;
    }

    /// Libellé d'identité (« Connecté : X (Observateur) » ou « Non connecté »).
    public ReadOnlyStringProperty identiteProperty() {
        return identite.getReadOnlyProperty();
    }

    /// `true` si un token valide est enregistré (pilote l'activation du bouton de déconnexion).
    public ReadOnlyBooleanProperty connecteProperty() {
        return connecte.getReadOnlyProperty();
    }
}

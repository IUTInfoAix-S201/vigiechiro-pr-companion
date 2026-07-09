package fr.univ_amu.iut.connexion.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import fr.univ_amu.iut.commun.view.OuvrirConnexion;
import fr.univ_amu.iut.connexion.model.StockageConnexion;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/// Façade d'ouverture de la **modale « Connexion VigieChiro »** (#727/#741). Implémente le contrat
/// socle [OuvrirConnexion] : le menu ☰ du chrome ouvre la modale et affiche l'état de connexion sans
/// que `commun` dépende de la feature.
///
/// Charge le FXML avec la `controllerFactory` Guice (comme les autres modales du projet) et l'affiche
/// en fenêtre modale applicative, **sans propriétaire** : la modale est déclenchée depuis le menu, hors
/// de tout contexte de fenêtre.
@Singleton
public final class NavigationConnexion implements OuvrirConnexion {

    private final Injector injector;
    private final StockageConnexion stockage;

    @Inject
    public NavigationConnexion(Injector injector, StockageConnexion stockage) {
        this.injector = Objects.requireNonNull(injector, "injector");
        this.stockage = Objects.requireNonNull(stockage, "stockage");
    }

    /// Ouvre la modale de connexion (non bloquante).
    @Override
    public void ouvrir() {
        FXMLLoader loader = new FXMLLoader(NavigationConnexion.class.getResource("ConnexionModale.fxml"));
        loader.setControllerFactory(injector::getInstance);
        try {
            Parent vue = loader.load();
            Stage modale = new Stage();
            modale.initModality(Modality.APPLICATION_MODAL);
            modale.setTitle("Connexion VigieChiro");
            modale.setScene(new Scene(vue));
            modale.show();
        } catch (IOException echec) {
            throw new UncheckedIOException("Chargement FXML impossible : " + loader.getLocation(), echec);
        }
    }

    /// Libellé de l'entrée de menu selon l'état stocké (sans réseau) : identité si connecté, invite
    /// sinon. Emoji cohérent avec les autres entrées du menu ☰ (💾 / ↩ / 🧹).
    @Override
    public String libelleMenu() {
        return stockage.profil()
                .map(profil -> "✅ VigieChiro : "
                        + (profil.pseudo() == null ? "?" : profil.pseudo())
                        + (profil.role() == null ? "" : " (" + profil.role() + ")"))
                .orElse("🔌 Se connecter à VigieChiro…");
    }
}

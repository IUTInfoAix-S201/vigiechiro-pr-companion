package fr.univ_amu.iut.connexion.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/// Façade d'ouverture de la **modale « Connexion VigieChiro »** (#727). Charge le FXML avec la
/// `controllerFactory` Guice (comme les autres modales du projet) et l'affiche en fenêtre modale
/// applicative, **sans propriétaire** : la modale est déclenchée depuis une carte d'accueil, hors de
/// tout contexte de fenêtre.
@Singleton
public final class NavigationConnexion {

    private final Injector injector;

    @Inject
    public NavigationConnexion(Injector injector) {
        this.injector = Objects.requireNonNull(injector, "injector");
    }

    /// Ouvre la modale de connexion (non bloquante).
    public void ouvrirModale() {
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
}

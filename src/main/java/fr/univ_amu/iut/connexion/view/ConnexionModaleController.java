package fr.univ_amu.iut.connexion.view;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.api.ProfilVigieChiro;
import fr.univ_amu.iut.commun.view.OuvreurDeLien;
import fr.univ_amu.iut.connexion.viewmodel.ConnexionViewModel;
import java.util.Objects;
import java.util.Optional;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/// Controller de la **modale « Connexion VigieChiro »** (`ConnexionModale.fxml`, #727/#741).
///
/// Guide l'utilisateur en trois étapes pour relier l'app à son compte, **sans fichier externe** :
/// 1. ouvrir VigieChiro (navigateur système, via [OuvreurDeLien]) ;
/// 2. installer le **marque-page** qui copie le token (bouton « Copier le marque-page ») ;
/// 3. coller le token, vérifié via `GET /moi` **hors du fil JavaFX** (thread virtuel).
///
/// Pur câblage : lie les contrôles aux propriétés du [ConnexionViewModel].
public class ConnexionModaleController {

    /// Page d'accueil de la plateforme (connexion GitHub/Google), ouverte à l'étape 1.
    private static final String URL_VIGIECHIRO = "https://vigiechiro.herokuapp.com";

    /// Marque-page (bookmarklet) copié à l'étape 2 : lit le token du `localStorage` de VigieChiro et le
    /// place dans le presse-papier (repli `prompt` si l'API clipboard du navigateur est indisponible).
    private static final String MARQUE_PAGE = "javascript:(function(){"
            + "var t=localStorage.getItem('auth-session-token');"
            + "if(!t){alert('Aucun token : connectez-vous sur VigieChiro puis recliquez ce marque-page.');return;}"
            + "if(navigator.clipboard){navigator.clipboard.writeText(t).then("
            + "function(){alert('Token VigieChiro copie ('+t.length+' caracteres).');},"
            + "function(){window.prompt('Copiez votre token VigieChiro :',t);});}"
            + "else{window.prompt('Copiez votre token VigieChiro :',t);}})();";

    private final ConnexionViewModel viewModel;
    private final OuvreurDeLien ouvreurDeLien;

    @FXML
    private VBox racine;

    @FXML
    private TextField champToken;

    @FXML
    private Label labelIdentite;

    @FXML
    private Label labelMessage;

    @FXML
    private Button boutonConnecter;

    @FXML
    private Button boutonDeconnecter;

    @Inject
    public ConnexionModaleController(ConnexionViewModel viewModel, OuvreurDeLien ouvreurDeLien) {
        this.viewModel = Objects.requireNonNull(viewModel, "viewModel");
        this.ouvreurDeLien = Objects.requireNonNull(ouvreurDeLien, "ouvreurDeLien");
    }

    @FXML
    private void initialize() {
        labelIdentite.textProperty().bind(viewModel.identiteProperty());
        boutonDeconnecter.disableProperty().bind(viewModel.connecteProperty().not());
        viewModel.rafraichir();
    }

    /// Étape 1 : ouvre la plateforme dans le navigateur système (pour s'y connecter).
    @FXML
    private void ouvrirSite() {
        ouvreurDeLien.ouvrir(URL_VIGIECHIRO);
    }

    /// Étape 2 : copie le marque-page dans le presse-papier, à installer comme favori.
    @FXML
    private void copierMarquePage() {
        ClipboardContent contenu = new ClipboardContent();
        contenu.putString(MARQUE_PAGE);
        Clipboard.getSystemClipboard().setContent(contenu);
        labelMessage.setText(
                "Marque-page copié : créez un favori, collez-le comme adresse, puis cliquez-le sur l'onglet VigieChiro.");
    }

    /// Étape 3 : vérifie et enregistre le token collé.
    @FXML
    private void connecter() {
        String token = champToken.getText();
        if (token == null || token.isBlank()) {
            labelMessage.setText("Collez d'abord votre token VigieChiro.");
            return;
        }
        boutonConnecter.setDisable(true);
        labelMessage.setText("Vérification en cours…");
        Thread.ofVirtual().name("connexion-vigiechiro").start(() -> {
            Optional<ProfilVigieChiro> profil = viewModel.connecter(token);
            Platform.runLater(() -> {
                viewModel.rafraichir();
                boutonConnecter.setDisable(false);
                if (profil.isPresent()) {
                    labelMessage.setText("Connexion réussie.");
                    champToken.clear();
                } else {
                    labelMessage.setText("Token invalide ou expiré : recollez-en un depuis le site VigieChiro.");
                }
            });
        });
    }

    @FXML
    private void deconnecter() {
        viewModel.deconnecter();
        viewModel.rafraichir();
        champToken.clear();
        labelMessage.setText("Déconnecté.");
    }

    @FXML
    private void fermer() {
        ((Stage) racine.getScene().getWindow()).close();
    }
}

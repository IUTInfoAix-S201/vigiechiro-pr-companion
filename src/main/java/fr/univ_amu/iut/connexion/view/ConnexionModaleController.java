package fr.univ_amu.iut.connexion.view;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.api.ProfilVigieChiro;
import fr.univ_amu.iut.connexion.viewmodel.ConnexionViewModel;
import java.util.Objects;
import java.util.Optional;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/// Controller de la **modale « Connexion VigieChiro »** (`ConnexionModale.fxml`, #727).
///
/// Colle un token, vérifie l'identité via `GET /moi` **hors du fil JavaFX** (thread virtuel, comme la
/// récupération météo), affiche l'état, et permet de se déconnecter. Pur câblage : lie les contrôles
/// aux propriétés du [ConnexionViewModel].
public class ConnexionModaleController {

    private final ConnexionViewModel viewModel;

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
    public ConnexionModaleController(ConnexionViewModel viewModel) {
        this.viewModel = Objects.requireNonNull(viewModel, "viewModel");
    }

    @FXML
    private void initialize() {
        labelIdentite.textProperty().bind(viewModel.identiteProperty());
        boutonDeconnecter.disableProperty().bind(viewModel.connecteProperty().not());
        viewModel.rafraichir();
    }

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

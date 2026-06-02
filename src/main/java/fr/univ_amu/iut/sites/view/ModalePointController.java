package fr.univ_amu.iut.sites.view;

import com.google.inject.Inject;
import fr.univ_amu.iut.sites.model.PointDEcoute;
import fr.univ_amu.iut.sites.model.Site;
import fr.univ_amu.iut.sites.viewmodel.PointEditViewModel;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/// Controller de la **modale d'ajout / d'édition d'un point d'écoute** (`ModalePoint.fxml`).
///
/// Lie les champs (code, descriptif, latitude, longitude) en bidirectionnel au
/// [PointEditViewModel], et reflète son état de présentation : titre et libellé du bouton (selon
/// création/édition), activation du bouton de validation ([PointEditViewModel#peutEnregistrer()]),
/// surlignage du code invalide (R2) et message d'erreur métier.
///
/// La modale se ferme elle-même via sa propre fenêtre ; après un enregistrement réussi, elle
/// exécute le `Runnable` fourni par l'appelant (typiquement le rafraîchissement de M-Site-detail).
public class ModalePointController {

    private static final String STYLE_CHAMP_INVALIDE = "champ-invalide";

    private final PointEditViewModel viewModel;
    private Runnable apresSucces = () -> {};

    @FXML
    private VBox racine;

    @FXML
    private Label titreModale;

    @FXML
    private TextField champCode;

    @FXML
    private Label messageErreur;

    @FXML
    private TextArea champDescription;

    @FXML
    private TextField champLatitude;

    @FXML
    private TextField champLongitude;

    @FXML
    private Button boutonValider;

    @Inject
    public ModalePointController(PointEditViewModel viewModel) {
        this.viewModel = Objects.requireNonNull(viewModel, "viewModel");
    }

    @FXML
    private void initialize() {
        titreModale.textProperty().bind(viewModel.titreProperty());
        champCode.textProperty().bindBidirectional(viewModel.codeProperty());
        champDescription.textProperty().bindBidirectional(viewModel.descriptionProperty());
        champLatitude.textProperty().bindBidirectional(viewModel.latitudeProperty());
        champLongitude.textProperty().bindBidirectional(viewModel.longitudeProperty());
        boutonValider.textProperty().bind(viewModel.libelleBoutonProperty());
        boutonValider.disableProperty().bind(viewModel.peutEnregistrer().not());
        messageErreur.textProperty().bind(viewModel.messageErreurProperty());
        messageErreur.visibleProperty().bind(viewModel.messageErreurProperty().isNotEmpty());
        messageErreur.managedProperty().bind(viewModel.messageErreurProperty().isNotEmpty());
        viewModel.codeValide().addListener((observable, avant, valide) -> majStyleCode());
        viewModel.codeProperty().addListener((observable, avant, apres) -> majStyleCode());
    }

    /// Prépare la modale en mode création et mémorise l'action de succès.
    public void demarrerCreation(Site site, Runnable apresSucces) {
        this.apresSucces = Objects.requireNonNull(apresSucces, "apresSucces");
        viewModel.preparerCreation(site);
        majStyleCode();
    }

    /// Prépare la modale en mode édition (champs pré-remplis) et mémorise l'action de succès.
    public void demarrerEdition(Site site, PointDEcoute point, Runnable apresSucces) {
        this.apresSucces = Objects.requireNonNull(apresSucces, "apresSucces");
        viewModel.preparerEdition(site, point);
        majStyleCode();
    }

    @FXML
    private void valider() {
        if (viewModel.enregistrer()) {
            apresSucces.run();
            fermer();
        }
    }

    @FXML
    private void annuler() {
        fermer();
    }

    /// Surligne le champ code uniquement quand il est non vide et invalide (R2).
    private void majStyleCode() {
        boolean afficherErreur =
                !viewModel.codeValide().get() && !champCode.getText().isBlank();
        champCode.getStyleClass().remove(STYLE_CHAMP_INVALIDE);
        if (afficherErreur) {
            champCode.getStyleClass().add(STYLE_CHAMP_INVALIDE);
        }
    }

    private void fermer() {
        ((Stage) racine.getScene().getWindow()).close();
    }
}

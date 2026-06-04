package fr.univ_amu.iut.lot.view;

import com.google.inject.Inject;
import fr.univ_amu.iut.lot.viewmodel.LotViewModel;
import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

/// Controller de l'écran **M-Lot** (`Lot.fxml`).
///
/// Pur câblage (patron CM4) : lie le statut, le récapitulatif, le dossier à téléverser, les alertes
/// de cohérence (R14) et les deux actions du dépôt au [LotViewModel]. « Préparer le lot » et
/// « Marquer déposé » ne sont actifs que dans l'état workflow adéquat ; la zone d'alertes
/// n'apparaît qu'en présence d'alertes bloquantes. Aucun accès base de données ni logique métier ici
/// (règle ArchUnit `view_sans_jdbc`).
public class LotController {

    private final LotViewModel viewModel;

    // TODO (M-Lot) : déclarez les champs @FXML correspondant aux fx:id de Lot.fxml (Label, Button,
    //   ListView, VBox...), liez-les au LotViewModel dans « @FXML private void initialize() », et
    //   ajoutez les handlers @FXML des boutons (onAction="#preparer"/"#deposer"). Référence : feature sites.
    // --solution--
    @FXML
    private Label lblStatut;

    @FXML
    private Label lblRecap;

    @FXML
    private Label lblCheminDossier;

    @FXML
    private VBox zoneAlertes;

    @FXML
    private ListView<String> listeAlertes;

    @FXML
    private Button btnPreparer;

    @FXML
    private Button btnDeposer;

    @FXML
    private Label lblMessage;

    // --end-solution--

    @Inject
    public LotController(LotViewModel viewModel) {
        this.viewModel = Objects.requireNonNull(viewModel, "viewModel");
    }

    // --solution--
    @FXML
    private void initialize() {
        lblStatut.textProperty().bind(viewModel.statutProperty());
        lblRecap.textProperty().bind(viewModel.recapProperty());
        lblCheminDossier.textProperty().bind(viewModel.cheminDossierProperty());

        listeAlertes.setItems(viewModel.alertes());
        // La zone d'alertes n'a de sens qu'en présence d'alertes bloquantes (R14).
        BooleanBinding alertesPresentes = Bindings.isNotEmpty(viewModel.alertes());
        zoneAlertes.visibleProperty().bind(alertesPresentes);
        zoneAlertes.managedProperty().bind(alertesPresentes);

        btnPreparer.disableProperty().bind(viewModel.peutPreparerProperty().not());
        btnDeposer.disableProperty().bind(viewModel.peutDeposerProperty().not());

        lblMessage.textProperty().bind(viewModel.messageProperty());
        var messagePresent = viewModel.messageProperty().isNotEmpty();
        lblMessage.visibleProperty().bind(messagePresent);
        lblMessage.managedProperty().bind(messagePresent);
    }

    // --end-solution--

    /// Ouvre l'écran sur le passage `idPassage`. Appelée par [NavigationLot] après le chargement FXML.
    public void ouvrirSur(Long idPassage) {
        viewModel.ouvrirSur(idPassage);
    }

    // --solution--
    @FXML
    private void preparer() {
        viewModel.preparer();
    }

    @FXML
    private void deposer() {
        viewModel.deposer();
    }
    // --end-solution--
}

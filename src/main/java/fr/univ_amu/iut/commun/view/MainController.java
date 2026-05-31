package fr.univ_amu.iut.commun.view;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.viewmodel.NavigationViewModel;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/// Controller du chrome principal (`MainView.fxml`).
///
/// Instancié par Guice via la `controllerFactory` du `FXMLLoader` (cf. [fr.univ_amu.iut.App]) :
/// il reçoit par injection le [NavigationViewModel] (état observable du chrome) et le
/// [Navigateur] (service de swap de la zone centrale). Son rôle se limite au **câblage** :
/// lier les labels du chrome aux propriétés du ViewModel et lier le centre du `BorderPane`
/// à la vue centrale publiée par le [Navigateur]. Aucune logique métier ici.
public class MainController {

  private final NavigationViewModel navigation;
  private final Navigateur navigateur;

  @FXML private BorderPane racine;
  @FXML private Label titreApplication;
  @FXML private Label filAriane;
  @FXML private Label pied;

  @Inject
  public MainController(NavigationViewModel navigation, Navigateur navigateur) {
    this.navigation = navigation;
    this.navigateur = navigateur;
  }

  /// Appelée par le `FXMLLoader` une fois les `@FXML` injectés. Câble les bindings.
  @FXML
  private void initialize() {
    titreApplication.textProperty().bind(navigation.titreApplicationProperty());
    filAriane.textProperty().bind(navigation.filArianeProperty());
    pied.textProperty().bind(navigation.piedDePageProperty());

    // La zone d'accueil déclarée dans le FXML devient la vue centrale initiale, puis le centre
    // du BorderPane suit la propriété du Navigateur : toute navigation passe par afficher(...).
    navigateur.afficher((Parent) racine.getCenter());
    racine.centerProperty().bind(navigateur.vueCentraleProperty());
  }
}

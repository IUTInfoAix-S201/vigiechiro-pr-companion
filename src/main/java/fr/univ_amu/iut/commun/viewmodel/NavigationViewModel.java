package fr.univ_amu.iut.commun.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/// ViewModel du socle de navigation : porte l'état observable du chrome principal.
///
/// Trois informations transverses, exposées en propriétés JavaFX observables :
///  - le titre de l'application (barre haute),
///  - la vue courante (identifiant logique de la zone centrale affichée),
///  - le fil d'Ariane (libellé lisible du chemin de navigation).
///
/// Cette classe est volontairement **agnostique de l'IHM** : elle n'importe que
/// `javafx.beans.property` (le modèle observable), jamais `javafx.scene`, `javafx.fxml`
/// ni `javafx.stage`. La règle ArchUnit `viewmodel_sans_javafx_ui` verrouille cette frontière.
/// La vue (controllers FXML) se lie à ces propriétés ; le [fr.univ_amu.iut.commun.view.Navigateur]
/// les met à jour quand une feature change de zone centrale.
public class NavigationViewModel {

  private final StringProperty titreApplication =
      new SimpleStringProperty(this, "titreApplication", "VigieChiro PR Companion");
  private final StringProperty vueCourante =
      new SimpleStringProperty(this, "vueCourante", "accueil");
  private final StringProperty filAriane = new SimpleStringProperty(this, "filAriane", "Accueil");
  private final StringProperty piedDePage =
      new SimpleStringProperty(this, "piedDePage", "SAÉ 2.01 · IUT d'Aix-Marseille");

  /// Propriété observable du titre de l'application affiché dans la barre haute.
  public StringProperty titreApplicationProperty() {
    return titreApplication;
  }

  public String getTitreApplication() {
    return titreApplication.get();
  }

  public void setTitreApplication(String valeur) {
    titreApplication.set(valeur);
  }

  /// Propriété observable de la vue courante (identifiant logique, ex. `accueil`, `sites`).
  public StringProperty vueCouranteProperty() {
    return vueCourante;
  }

  public String getVueCourante() {
    return vueCourante.get();
  }

  public void setVueCourante(String valeur) {
    vueCourante.set(valeur);
  }

  /// Propriété observable du fil d'Ariane (libellé lisible affiché à l'utilisateur).
  public StringProperty filArianeProperty() {
    return filAriane;
  }

  public String getFilAriane() {
    return filAriane.get();
  }

  public void setFilAriane(String valeur) {
    filAriane.set(valeur);
  }

  /// Propriété observable du texte de pied de page.
  public StringProperty piedDePageProperty() {
    return piedDePage;
  }

  public String getPiedDePage() {
    return piedDePage.get();
  }

  public void setPiedDePage(String valeur) {
    piedDePage.set(valeur);
  }

  /// Met à jour l'état de navigation en une étape : la vue courante et son libellé de fil
  /// d'Ariane. Les features appellent cette méthode quand elles prennent la main sur la zone
  /// centrale, pour garder le chrome cohérent avec le contenu affiché.
  public void naviguerVers(String vue, String libelleFilAriane) {
    vueCourante.set(vue);
    filAriane.set(libelleFilAriane);
  }
}

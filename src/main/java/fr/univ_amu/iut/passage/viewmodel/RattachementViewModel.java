package fr.univ_amu.iut.passage.viewmodel;

import fr.univ_amu.iut.commun.model.Prefixe;
import fr.univ_amu.iut.commun.model.RegleMetierException;
import fr.univ_amu.iut.passage.model.DetailPassage;
import fr.univ_amu.iut.passage.model.ServicePassage;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;

/// ViewModel de la modale « Modifier le rattachement » (E2.S8) : corrige l'année ou le numéro de
/// passage d'un passage importé, sans changer de site/point.
///
/// Pur (aucun `javafx.scene` — règle ArchUnit `viewmodel_sans_javafx_ui`). Expose les deux champs
/// éditables ([#anneeProperty], [#numeroPassageProperty]), un **récapitulatif** réactif des
/// conséquences ([#recapProperty] : « X → Y, N séquences renommées ») et un message d'erreur. Le
/// carré et le code point (inchangés) sont fournis par la navigation : le `model`/`viewmodel` ne
/// dépend pas de `sites`. [#valider] délègue à [ServicePassage#modifierRattachement].
public class RattachementViewModel {

  private final ServicePassage service;
  private final IntegerProperty annee = new SimpleIntegerProperty(this, "annee");
  private final IntegerProperty numeroPassage = new SimpleIntegerProperty(this, "numeroPassage");
  private final ReadOnlyStringWrapper recap = new ReadOnlyStringWrapper(this, "recap", "");
  private final ReadOnlyStringWrapper messageErreur =
      new ReadOnlyStringWrapper(this, "messageErreur", "");

  private Long idPassage;
  private String carre;
  private String codePoint;
  private int anneeActuelle;
  private int numeroActuel;
  private int nombreSequences;

  public RattachementViewModel(ServicePassage service) {
    this.service = Objects.requireNonNull(service, "service");
    annee.addListener((observable, avant, apres) -> majRecap());
    numeroPassage.addListener((observable, avant, apres) -> majRecap());
  }

  /// Initialise la modale sur le passage `idPassage` (carré et code point fournis par la
  /// navigation). Pré-remplit les champs avec les valeurs courantes lues via le service.
  public void ouvrirSur(Long idPassage, String carre, String codePoint) {
    this.idPassage = Objects.requireNonNull(idPassage, "idPassage");
    this.carre = Objects.requireNonNull(carre, "carre");
    this.codePoint = Objects.requireNonNull(codePoint, "codePoint");
    DetailPassage detail = service.detailPassage(idPassage);
    anneeActuelle = detail.annee();
    numeroActuel = detail.numeroPassage();
    nombreSequences = detail.nombreSequences();
    messageErreur.set("");
    annee.set(detail.annee());
    numeroPassage.set(detail.numeroPassage());
    majRecap();
  }

  /// Applique le nouveau rattachement (année + n° saisis).
  ///
  /// @return `true` si l'opération a réussi (la vue peut fermer la modale) ; `false` si une règle
  ///     métier l'a refusée (motif dans [#messageErreurProperty], ex. R5 : quadruplet déjà pris)
  public boolean valider() {
    try {
      service.modifierRattachement(
          idPassage, new Prefixe(carre, annee.get(), numeroPassage.get(), codePoint));
      messageErreur.set("");
      return true;
    } catch (RegleMetierException refus) {
      messageErreur.set(refus.getMessage());
      return false;
    }
  }

  private void majRecap() {
    if (carre == null) {
      recap.set("");
      return;
    }
    String avant = new Prefixe(carre, anneeActuelle, numeroActuel, codePoint).nomDossierSession();
    String apres =
        new Prefixe(carre, annee.get(), numeroPassage.get(), codePoint).nomDossierSession();
    if (avant.equals(apres)) {
      recap.set("Aucun changement de rattachement.");
    } else {
      recap.set(
          "Rattachement : "
              + avant
              + " → "
              + apres
              + " — "
              + nombreSequences
              + " séquence(s) de la nuit seront renommées. Action irréversible.");
    }
  }

  public IntegerProperty anneeProperty() {
    return annee;
  }

  public IntegerProperty numeroPassageProperty() {
    return numeroPassage;
  }

  public ReadOnlyStringProperty recapProperty() {
    return recap.getReadOnlyProperty();
  }

  public ReadOnlyStringProperty messageErreurProperty() {
    return messageErreur.getReadOnlyProperty();
  }
}

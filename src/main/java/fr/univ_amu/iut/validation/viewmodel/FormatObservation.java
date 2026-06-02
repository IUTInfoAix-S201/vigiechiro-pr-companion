package fr.univ_amu.iut.validation.viewmodel;

import fr.univ_amu.iut.validation.model.Observation;
import fr.univ_amu.iut.validation.model.ObservationStatut;
import fr.univ_amu.iut.validation.model.StatutObservation;

/// Formatages d'affichage d'une observation Tadarida : détail multi-ligne, libellé de statut et
/// rendu des valeurs optionnelles (probabilité, taxon, fréquence). Source **unique** partagée entre
/// le [ValidationViewModel] (panneau de détail) et le controller de la vue (colonne « Statut »).
///
/// Classe utilitaire sans état : sortie la logique d'affichage du ViewModel pour qu'il garde une
/// seule responsabilité (orchestrer la revue), et pour éviter la duplication du libellé de statut.
public final class FormatObservation {

  /// Affichage des valeurs optionnelles absentes (probabilité, taxon observateur non saisi).
  private static final String NON_RENSEIGNE = "non renseigné";

  private FormatObservation() {}

  /// Détail multi-ligne d'une observation sélectionnée (proposition Tadarida, saisie observateur,
  /// fréquence médiane, statut de revue).
  public static String detail(ObservationStatut courant) {
    Observation o = courant.observation();
    return "Tadarida : "
        + o.taxonTadarida()
        + " ("
        + proba(o.probTadarida())
        + ")\nObservateur : "
        + valeurOuAbsente(o.taxonObservateur())
        + " ("
        + proba(o.probObservateur())
        + ")\nFréquence médiane : "
        + frequence(o.frequenceMedianeHz())
        + "\nStatut : "
        + libelleStatut(courant.statut());
  }

  /// Libellé d'affichage du statut de revue d'une observation.
  public static String libelleStatut(StatutObservation statut) {
    return switch (statut) {
      case NON_TOUCHEE -> "À revoir";
      case VALIDEE -> "Validée";
      case CORRIGEE -> "Corrigée";
    };
  }

  private static String proba(Double probabilite) {
    return probabilite == null ? NON_RENSEIGNE : Math.round(probabilite * 100) + " %";
  }

  private static String valeurOuAbsente(String code) {
    return code == null || code.isBlank() ? NON_RENSEIGNE : code;
  }

  private static String frequence(Integer hz) {
    return hz == null ? NON_RENSEIGNE : hz + " Hz";
  }
}

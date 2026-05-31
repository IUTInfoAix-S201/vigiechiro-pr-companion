package fr.univ_amu.iut.multisite.model;

import fr.univ_amu.iut.commun.model.Verdict;
import java.util.Comparator;

/// Critères de tri de la vue multi-sites (parcours P5, P5-CA2 : « triable par site, point, n°
/// passage, statut, verdict et date »). Chaque valeur expose un [Comparator] déterministe via
/// [#comparateur()].
///
/// Tous les comparateurs **brisent les égalités** avec le même ordre de référence (site,
/// point, année, n° de passage) pour garantir un résultat **stable** : deux exécutions sur les
/// mêmes données produisent exactement la même séquence de lignes (indispensable pour les exports
/// CSV « golden »). Le verdict, qui peut être `null` (passage non encore vérifié), est placé
/// en **fin** de tri.
public enum TriMultisite {

  /// Tri de lecture par défaut : site, puis point, puis année, puis n° de passage.
  PAR_SITE(null),

  /// Tri par année croissante, puis par l'ordre de référence.
  PAR_ANNEE(Comparator.comparingInt(LignePassage::annee)),

  /// Tri par statut de workflow (ordre de progression
  /// [StatutWorkflow][fr.univ_amu.iut.commun.model.StatutWorkflow]), puis référence.
  PAR_STATUT(Comparator.comparingInt((LignePassage ligne) -> ligne.statut().ordinal())),

  /// Tri par verdict ([Verdict], passages non vérifiés en dernier), puis référence.
  PAR_VERDICT(
      Comparator.comparing(
          LignePassage::verdict, Comparator.nullsLast(Comparator.comparingInt(Verdict::ordinal))));

  /// Ordre de référence stable, utilisé tel quel par [#PAR_SITE] et en départage ailleurs.
  private static final Comparator<LignePassage> REFERENCE =
      Comparator.comparing(LignePassage::numeroCarre)
          .thenComparing(LignePassage::codePoint)
          .thenComparingInt(LignePassage::annee)
          .thenComparingInt(LignePassage::numeroPassage);

  private final Comparator<LignePassage> criterePrincipal;

  TriMultisite(Comparator<LignePassage> criterePrincipal) {
    this.criterePrincipal = criterePrincipal;
  }

  /// Comparateur déterministe correspondant à ce critère de tri (critère principal puis ordre de
  /// référence). Construit à l'appel : [#REFERENCE] est alors initialisé (contrairement à
  /// l'instant de construction des constantes d'énum).
  public Comparator<LignePassage> comparateur() {
    return criterePrincipal == null ? REFERENCE : criterePrincipal.thenComparing(REFERENCE);
  }
}

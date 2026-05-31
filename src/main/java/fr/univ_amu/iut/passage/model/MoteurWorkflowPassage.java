package fr.univ_amu.iut.passage.model;

import fr.univ_amu.iut.commun.model.RegleMetierException;
import fr.univ_amu.iut.commun.model.StatutWorkflow;
import java.util.List;
import java.util.Optional;

/// Moteur (pur) des transitions de [StatutWorkflow] d'un [Passage].
///
/// Le workflow d'un passage est **linéaire** (C5) :
/// `Importé → Transformé → Vérifié → Prêt à déposer → Déposé`. Une seule transition est autorisée
/// depuis un statut donné : son **successeur immédiat**. On interdit ainsi de sauter une étape
/// (ex. importer puis déposer directement) ou de revenir en arrière (ex. re-transformer un passage
/// déjà déposé).
///
/// Cette logique est isolée dans une classe dédiée (et non dans [StatutWorkflow], qui vit en
/// `commun.model` et reste un simple énum de libellés) pour deux raisons :
///
/// - elle est **spécifique à la feature `passage`** (le sens de progression n'a de sens que pour
/// un passage) ;
/// - elle est **purement algorithmique**, donc testable en JUnit nu, sans base ni mock — d'où le
/// test `MoteurWorkflowPassageTest`.
///
/// Une transition interdite est une violation d'**invariant métier** (règle dure) : elle lève une
/// [RegleMetierException], par cohérence avec le patron du service de référence (cf.
/// SERVICE-CONVENTIONS §2.3).
public final class MoteurWorkflowPassage {

  /// Ordre canonique des statuts : l'index dans cette liste définit la progression.
  private static final List<StatutWorkflow> ORDRE =
      List.of(
          StatutWorkflow.IMPORTE,
          StatutWorkflow.TRANSFORME,
          StatutWorkflow.VERIFIE,
          StatutWorkflow.PRET_A_DEPOSER,
          StatutWorkflow.DEPOSE);

  /// Successeur immédiat d'un statut, ou [Optional#empty()] si `actuel` est le statut terminal
  /// ([StatutWorkflow#DEPOSE]).
  public Optional<StatutWorkflow> suivant(StatutWorkflow actuel) {
    int index = ORDRE.indexOf(actuel);
    if (index < 0 || index == ORDRE.size() - 1) {
      return Optional.empty();
    }
    return Optional.of(ORDRE.get(index + 1));
  }

  /// `true` si l'on peut passer de `actuel` à `cible`, c'est-à-dire si `cible` est exactement le
  /// successeur immédiat de `actuel`.
  public boolean estTransitionAutorisee(StatutWorkflow actuel, StatutWorkflow cible) {
    return suivant(actuel).map(attendu -> attendu == cible).orElse(false);
  }

  /// Exige que la transition `actuel → cible` soit autorisée.
  ///
  /// @throws RegleMetierException si la transition n'est pas le passage à l'étape suivante (saut
  /// d'étape, retour en arrière, ou statut déjà terminal)
  public void exigerTransitionAutorisee(StatutWorkflow actuel, StatutWorkflow cible) {
    if (!estTransitionAutorisee(actuel, cible)) {
      throw new RegleMetierException(
          "Transition de workflow interdite : « "
              + actuel.libelle()
              + " » → « "
              + cible.libelle()
              + " ». Seul le passage à l'étape suivante est autorisé"
              + suivant(actuel).map(s -> " (attendu : « " + s.libelle() + " »).").orElse("."));
    }
  }
}

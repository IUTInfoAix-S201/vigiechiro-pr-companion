package fr.univ_amu.iut.commun.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/// Implémentation de production de [Horloge] : délègue à l'horloge système.
///
/// C'est l'instance bindée par défaut dans `CommunModule`. Aucun service n'instancie cette
/// classe directement : il reçoit une [Horloge] par injection (constructeur).
public final class HorlogeSysteme implements Horloge {

  @Override
  public LocalDate aujourdhui() {
    return LocalDate.now();
  }

  @Override
  public LocalDateTime maintenant() {
    return LocalDateTime.now();
  }
}

package fr.univ_amu.iut.commun.persistence;

import java.sql.SQLException;

/**
 * Exception non vérifiée (unchecked) qui enveloppe une erreur de la couche de persistance.
 *
 * <p>L'API JDBC lève des {@link SQLException} <b>vérifiées</b> un peu partout. Les propager telles
 * quelles obligerait chaque appelant (ViewModel, service...) à les attraper, ce qui polluerait tout
 * le code au-dessus de la couche DAO. La convention de la couche {@code commun.persistence} est
 * donc de <b>traduire</b> ces exceptions techniques en une {@link RuntimeException} dédiée : le
 * code métier reste lisible et peut choisir de l'attraper uniquement là où c'est pertinent
 * (tolérance aux erreurs, cf. objectifs qualité 5.3).
 */
public class DataAccessException extends RuntimeException {

  public DataAccessException(String message, Throwable cause) {
    super(message, cause);
  }
}

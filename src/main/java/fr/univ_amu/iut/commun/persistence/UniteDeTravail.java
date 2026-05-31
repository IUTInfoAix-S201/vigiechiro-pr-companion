package fr.univ_amu.iut.commun.persistence;

import java.sql.Connection;
import java.sql.SQLException;

/// Exécute un bloc de travail dans une **transaction atomique** (begin / commit / rollback).
///
/// Par défaut, chaque appel DAO s'auto-commit. Quand plusieurs écritures doivent réussir ou
/// échouer ensemble (ex. créer un passage et sa session d'enregistrement), on les regroupe dans une
/// unité de travail : si le bloc lève une exception, **tout est annulé** (rollback) et la base
/// reste cohérente (objectif qualité intégrité / résilience O7).
///
/// ```
/// uniteDeTravail.executer(connexion -> {
///   // plusieurs écritures sur la même connexion...
/// }); // commit si tout s'est bien passé, rollback sinon
/// ```
public class UniteDeTravail {

  private final SourceDeDonnees source;

  public UniteDeTravail(SourceDeDonnees source) {
    this.source = source;
  }

  /// Ouvre une connexion, désactive l'auto-commit, exécute `travail`, puis valide (commit). En
  /// cas d'erreur, annule (rollback) et propage une [DataAccessException].
  public void executer(TravailTransactionnel travail) {
    try (Connection connexion = source.getConnection()) {
      boolean autoCommitInitial = connexion.getAutoCommit();
      connexion.setAutoCommit(false);
      try {
        travail.executer(connexion);
        connexion.commit();
      } catch (SQLException | RuntimeException erreur) {
        connexion.rollback();
        throw new DataAccessException("Transaction annulée (rollback)", erreur);
      } finally {
        connexion.setAutoCommit(autoCommitInitial);
      }
    } catch (SQLException e) {
      throw new DataAccessException("Échec d'ouverture de la transaction", e);
    }
  }

  /// Bloc de travail s'exécutant sur la connexion transactionnelle fournie.
  @FunctionalInterface
  public interface TravailTransactionnel {
    void executer(Connection connexion) throws SQLException;
  }
}

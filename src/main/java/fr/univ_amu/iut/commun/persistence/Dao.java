package fr.univ_amu.iut.commun.persistence;

import java.util.List;
import java.util.Optional;

/**
 * Contrat CRUD générique d'un DAO (Data Access Object), paramétré par le type d'entité {@code T} et
 * le type de sa clé primaire {@code ID}.
 *
 * <p>Chaque feature fournit ses DAO concrets dans son paquet {@code <feature>.model.dao}, en
 * étendant {@link DaoGenerique} (qui implémente déjà {@code findAll} / {@code findById} / {@code
 * delete} de façon générique). Seules les écritures dépendantes des colonnes ({@link #insert} /
 * {@link #update}) restent à écrire dans chaque DAO.
 *
 * @param <T> type de l'entité manipulée
 * @param <ID> type de la clé primaire (ex. {@code Long} pour un id auto-incrémenté, {@code String}
 *     pour une clé naturelle)
 */
public interface Dao<T, ID> {

  /** Toutes les entités, triées sur la colonne clé. */
  List<T> findAll();

  /** L'entité d'identifiant {@code id}, ou {@link Optional#empty()} si elle n'existe pas. */
  Optional<T> findById(ID id);

  /**
   * Insère une nouvelle entité et renvoie l'entité telle qu'elle est stockée (avec sa clé générée
   * si la table utilise un {@code INTEGER PRIMARY KEY AUTOINCREMENT}).
   */
  T insert(T entite);

  /** Met à jour une entité existante (identifiée par sa clé primaire). */
  void update(T entite);

  /** Supprime l'entité d'identifiant {@code id}. */
  void delete(ID id);
}

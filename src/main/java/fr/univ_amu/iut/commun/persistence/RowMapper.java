package fr.univ_amu.iut.commun.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Interface fonctionnelle qui transforme la <b>ligne courante</b> d'un {@link ResultSet} en une
 * entité du domaine.
 *
 * <p>Sans {@code RowMapper}, on réécrirait à chaque DAO la même boucle {@code while (rs.next())}.
 * Le {@code RowMapper} factorise cette répétition : on décrit une seule fois « comment lire une
 * ligne » et {@link DaoGenerique} s'occupe d'ouvrir la connexion, d'exécuter la requête et
 * d'itérer.
 *
 * @param <T> le type d'entité produit pour chaque ligne
 */
@FunctionalInterface
public interface RowMapper<T> {

  T mapper(ResultSet rs) throws SQLException;
}

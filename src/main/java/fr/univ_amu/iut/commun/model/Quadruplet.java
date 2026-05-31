package fr.univ_amu.iut.commun.model;

/**
 * Clé métier d'unicité d'un passage (R5) : un point ne peut pas avoir deux passages de même numéro
 * la même année.
 *
 * <p>Quadruplet {@code (carré, année, numéro de passage, code point)}. Le site est déduit du point
 * ; la contrainte est matérialisée en base par {@code UNIQUE(point_id, year, passage_number)}. Ce
 * value object sert à raisonner sur la clé dans le code métier (déduplication, recherche) sans
 * trimbaler des identifiants techniques.
 *
 * @param carre numéro de carré (6 chiffres, cf. {@link
 *     fr.univ_amu.iut.commun.model.validation.ValidateurCarre})
 * @param annee année à 4 chiffres (ex. 2026)
 * @param numeroPassage numéro de passage (typiquement 1 ou 2)
 * @param codePoint code du point d'écoute (lettre + chiffre, cf. {@link
 *     fr.univ_amu.iut.commun.model.validation.ValidateurCodePoint})
 */
public record Quadruplet(String carre, int annee, int numeroPassage, String codePoint) {}

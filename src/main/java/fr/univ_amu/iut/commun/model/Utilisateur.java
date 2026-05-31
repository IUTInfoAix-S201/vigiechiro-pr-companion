package fr.univ_amu.iut.commun.model;

/**
 * Unique utilisateur de l'application (C1, table {@code user}).
 *
 * <p>L'application est mono-utilisateur et hors-ligne : pas de compte ni de mot de passe. En
 * pratique, la table {@code user} ne contient qu'une seule ligne. L'entité est <b>transverse</b>
 * (cross-cutting) : elle vit dans {@code commun.model} car plusieurs features y font référence (un
 * site appartient à l'utilisateur courant, R2.01).
 *
 * @param localId identifiant local (UUID en {@code TEXT}), généré à l'installation, jamais affiché
 *     — clé primaire naturelle
 * @param displayName nom affiché (optionnel, repris dans la barre de titre)
 */
public record Utilisateur(String localId, String displayName) {}

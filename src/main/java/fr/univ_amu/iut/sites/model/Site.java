package fr.univ_amu.iut.sites.model;

import fr.univ_amu.iut.commun.model.Protocole;

/**
 * Site de suivi : carreau 2 km × 2 km du carroyage national Vigie-Chiro (C2, table {@code
 * monitoring_site}).
 *
 * <p>L'{@code id} (clé technique auto-incrémentée) vaut {@code null} tant que le site n'a pas été
 * inséré : {@link fr.univ_amu.iut.sites.model.dao.SiteDao#insert(Site)} renvoie une copie avec l'id
 * généré par SQLite.
 *
 * @param id clé technique, {@code null} avant insertion
 * @param numeroCarre numéro de carré (6 chiffres, R1) — unique par utilisateur
 * @param nomConvivial nom convivial (optionnel)
 * @param protocole protocole de suivi (R3/R4)
 * @param commentaire commentaire libre (optionnel)
 * @param dateCreation date de création (ISO {@code AAAA-MM-JJ})
 * @param idUtilisateur identifiant de l'utilisateur propriétaire (FK → {@code user.local_id})
 */
public record Site(
    Long id,
    String numeroCarre,
    String nomConvivial,
    Protocole protocole,
    String commentaire,
    String dateCreation,
    String idUtilisateur) {}

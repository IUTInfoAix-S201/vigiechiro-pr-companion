package fr.univ_amu.iut.multisite.model;

import fr.univ_amu.iut.commun.model.StatutWorkflow;
import fr.univ_amu.iut.commun.model.Verdict;

/**
 * Ligne d'une vue agrégée multi-sites (parcours P5, épopée E5) : un passage <b>aplati</b> avec ses
 * informations clés pour un tableau haute densité (écran M-MultiSite). Chaque ligne croise les
 * trois features dont la vue dépend : le site et le point ({@code sites.model}), le passage ({@code
 * passage.model}).
 *
 * <p>Ce n'est pas une entité persistée : c'est une <b>projection en lecture seule</b> construite
 * par {@link ServiceMultisite} à partir des DAO de {@code sites} et {@code passage}. Les champs
 * reprennent exactement les colonnes que le tableau affiche (P5-CA2 : « triable et filtrable par
 * site, point, n° passage, statut, verdict et date »).
 *
 * @param idPassage identifiant technique du passage (référence vers la fiche détaillée)
 * @param numeroCarre n° de carré du site d'appartenance (identifie le site, colonne « site »)
 * @param codePoint code du point d'écoute (lettre + chiffre, R2)
 * @param annee année du passage (4 chiffres)
 * @param numeroPassage n° de passage dans l'année (typiquement 1 ou 2, R3)
 * @param dateEnregistrement date d'enregistrement (ISO {@code AAAA-MM-JJ})
 * @param statut statut d'avancement dans le workflow d'import → dépôt
 * @param verdict verdict de vérification ({@code null} tant que le passage n'a pas été vérifié)
 */
public record LignePassage(
    Long idPassage,
    String numeroCarre,
    String codePoint,
    int annee,
    int numeroPassage,
    String dateEnregistrement,
    StatutWorkflow statut,
    Verdict verdict) {}

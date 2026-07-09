package fr.univ_amu.iut.commun.api;

/// Site de l'observateur sur **VigieChiro** (`GET /moi/sites`, #728). Vue minimale servant au
/// rapprochement : l'`id` MongoDB, le `titre` (unique par observateur, rapproché du numéro de carré /
/// nom convivial de nos sites locaux) et l'indicateur `verrouille` (un site verrouillé par le MNHN est
/// prérequis au dépôt d'une participation, cf. #142).
///
/// @param id identifiant VigieChiro (`_id`, 24 caractères hexadécimaux)
/// @param titre titre du site (`titre`), base du rapprochement local
/// @param verrouille `true` si le site est verrouillé côté plateforme (dépôt possible)
public record SiteVigieChiro(String id, String titre, boolean verrouille) {}

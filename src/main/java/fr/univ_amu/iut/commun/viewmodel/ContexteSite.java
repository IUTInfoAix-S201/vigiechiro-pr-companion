package fr.univ_amu.iut.commun.viewmodel;

/// Contexte d'identité d'un site/point transmis lors de la navigation vers l'écran M-Passage
/// (depuis M-Site-detail).
///
/// Vit dans le **socle** (`commun`) pour être partagé par la feature appelante (`sites`) et la
/// feature cible (`passage`) **sans** dépendance inter-feature vers leurs `view`/`viewmodel` (règle
/// ArchUnit `pas_de_dependance_inter_feature_vers_la_vue`). Permet d'afficher le carré et le code
/// du point sans jointure `passage → sites`.
///
/// @param numeroCarre numéro de carré Vigie-Chiro du site (ex. `640380`)
/// @param codePoint code du point d'écoute (ex. `A1`)
/// @param nomSite nom convivial du site, ou `null`
public record ContexteSite(String numeroCarre, String codePoint, String nomSite) {}

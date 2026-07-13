package fr.univ_amu.iut.passage.model;

/// Disponibilité de l'**audio local** d'un passage : toutes, une partie, ou aucune de ses séquences
/// d'écoute présentes sur disque (#1298, EPIC #1297).
///
/// C'est un état **observé** sur le système de fichiers, pas un statut du workflow local : il change
/// à tout moment (disque externe rebranché, sauvegarde restaurée, purge) et n'est pas monotone. Il
/// reste donc délibérément **distinct** de [fr.univ_amu.iut.commun.model.StatutWorkflow], dont
/// `DEPOSE` demeure l'état terminal (« ma part locale est faite ») : même arbitrage que
/// [fr.univ_amu.iut.commun.api.EtatTraitement], l'état observé côté serveur.
///
/// Se calcule via [ServiceDisponibiliteAudio], jamais à la main : le décompte sous-jacent
/// ([DecompteAudio]) est groupé et mis en cache.
public enum DisponibiliteAudio {

    /// Toutes les séquences d'écoute du passage sont sur disque : écoute intégrale possible.
    COMPLETE,

    /// Une partie seulement des séquences est sur disque. Cas réel à part entière (média
    /// partiellement copié, purge interrompue, restauration incomplète), pas un dégénéré de
    /// [#COMPLETE] ou [#ABSENTE] : l'IHM doit afficher le décompte, pas un simple oui/non.
    PARTIELLE,

    /// Aucune séquence sur disque : le passage est de fait **archivé** (EPIC #1297). Ses
    /// observations et vérifications restent consultables ; on ne peut simplement plus écouter,
    /// jusqu'à une réactivation par réimport (#1302). Un passage sans aucune séquence persistée
    /// (jamais importé localement) est aussi [#ABSENTE] : il n'y a rien à écouter.
    ABSENTE
}

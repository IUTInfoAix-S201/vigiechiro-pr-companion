package fr.univ_amu.iut.commun.view;

/// Contrat socle pour ouvrir la **modale de connexion VigieChiro** depuis le chrome (menu ☰), sans
/// que le socle dépende de la feature `connexion`. Même patron que [OuvrirSite] / [OuvrirPassage] :
/// l'implémentation ([fr.univ_amu.iut.connexion.view.NavigationConnexion]) est fournie par la feature
/// via `OptionalBinder` ; un défaut inerte ([OuvrirConnexionAucun]) couvre les injecteurs qui ne
/// chargent pas la feature (outils de capture, tests du socle).
public interface OuvrirConnexion {

    /// Ouvre la modale de connexion (non bloquante).
    void ouvrir();

    /// Libellé de l'entrée de menu, **reflétant l'état de connexion** (« Se connecter… » vs
    /// « Connecté : … »). Réévalué à chaque ouverture du menu, sans appel réseau.
    String libelleMenu();
}

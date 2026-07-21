package fr.univ_amu.iut.commun.view;

import fr.univ_amu.iut.commun.viewmodel.CompteRendu;
import fr.univ_amu.iut.commun.viewmodel.TexteCompteRendu;

/// Stratégie de **confirmation d'une action à conséquences** : quitter un écran à saisie non
/// enregistrée, écraser un passage, supprimer des archives, réimporter des résultats… Contrat **neutre**
/// du socle (#1013, généralise le `ConfirmateurQuitter` né avec le [Navigateur]) : les composants
/// demandent l'avis de l'utilisateur **sans dépendre d'une boîte de dialogue concrète** - l'application
/// branche [ConfirmationNavigation] (vrai `Alert`), les tests un stub déterministe (un dialogue natif
/// figerait TestFX headless). Voir [ConfirmateurModifiable] pour le porteur injectable partagé.
@FunctionalInterface
public interface Confirmateur {

    /// Demande confirmation. Renvoie `true` pour poursuivre l'action, `false` pour y renoncer.
    boolean confirmer(String message);

    /// Demande confirmation d'un **compte rendu structuré** : un intitulé et ses détails, chacun sur sa
    /// propre ligne alignée (#2060).
    ///
    /// Pourquoi cette surcharge : une liste à puces aplatie dans une chaîne unique perd son alignement dès
    /// qu'une ligne dépasse la largeur du dialogue - la continuation repart à la marge, au niveau de la
    /// puce suivante, et l'on ne distingue plus deux passages. C'est le défaut que #1987 avait refermé en
    /// remplaçant la chaîne par une **structure** ([VueCompteRendu]), et qu'on reproduisait ici faute d'un
    /// moyen de faire passer cette structure jusqu'à la modale.
    ///
    /// Par défaut, **repli textuel** ([TexteCompteRendu]) : suffisant pour les stubs de test et les
    /// lambdas, qui ne rendent rien. Seule l'implémentation qui **rend** un dialogue - [ConfirmationNavigation] -
    /// a intérêt à surcharger pour montrer la structure plutôt que sa mise à plat.
    default boolean confirmer(CompteRendu compteRendu) {
        return confirmer(TexteCompteRendu.rendre(compteRendu));
    }
}

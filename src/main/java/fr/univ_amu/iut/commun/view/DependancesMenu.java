package fr.univ_amu.iut.commun.view;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.persistence.ServicePurgeOriginaux;
import fr.univ_amu.iut.commun.persistence.ServiceSauvegarde;
import fr.univ_amu.iut.commun.viewmodel.ReglagesReactifs;

/// Dépendances du menu ☰ (outils) regroupées, injectées d'un bloc dans [MainController].
///
/// Double intérêt : matérialiser la **cohésion** « ce que le menu Outils manipule » (sauvegarde /
/// restauration de la base, purge des originaux, connexion VigieChiro, source des fiches espèces,
/// écran Réglages), et **garder compact** le constructeur de [MainController] (déjà au plafond de
/// paramètres) au lieu de l'allonger d'un cran à chaque nouvelle entrée de menu.
///
/// La [ReglagesReactifs] sert à lier l'item « source des fiches espèces » à la **même** Property que
/// l'onglet « Général » de l'écran Réglages (#928), pour une synchro live. Guice injecte le
/// **constructeur canonique** du record (annoté [Inject]).
public record DependancesMenu(
        ServiceSauvegarde sauvegarde,
        ServicePurgeOriginaux purge,
        OuvrirConnexion connexion,
        ReglagesReactifs reactifs,
        NavigationReglages reglages) {

    @Inject
    public DependancesMenu {}
}

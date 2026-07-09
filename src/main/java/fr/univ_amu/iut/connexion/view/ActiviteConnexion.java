package fr.univ_amu.iut.connexion.view;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.view.ActiviteAccueil;
import fr.univ_amu.iut.commun.view.Prisme;
import fr.univ_amu.iut.connexion.model.StockageConnexion;
import java.util.Objects;

/// Carte d'accueil « Connexion VigieChiro » (#727) : ouvre la modale de connexion. Sa description
/// reflète l'état courant (connecté / non) lu du stockage local, **sans appel réseau**.
///
/// Implémente le contrat du socle [ActiviteAccueil] et est enregistrée dans le
/// `Multibinder<ActiviteAccueil>` par [fr.univ_amu.iut.connexion.di.ConnexionModule] : le socle ne
/// dépend pas de la feature.
public final class ActiviteConnexion implements ActiviteAccueil {

    private final NavigationConnexion navigation;
    private final StockageConnexion stockage;

    @Inject
    public ActiviteConnexion(NavigationConnexion navigation, StockageConnexion stockage) {
        this.navigation = Objects.requireNonNull(navigation, "navigation");
        this.stockage = Objects.requireNonNull(stockage, "stockage");
    }

    @Override
    public Prisme prisme() {
        return Prisme.COLLECTE_PASSAGES;
    }

    @Override
    public int ordre() {
        return 90;
    }

    @Override
    public String iconeLiteral() {
        return "fas-plug";
    }

    @Override
    public String couleur() {
        return "#4b45d8";
    }

    @Override
    public String titre() {
        return "Connexion VigieChiro";
    }

    @Override
    public String description() {
        return stockage.profil()
                .map(profil -> "Connecté : " + (profil.pseudo() == null ? "?" : profil.pseudo()))
                .orElse("Reliez l'app à la plateforme en collant votre token.");
    }

    @Override
    public void ouvrir() {
        navigation.ouvrirModale();
    }
}

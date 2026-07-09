package fr.univ_amu.iut.connexion.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import fr.univ_amu.iut.commun.api.ClientVigieChiro;
import fr.univ_amu.iut.commun.api.FournisseurToken;
import fr.univ_amu.iut.commun.model.Horloge;
import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.commun.view.ActiviteAccueil;
import fr.univ_amu.iut.connexion.model.StockageConnexion;
import fr.univ_amu.iut.connexion.view.ActiviteConnexion;
import fr.univ_amu.iut.connexion.viewmodel.ConnexionViewModel;

/// Module Guice de la feature `connexion` (#727). Câble :
/// - le stockage local du token comme [FournisseurToken] du socle (consommé par [ClientVigieChiro]) ;
/// - le [ClientVigieChiro] (paquet `commun.api`) construit sur ce fournisseur de token ;
/// - la carte d'accueil « Connexion VigieChiro » via `Multibinder<ActiviteAccueil>` (le socle ne
///   dépend pas de la feature).
public class ConnexionModule extends AbstractModule {

    @Override
    protected void configure() {
        // Le stockage local EST la source du token pour tout le socle réseau (commun.api).
        bind(FournisseurToken.class).to(StockageConnexion.class);
        // Publie la carte d'accueil sans que `commun` dépende de la feature.
        Multibinder.newSetBinder(binder(), ActiviteAccueil.class).addBinding().to(ActiviteConnexion.class);
    }

    @Provides
    @Singleton
    StockageConnexion fournirStockage(Workspace workspace, Horloge horloge) {
        return new StockageConnexion(workspace, horloge);
    }

    /// Client de l'API VigieChiro, alimenté par le token stocké. Singleton : partagé par les futures
    /// features consommatrices (référentiel taxons, sites, dépôt…).
    @Provides
    @Singleton
    ClientVigieChiro fournirClient(FournisseurToken fournisseurToken) {
        return new ClientVigieChiro(fournisseurToken);
    }

    // ViewModel non-singleton : le FXMLLoader recrée le controller à chaque ouverture de la modale.
    @Provides
    ConnexionViewModel fournirViewModel(StockageConnexion stockage, ClientVigieChiro client) {
        return new ConnexionViewModel(stockage, client);
    }
}

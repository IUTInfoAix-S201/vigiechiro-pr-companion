package fr.univ_amu.iut.connexion.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.OptionalBinder;
import fr.univ_amu.iut.commun.api.ClientVigieChiro;
import fr.univ_amu.iut.commun.api.FournisseurToken;
import fr.univ_amu.iut.commun.api.RapprochementVigieChiro;
import fr.univ_amu.iut.commun.model.Horloge;
import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.commun.view.OuvrirConnexion;
import fr.univ_amu.iut.connexion.model.StockageConnexion;
import fr.univ_amu.iut.connexion.view.NavigationConnexion;
import fr.univ_amu.iut.connexion.viewmodel.ConnexionViewModel;
import java.util.Set;

/// Module Guice de la feature `connexion` (#727/#741). Câble :
/// - le stockage local du token comme [FournisseurToken] du socle (consommé par [ClientVigieChiro]) ;
/// - le [ClientVigieChiro] (paquet `commun.api`) construit sur ce fournisseur de token ;
/// - l'ouverture de la modale via le contrat socle [OuvrirConnexion] (menu ☰ du chrome), sans que
///   `commun` dépende de la feature.
public class ConnexionModule extends AbstractModule {

    @Override
    protected void configure() {
        // Le stockage local EST la source du token pour tout le socle réseau (commun.api).
        bind(FournisseurToken.class).to(StockageConnexion.class);
        // Fournit l'implémentation réelle du contrat socle : le menu ☰ ouvre la modale (#741). Prend le
        // pas sur le défaut inerte posé par CommunModule dès que la feature est chargée (app complète).
        OptionalBinder.newOptionalBinder(binder(), OuvrirConnexion.class)
                .setBinding()
                .to(NavigationConnexion.class);
        // Déclare le point d'extension de rapprochement (#728). Vide ici : les features taxons/sites y
        // contribuent leurs rapprocheurs. Déclaré même sans contributeur pour que `ConnexionViewModel`
        // reçoive un Set (éventuellement vide) quand seule `connexion` est chargée (outil de capture).
        Multibinder.newSetBinder(binder(), RapprochementVigieChiro.class);
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
    // Reçoit l'ensemble des rapprocheurs (#728) qu'il déclenche après une connexion réussie.
    @Provides
    ConnexionViewModel fournirViewModel(
            StockageConnexion stockage, ClientVigieChiro client, Set<RapprochementVigieChiro> rapprocheurs) {
        return new ConnexionViewModel(stockage, client, rapprocheurs);
    }
}

package fr.univ_amu.iut.multisite;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Guice;
import com.google.inject.Injector;
import fr.univ_amu.iut.commun.di.CommunModule;
import fr.univ_amu.iut.commun.di.PersistenceModule;
import fr.univ_amu.iut.commun.persistence.MigrationSchema;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.multisite.di.MultisiteModule;
import fr.univ_amu.iut.multisite.model.ServiceMultisite;
import fr.univ_amu.iut.multisite.model.dao.SavedViewDao;
import fr.univ_amu.iut.multisite.viewmodel.MultisiteViewModel;
import fr.univ_amu.iut.passage.di.PassageModule;
import fr.univ_amu.iut.sites.di.SitesModule;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/// Filet d'intégration Guice de la feature `multisite` : on assemble un injecteur dédié
/// (socle + `SitesModule` + `PassageModule` + `MultisiteModule`) et on vérifie que
/// [SavedViewDao], [ServiceMultisite] et [MultisiteViewModel] sont résolubles, sans conflit de
/// binding.
///
/// `SitesModule` et `PassageModule` sont requis car le service reçoit leurs DAO ([SiteDao],
/// [PointDao], [PassageDao]) et le ViewModel l'identité de l'utilisateur courant
/// (`@Named("idUtilisateurCourant")`, publiée par `SitesModule`) ; le socle (`CommunModule`)
/// fournit l'`Horloge`, le `Navigateur` et le `NavigationViewModel` dont dépend la carte d'accueil
/// [fr.univ_amu.iut.multisite.view.ActiviteMultisite] enregistrée par le module. On surcharge le
/// workspace vers un `@TempDir` pour ne pas toucher au workspace réel.
class MultisiteModuleTest {

    @TempDir
    Path workspaceJetable;

    @AfterEach
    void nettoyerLaSurcharge() {
        System.clearProperty("vigiechiro.workspace");
    }

    @Test
    @DisplayName("MultisiteModule assemble SavedViewDao, ServiceMultisite et MultisiteViewModel via Guice")
    void multisite_module_resout_dao_service_et_vm() {
        System.setProperty("vigiechiro.workspace", workspaceJetable.toString());

        Injector injecteur = Guice.createInjector(
                new CommunModule(),
                new PersistenceModule(),
                new SitesModule(),
                new PassageModule(),
                new MultisiteModule());
        // Le ViewModel tire l'identité de l'utilisateur courant, dont le provider lit la table `user` :
        // on migre le schéma avant de le résoudre.
        new MigrationSchema(injecteur.getInstance(SourceDeDonnees.class)).migrer();

        assertThat(injecteur.getInstance(SavedViewDao.class)).isNotNull();
        assertThat(injecteur.getInstance(ServiceMultisite.class)).isNotNull();
        assertThat(injecteur.getInstance(MultisiteViewModel.class)).isNotNull();
    }
}

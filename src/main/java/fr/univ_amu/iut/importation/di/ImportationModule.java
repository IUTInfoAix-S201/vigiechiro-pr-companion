package fr.univ_amu.iut.importation.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import fr.univ_amu.iut.commun.model.Horloge;
import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.commun.persistence.UniteDeTravail;
import fr.univ_amu.iut.commun.view.ActiviteAccueil;
import fr.univ_amu.iut.importation.model.AnalyseurLogPR;
import fr.univ_amu.iut.importation.model.CopieProtegee;
import fr.univ_amu.iut.importation.model.InspecteurDossier;
import fr.univ_amu.iut.importation.model.Renommeur;
import fr.univ_amu.iut.importation.model.ServiceImport;
import fr.univ_amu.iut.importation.model.TransformationAudio;
import fr.univ_amu.iut.importation.model.dao.AgregatImportDao;
import fr.univ_amu.iut.importation.view.ActiviteImporterNuit;
import fr.univ_amu.iut.importation.viewmodel.ImportationViewModel;
import fr.univ_amu.iut.sites.model.ServiceSites;

/// Module Guice de la feature `importation` : fournit les moteurs du parcours d'import P2
/// (inspection du journal, copie protégée, renommage, transformation audio), le DAO transactionnel
/// de l'agrégat et le service d'orchestration [ServiceImport].
///
/// Même patron que `SitesModule` / `QualificationModule` : des méthodes `@Provides @Singleton`
/// assemblent des classes métier restées **sans annotation d'injection** (couche `model`
/// indépendante du framework, objectif réutilisation O6).
///
/// L'assemblage de [ServiceImport] est **inter-modules** : il reçoit l'[UniteDeTravail], le
/// [Workspace] et l'[Horloge] du socle, plus les moteurs et l'[AgregatImportDao] de la feature. Le
/// DAO écrit dans des tables possédées par `passage`, mais la dépendance va
/// `importation → passage.model` (jamais l'inverse) : le graphe reste acyclique (contrôlé par
/// `ArchitectureTest`).
///
/// **Installé** dans `RacineInjecteur` (la racine de composition de l'application) :
/// [ServiceImport] est donc résoluble par l'injecteur applicatif. Le câblage en isolation reste
/// validé par `ImportationModuleTest` (injecteur local socle + passage + importation).
public class ImportationModule extends AbstractModule {

    /// Enregistre la carte d'accueil de la feature dans le point d'extension du socle. Le
    /// `MainController` la découvre via `Set<ActiviteAccueil>` sans que `commun` dépende de
    /// `importation`.
    @Override
    protected void configure() {
        Multibinder.newSetBinder(binder(), ActiviteAccueil.class).addBinding().to(ActiviteImporterNuit.class);
    }

    @Provides
    @Singleton
    AnalyseurLogPR fournirAnalyseurLogPR() {
        return new AnalyseurLogPR();
    }

    @Provides
    @Singleton
    InspecteurDossier fournirInspecteurDossier(AnalyseurLogPR analyseurLog) {
        return new InspecteurDossier(analyseurLog);
    }

    @Provides
    @Singleton
    CopieProtegee fournirCopieProtegee() {
        return new CopieProtegee();
    }

    @Provides
    @Singleton
    Renommeur fournirRenommeur() {
        return new Renommeur();
    }

    @Provides
    @Singleton
    TransformationAudio fournirTransformationAudio() {
        return new TransformationAudio();
    }

    @Provides
    @Singleton
    AgregatImportDao fournirAgregatImportDao(SourceDeDonnees source) {
        return new AgregatImportDao(source);
    }

    @Provides
    @Singleton
    ServiceImport fournirServiceImport(
            InspecteurDossier inspecteur,
            CopieProtegee copie,
            Renommeur renommeur,
            TransformationAudio transformation,
            AgregatImportDao agregatDao,
            UniteDeTravail uniteDeTravail,
            Workspace workspace,
            Horloge horloge) {
        return new ServiceImport(
                inspecteur, copie, renommeur, transformation, agregatDao, uniteDeTravail, workspace, horloge);
    }

    /// ViewModel de l'assistant M-Import. **Non-singleton** (un VM frais par chargement FXML : un
    /// écran rouvert ne réutilise pas l'état d'un précédent, cf. patron `SitesModule`). Dépend de
    /// [ServiceSites] et de l'utilisateur courant (fournis par `SitesModule`) pour lister les
    /// sites/points : dépendance `importation → sites` sur le `model` d'une autre feature.
    @Provides
    ImportationViewModel fournirImportationViewModel(
            ServiceImport serviceImport,
            ServiceSites serviceSites,
            Horloge horloge,
            @Named("idUtilisateurCourant") String idUtilisateur) {
        return new ImportationViewModel(serviceImport, serviceSites, horloge, idUtilisateur);
    }
}

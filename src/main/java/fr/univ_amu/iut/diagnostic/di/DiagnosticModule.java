package fr.univ_amu.iut.diagnostic.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import fr.univ_amu.iut.commun.model.Horloge;
import fr.univ_amu.iut.diagnostic.model.ServiceDiagnostic;
import fr.univ_amu.iut.passage.model.dao.JournalDuCapteurDao;
import fr.univ_amu.iut.passage.model.dao.PassageDao;
import fr.univ_amu.iut.passage.model.dao.ReleveClimatiqueDao;
import fr.univ_amu.iut.passage.model.dao.SessionDao;
import fr.univ_amu.iut.sites.model.dao.PointDao;

/**
 * Module Guice de la feature {@code diagnostic} : assemble {@link ServiceDiagnostic} à partir des
 * DAO publiés par les features {@code passage} ({@link PassageDao}, {@link SessionDao}, {@link
 * JournalDuCapteurDao}, {@link ReleveClimatiqueDao}) et {@code sites} ({@link PointDao}), plus
 * l'{@link Horloge} du socle. Mêmes conventions que {@code SitesModule}/{@code LotModule} : une
 * méthode {@code @Provides @Singleton} câble un objet resté <b>sans annotation d'injection</b>, les
 * DAO inter-features étant reçus en lecture seule (sens autorisé {@code diagnostic → passage} et
 * {@code diagnostic → sites}, graphe acyclique).
 *
 * <p><b>Intégration</b> : ce module n'est pas (encore) installé dans {@code RacineInjecteur}
 * (fichier gelé pour cette tâche). Son câblage est exercé en isolation par {@code
 * DiagnosticModuleTest} (injecteur local). Pour rendre {@code ServiceDiagnostic} résoluble par
 * l'injecteur applicatif, il faudra ajouter {@code new DiagnosticModule()} à {@code
 * RacineInjecteur.creer()}.
 */
public class DiagnosticModule extends AbstractModule {

  @Provides
  @Singleton
  ServiceDiagnostic fournirServiceDiagnostic(
      PassageDao passageDao,
      SessionDao sessionDao,
      JournalDuCapteurDao journalDao,
      ReleveClimatiqueDao releveDao,
      PointDao pointDao,
      Horloge horloge) {
    return new ServiceDiagnostic(passageDao, sessionDao, journalDao, releveDao, pointDao, horloge);
  }
}

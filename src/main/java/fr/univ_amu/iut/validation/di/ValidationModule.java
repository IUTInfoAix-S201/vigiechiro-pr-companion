package fr.univ_amu.iut.validation.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.validation.model.dao.GroupeTaxonomiqueDao;
import fr.univ_amu.iut.validation.model.dao.ObservationDao;
import fr.univ_amu.iut.validation.model.dao.ResultatsIdentificationDao;
import fr.univ_amu.iut.validation.model.dao.TaxonDao;

/**
 * Module Guice de la feature {@code validation} : fournit ses DAO à partir de la {@link
 * SourceDeDonnees} (binder en singleton par {@code CommunModule}).
 *
 * <p>Comme {@code SitesModule}, on utilise des méthodes {@code @Provides} (et non {@code @Inject}
 * sur les DAO) pour garder la couche {@code model.dao} indépendante du framework d'injection : les
 * DAO restent de simples objets réutilisables, c'est ce module qui sait les assembler.
 *
 * <p>Note d'intégration (phase 3) : ce module n'est <b>pas encore installé</b> dans {@code
 * RacineInjecteur}. Il le sera lorsque la feature validation sera câblée au runtime applicatif.
 */
public class ValidationModule extends AbstractModule {

  @Provides
  @Singleton
  GroupeTaxonomiqueDao fournirGroupeTaxonomiqueDao(SourceDeDonnees source) {
    return new GroupeTaxonomiqueDao(source);
  }

  @Provides
  @Singleton
  TaxonDao fournirTaxonDao(SourceDeDonnees source) {
    return new TaxonDao(source);
  }

  @Provides
  @Singleton
  ResultatsIdentificationDao fournirResultatsIdentificationDao(SourceDeDonnees source) {
    return new ResultatsIdentificationDao(source);
  }

  @Provides
  @Singleton
  ObservationDao fournirObservationDao(SourceDeDonnees source) {
    return new ObservationDao(source);
  }
}

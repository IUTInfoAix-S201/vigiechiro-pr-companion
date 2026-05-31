package fr.univ_amu.iut.sites.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.sites.model.dao.PointDao;
import fr.univ_amu.iut.sites.model.dao.SiteDao;

/**
 * Module Guice de la feature {@code sites} : fournit ses DAO à partir de la {@link SourceDeDonnees}
 * (binder en singleton par {@code CommunModule}).
 *
 * <p>On utilise des méthodes {@code @Provides} (et non {@code @Inject} sur les DAO) pour garder la
 * couche {@code model.dao} <b>indépendante du framework</b> d'injection : les DAO restent de
 * simples objets réutilisables (objectif réutilisation O6). C'est ce module qui sait les assembler.
 */
public class SitesModule extends AbstractModule {

  @Provides
  @Singleton
  SiteDao fournirSiteDao(SourceDeDonnees source) {
    return new SiteDao(source);
  }

  @Provides
  @Singleton
  PointDao fournirPointDao(SourceDeDonnees source) {
    return new PointDao(source);
  }
}

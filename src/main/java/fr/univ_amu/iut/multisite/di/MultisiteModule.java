package fr.univ_amu.iut.multisite.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import fr.univ_amu.iut.commun.model.Horloge;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.multisite.model.ServiceMultisite;
import fr.univ_amu.iut.multisite.model.dao.SavedViewDao;
import fr.univ_amu.iut.passage.model.dao.PassageDao;
import fr.univ_amu.iut.sites.model.dao.PointDao;
import fr.univ_amu.iut.sites.model.dao.SiteDao;

/**
 * Module Guice de la feature {@code multisite} : fournit ses DAO et son service à partir de la
 * {@link SourceDeDonnees} (binder en singleton par {@code CommunModule}).
 *
 * <p>On utilise des méthodes {@code @Provides} (et non {@code @Inject} sur les DAO/service) pour
 * garder les couches {@code model.dao} et {@code model} <b>indépendantes du framework</b>
 * d'injection : DAO et service restent de simples objets réutilisables (objectif réutilisation O6).
 * C'est ce module qui sait les assembler.
 */
public class MultisiteModule extends AbstractModule {

  @Provides
  @Singleton
  SavedViewDao fournirSavedViewDao(SourceDeDonnees source) {
    return new SavedViewDao(source);
  }

  /**
   * Vue agrégée multi-sites (parcours P5). Reçoit son propre {@link SavedViewDao} ainsi que les DAO
   * en lecture des features {@code sites} ({@link SiteDao}, {@link PointDao}) et {@code passage}
   * ({@link PassageDao}), fournis par leurs modules respectifs, plus l'{@link Horloge} du socle.
   * L'assemblage inter-modules est résolu par {@code RacineInjecteur}.
   */
  @Provides
  @Singleton
  ServiceMultisite fournirServiceMultisite(
      SavedViewDao savedViewDao,
      SiteDao siteDao,
      PointDao pointDao,
      PassageDao passageDao,
      Horloge horloge) {
    return new ServiceMultisite(savedViewDao, siteDao, pointDao, passageDao, horloge);
  }
}

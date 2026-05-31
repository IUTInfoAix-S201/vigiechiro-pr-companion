package fr.univ_amu.iut.multisite.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.multisite.model.dao.SavedViewDao;

/**
 * Module Guice de la feature {@code multisite} : fournit ses DAO à partir de la {@link
 * SourceDeDonnees} (binder en singleton par {@code CommunModule}).
 *
 * <p>On utilise des méthodes {@code @Provides} (et non {@code @Inject} sur les DAO) pour garder la
 * couche {@code model.dao} <b>indépendante du framework</b> d'injection : les DAO restent de
 * simples objets réutilisables (objectif réutilisation O6). C'est ce module qui sait les assembler.
 *
 * <p>Ce module n'est <b>pas encore installé</b> dans {@code RacineInjecteur} : son branchement dans
 * la racine de composition relève de la phase d'intégration.
 */
public class MultisiteModule extends AbstractModule {

  @Provides
  @Singleton
  SavedViewDao fournirSavedViewDao(SourceDeDonnees source) {
    return new SavedViewDao(source);
  }
}

package fr.univ_amu.iut.qualification.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.qualification.model.dao.SelectionDao;

/**
 * Module Guice de la feature {@code qualification} : fournit ses DAO à partir de la {@link
 * SourceDeDonnees} (binder en singleton par {@code CommunModule}).
 *
 * <p>Comme {@code SitesModule}, on utilise des méthodes {@code @Provides} (et non {@code @Inject}
 * sur les DAO) pour garder la couche {@code model.dao} indépendante du framework d'injection : les
 * DAO restent de simples objets réutilisables (objectif réutilisation O6).
 *
 * <p>Ce module n'est pas encore installé dans {@code RacineInjecteur} (phase d'intégration) :
 * l'enregistrement de la feature dans la racine de composition relève d'une phase ultérieure.
 */
public class QualificationModule extends AbstractModule {

  @Provides
  @Singleton
  SelectionDao fournirSelectionDao(SourceDeDonnees source) {
    return new SelectionDao(source);
  }
}

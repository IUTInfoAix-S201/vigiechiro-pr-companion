package fr.univ_amu.iut.bibliotheque.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import fr.univ_amu.iut.bibliotheque.model.ServiceBibliotheque;
import fr.univ_amu.iut.passage.model.dao.SequenceDao;
import fr.univ_amu.iut.validation.model.dao.ObservationDao;

/**
 * Module Guice de la feature {@code bibliotheque} : assemble {@link ServiceBibliotheque} à partir
 * des DAO publiés par les autres features ({@link ObservationDao} de {@code validation}, {@link
 * SequenceDao} de {@code passage}).
 *
 * <p>Même patron que {@code SitesModule} / {@code LotModule} : une méthode
 * {@code @Provides @Singleton} câble un service resté <b>sans annotation d'injection</b> (objet
 * Java ordinaire, instanciable à la main dans les tests). Les DAO inter-feature sont reçus en
 * lecture seule (sens autorisé {@code bibliotheque → validation} et {@code bibliotheque → passage},
 * graphe acyclique).
 *
 * <p><b>Intégration</b> : ce module n'est <b>pas (encore) installé</b> dans {@code RacineInjecteur}
 * (fichier gelé pour cette tâche). Son câblage est validé en isolation par {@code
 * BibliothequeModuleTest} (injecteur local fournissant les DAO feuilles). Pour le rendre résoluble
 * par l'injecteur applicatif, ajouter {@code new BibliothequeModule()} à {@code
 * RacineInjecteur.creer()} (et installer aussi {@code ValidationModule} et {@code PassageModule}
 * qui fournissent ses DAO — {@code PassageModule} et {@code ValidationModule} y sont déjà).
 */
public class BibliothequeModule extends AbstractModule {

  @Provides
  @Singleton
  ServiceBibliotheque fournirServiceBibliotheque(
      ObservationDao observationDao, SequenceDao sequenceDao) {
    return new ServiceBibliotheque(observationDao, sequenceDao);
  }
}

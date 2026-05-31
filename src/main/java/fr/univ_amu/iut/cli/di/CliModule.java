package fr.univ_amu.iut.cli.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import fr.univ_amu.iut.cli.model.RegistrePassages;
import fr.univ_amu.iut.passage.model.dao.PassageDao;
import fr.univ_amu.iut.sites.model.dao.PointDao;
import fr.univ_amu.iut.sites.model.dao.SiteDao;

/**
 * Module Guice de la feature transverse {@code cli}. Assemble les <b>aides de lecture</b> propres à
 * la ligne de commande ({@link RegistrePassages}) à partir des DAO publiés par les autres features
 * ({@code passage}, {@code sites}).
 *
 * <p>Même patron que {@code SitesModule} / {@code LotModule} : une méthode
 * {@code @Provides @Singleton} câble un objet resté <b>sans annotation d'injection</b> (couche
 * {@code model} indépendante du framework, donc instanciable à la main dans les tests).
 *
 * <p><b>Installation.</b> Ce module n'est <b>pas</b> ajouté à {@code RacineInjecteur} (fichier gelé
 * pour cette tâche). La {@code Cli} l'installe comme <b>injecteur enfant</b> de l'injecteur
 * applicatif complet : {@code RacineInjecteur.creer().createChildInjector(new CliModule())}.
 * L'enfant hérite ainsi de tous les bindings du socle et des features (dont {@code ServiceImport},
 * {@code ServiceLot}, {@code ServiceValidation}, que la CLI résout directement), et y ajoute les
 * aides propres à la CLI. La dépendance va {@code cli → <autre>.model.dao} (lecture des DAO),
 * jamais l'inverse : le graphe de features reste acyclique ({@code ArchitectureTest}).
 */
public class CliModule extends AbstractModule {

  @Provides
  @Singleton
  RegistrePassages fournirRegistrePassages(
      PassageDao passageDao, PointDao pointDao, SiteDao siteDao) {
    return new RegistrePassages(passageDao, pointDao, siteDao);
  }
}

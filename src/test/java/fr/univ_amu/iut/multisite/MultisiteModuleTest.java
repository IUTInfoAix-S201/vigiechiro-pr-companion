package fr.univ_amu.iut.multisite;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import fr.univ_amu.iut.commun.model.Horloge;
import fr.univ_amu.iut.commun.model.HorlogeFigee;
import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.commun.persistence.MigrationSchema;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.multisite.di.MultisiteModule;
import fr.univ_amu.iut.multisite.model.ServiceMultisite;
import fr.univ_amu.iut.multisite.model.dao.SavedViewDao;
import fr.univ_amu.iut.passage.model.dao.PassageDao;
import fr.univ_amu.iut.sites.model.dao.PointDao;
import fr.univ_amu.iut.sites.model.dao.SiteDao;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Filet de câblage Guice de la feature {@code multisite} : on exerce {@link MultisiteModule} au-
 * dessus d'un petit module local fournissant les feuilles dont {@link ServiceMultisite} dépend (la
 * {@link SourceDeDonnees}, les DAO des features {@code sites}/{@code passage} et l'{@link
 * Horloge}). On vérifie ainsi que les méthodes {@code @Provides} assemblent correctement le DAO et
 * le service (la racine de composition réelle est validée à part par {@code RacineInjecteurTest}).
 */
class MultisiteModuleTest {

  @TempDir Path workspaceJetable;

  @Test
  @DisplayName("MultisiteModule assemble SavedViewDao et ServiceMultisite via Guice")
  void multisite_module_resout_dao_et_service() {
    SourceDeDonnees source = new SourceDeDonnees(new Workspace(workspaceJetable));
    new MigrationSchema(source).migrer();

    Injector injecteur =
        Guice.createInjector(
            new MultisiteModule(),
            new AbstractModule() {
              @Override
              protected void configure() {
                bind(SourceDeDonnees.class).toInstance(source);
                bind(SiteDao.class).toInstance(new SiteDao(source));
                bind(PointDao.class).toInstance(new PointDao(source));
                bind(PassageDao.class).toInstance(new PassageDao(source));
                bind(Horloge.class).toInstance(new HorlogeFigee(LocalDate.of(2026, 5, 31)));
              }
            });

    assertThat(injecteur.getInstance(SavedViewDao.class)).isNotNull();
    assertThat(injecteur.getInstance(ServiceMultisite.class)).isNotNull();
  }
}

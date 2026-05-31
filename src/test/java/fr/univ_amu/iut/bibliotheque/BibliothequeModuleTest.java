package fr.univ_amu.iut.bibliotheque;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import fr.univ_amu.iut.bibliotheque.di.BibliothequeModule;
import fr.univ_amu.iut.bibliotheque.model.ServiceBibliotheque;
import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.commun.persistence.MigrationSchema;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.passage.model.dao.SequenceDao;
import fr.univ_amu.iut.validation.model.dao.ObservationDao;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Filet de câblage Guice de la feature {@code bibliotheque} : {@link BibliothequeModule} n'étant
 * pas (encore) installé dans {@code RacineInjecteur} (fichier gelé pour cette tâche), on l'exerce
 * ici au-dessus d'un module local fournissant les DAO feuilles dont il dépend ({@link
 * ObservationDao} de {@code validation}, {@link SequenceDao} de {@code passage}). On vérifie ainsi
 * que la méthode {@code @Provides} assemble correctement {@link ServiceBibliotheque}.
 */
class BibliothequeModuleTest {

  @TempDir Path workspaceJetable;

  @Test
  @DisplayName("BibliothequeModule assemble ServiceBibliotheque via Guice")
  void bibliotheque_module_resout_le_service() {
    SourceDeDonnees source = new SourceDeDonnees(new Workspace(workspaceJetable));
    new MigrationSchema(source).migrer();

    Injector injecteur =
        Guice.createInjector(
            new BibliothequeModule(),
            new AbstractModule() {
              @Override
              protected void configure() {
                bind(ObservationDao.class).toInstance(new ObservationDao(source));
                bind(SequenceDao.class).toInstance(new SequenceDao(source));
              }
            });

    assertThat(injecteur.getInstance(ServiceBibliotheque.class)).isNotNull();
  }
}

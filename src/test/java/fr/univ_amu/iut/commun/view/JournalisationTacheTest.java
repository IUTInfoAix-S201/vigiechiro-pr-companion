package fr.univ_amu.iut.commun.view;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.commun.model.OperationAnnuleeException;
import fr.univ_amu.iut.commun.model.RegleMetierException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// La journalisation des tâches de fond (#1523) distingue trois natures : un bug inattendu part en
/// SEVERE **avec sa trace** (la classe de bug qu'on ne voyait pas quand `getMessage()` était nul), tandis
/// qu'une annulation et un refus métier restent discrets (FINE, sans trace).
class JournalisationTacheTest {

    private final List<LogRecord> captures = new ArrayList<>();
    private Logger logger;
    private Handler sonde;
    private Level niveauInitial;
    private boolean parentInitial;

    @BeforeEach
    void brancherLaSonde() {
        logger = Logger.getLogger(JournalisationTache.class.getName());
        niveauInitial = logger.getLevel();
        parentInitial = logger.getUseParentHandlers();
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        sonde = new Handler() {
            @Override
            public void publish(LogRecord record) {
                captures.add(record);
            }

            @Override
            public void flush() {}

            @Override
            public void close() {}
        };
        sonde.setLevel(Level.ALL);
        logger.addHandler(sonde);
    }

    @AfterEach
    void debrancherLaSonde() {
        logger.removeHandler(sonde);
        logger.setLevel(niveauInitial);
        logger.setUseParentHandlers(parentInitial);
    }

    @Test
    @DisplayName("Un échec inattendu part en SEVERE, avec le Throwable (donc la trace)")
    void bug_inattendu_en_severe_avec_trace() {
        IllegalStateException bug = new IllegalStateException("état incohérent");

        JournalisationTache.consigner(bug);

        assertThat(captures).singleElement().satisfies(record -> {
            assertThat(record.getLevel()).isEqualTo(Level.SEVERE);
            assertThat(record.getThrown()).isSameAs(bug);
        });
    }

    @Test
    @DisplayName("Une annulation reste discrète (FINE, sans trace)")
    void annulation_discrete_en_fine() {
        JournalisationTache.consigner(new OperationAnnuleeException());

        assertThat(captures).singleElement().satisfies(record -> {
            assertThat(record.getLevel()).isEqualTo(Level.FINE);
            assertThat(record.getThrown()).isNull();
        });
    }

    @Test
    @DisplayName("Un refus métier reste discret (FINE, sans trace)")
    void refus_metier_discret_en_fine() {
        JournalisationTache.consigner(new RegleMetierException("point inconnu ici"));

        assertThat(captures).singleElement().satisfies(record -> {
            assertThat(record.getLevel()).isEqualTo(Level.FINE);
            assertThat(record.getThrown()).isNull();
        });
    }
}

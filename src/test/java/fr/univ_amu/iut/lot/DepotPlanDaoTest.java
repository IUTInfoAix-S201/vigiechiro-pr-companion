package fr.univ_amu.iut.lot;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.commun.model.StatutWorkflow;
import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.commun.persistence.MigrationSchema;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.fixture.JeuDeDonneesPassage;
import fr.univ_amu.iut.lot.model.DepotPlan;
import fr.univ_amu.iut.lot.model.dao.DepotPlanDao;
import fr.univ_amu.iut.passage.model.dao.PassageDao;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/// [DepotPlanDao] (#1993) : l'empreinte de la liste source, persistée au niveau du **passage**.
class DepotPlanDaoTest {

    @TempDir
    Path racine;

    private DepotPlanDao plans;
    private PassageDao passages;
    private Long idPassage;

    @BeforeEach
    void preparer() {
        SourceDeDonnees source = new SourceDeDonnees(new Workspace(racine.resolve("ws")));
        new MigrationSchema(source).migrer();
        idPassage = JeuDeDonneesPassage.dans(source)
                .statut(StatutWorkflow.PRET_A_DEPOSER)
                .semer()
                .idPassage();
        passages = new PassageDao(source);
        plans = new DepotPlanDao(source);
    }

    @Test
    @DisplayName("aucun plan tant qu'aucun dépôt n'a été entamé")
    void aucun_plan_au_depart() {
        assertThat(plans.parPassage(idPassage)).isEmpty();
    }

    @Test
    @DisplayName("le plan enregistré se relit avec son empreinte")
    void plan_enregistre_se_relit() {
        plans.enregistrer(new DepotPlan(idPassage, "abc123", "2026-07-19T10:00:00"));

        assertThat(plans.parPassage(idPassage)).get().satisfies(plan -> {
            assertThat(plan.empreinte()).isEqualTo("abc123");
            assertThat(plan.poseLe()).isEqualTo("2026-07-19T10:00:00");
        });
    }

    @Test
    @DisplayName("reposer un plan remplace l'empreinte au lieu d'échouer sur la clé (reprise idempotente)")
    void reposer_un_plan_est_idempotent() {
        plans.enregistrer(new DepotPlan(idPassage, "avant", "2026-07-19T10:00:00"));
        plans.enregistrer(new DepotPlan(idPassage, "apres", "2026-07-19T11:00:00"));

        assertThat(plans.parPassage(idPassage))
                .get()
                .extracting(DepotPlan::empreinte)
                .isEqualTo("apres");
    }

    @Test
    @DisplayName("supprimer le plan le fait oublier (réinitialisation du dépôt)")
    void supprimer_le_plan() {
        plans.enregistrer(new DepotPlan(idPassage, "abc123", "2026-07-19T10:00:00"));

        plans.supprimerPlan(idPassage);

        assertThat(plans.parPassage(idPassage)).isEmpty();
    }

    @Test
    @DisplayName("supprimer le passage emporte son plan (ON DELETE CASCADE)")
    void suppression_en_cascade() {
        plans.enregistrer(new DepotPlan(idPassage, "abc123", "2026-07-19T10:00:00"));

        passages.delete(idPassage);

        assertThat(plans.parPassage(idPassage)).isEmpty();
    }
}

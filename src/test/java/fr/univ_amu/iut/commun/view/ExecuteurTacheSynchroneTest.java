package fr.univ_amu.iut.commun.view;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.commun.model.JetonAnnulation;
import fr.univ_amu.iut.commun.model.Progression;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Exécution **synchrone** ([ExecuteurTacheSynchrone], #1014, défaut de test) : le travail et le
/// callback correspondant s'enchaînent sur le fil appelant ; exactement un des deux callbacks est
/// appelé, jamais les deux. Les extensions #1252 (annulation coopérative, progression) restent
/// synchrones et déterministes.
class ExecuteurTacheSynchroneTest {

    private final ExecuteurTache executeur = new ExecuteurTacheSynchrone();

    @Test
    @DisplayName("succès : le résultat du travail est remis à `succes`, `echec` n'est pas appelé")
    void succes_remis_au_callback() {
        List<String> succes = new ArrayList<>();
        List<Throwable> echecs = new ArrayList<>();

        executeur.executer(() -> "42", succes::add, echecs::add);

        assertThat(succes).containsExactly("42");
        assertThat(echecs).isEmpty();
    }

    @Test
    @DisplayName("échec : l'exception du travail est remise à `echec`, `succes` n'est pas appelé")
    void echec_remis_au_callback() {
        List<Object> succes = new ArrayList<>();
        List<Throwable> echecs = new ArrayList<>();
        RuntimeException panne = new IllegalStateException("base indisponible");

        executeur.executer(
                () -> {
                    throw panne;
                },
                succes::add,
                echecs::add);

        assertThat(succes).isEmpty();
        assertThat(echecs).containsExactly(panne);
    }

    @Test
    @DisplayName("#1252 : un jeton annulé avant le déclenchement conclut par `annule`, ni succès ni échec")
    void annulation_cooperative_conclut_par_annule() {
        List<Object> succes = new ArrayList<>();
        List<Throwable> echecs = new ArrayList<>();
        AtomicInteger annulations = new AtomicInteger();
        JetonAnnulation jeton = new JetonAnnulation();
        jeton.annuler(); // patron de test synchrone : annuler AVANT de déclencher

        executeur.executer(
                () -> {
                    jeton.leverSiAnnule(); // premier point de contrôle du travail
                    return "jamais atteint";
                },
                succes::add,
                annulations::incrementAndGet,
                echecs::add);

        assertThat(annulations.get()).isEqualTo(1);
        assertThat(succes).isEmpty();
        assertThat(echecs).as("une annulation n'est pas un échec").isEmpty();
    }

    @Test
    @DisplayName("#1252 : la surcharge annulable route toujours succès et échec ordinaires")
    void surcharge_annulable_route_succes_et_echec() {
        List<Object> succes = new ArrayList<>();
        List<Throwable> echecs = new ArrayList<>();
        AtomicInteger annulations = new AtomicInteger();
        RuntimeException panne = new IllegalStateException("disque plein");

        executeur.executer(() -> "42", succes::add, annulations::incrementAndGet, echecs::add);
        executeur.executer(
                () -> {
                    throw panne;
                },
                succes::add,
                annulations::incrementAndGet,
                echecs::add);

        assertThat(succes).containsExactly("42");
        assertThat(echecs).containsExactly(panne);
        assertThat(annulations.get()).isZero();
    }

    @Test
    @DisplayName("#1252 : la progression est appliquée immédiatement, dans l'ordre d'émission")
    void progression_appliquee_immediatement_dans_l_ordre() {
        List<Progression> points = new ArrayList<>();
        Consumer<Progression> relais = executeur.relaisProgression(points::add);

        executeur.executer(
                () -> {
                    relais.accept(new Progression("1/2", 0.5));
                    relais.accept(new Progression("2/2", 1.0));
                    return "fini";
                },
                resultat -> assertThat(points)
                        .as("points déjà appliqués quand le succès arrive")
                        .hasSize(2),
                erreur -> {});

        assertThat(points).extracting(Progression::libelle).containsExactly("1/2", "2/2");
    }
}

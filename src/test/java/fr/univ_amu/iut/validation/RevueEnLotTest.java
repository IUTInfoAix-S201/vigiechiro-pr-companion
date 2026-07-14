package fr.univ_amu.iut.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.univ_amu.iut.commun.model.RegleMetierException;
import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.commun.persistence.MigrationSchema;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.fixture.JeuDeDonneesPassage;
import fr.univ_amu.iut.validation.model.Observation;
import fr.univ_amu.iut.validation.model.RevueEnLot;
import fr.univ_amu.iut.validation.model.StatutObservation;
import fr.univ_amu.iut.validation.model.dao.ObservationDao;
import fr.univ_amu.iut.validation.model.dao.TaxonDao;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/// Actions de revue **en lot** (#479) : valider / corriger / marquer référence sur une liste d'ids, en une
/// transaction. Base SQLite jetable (taxons fil rouge semés par V02).
class RevueEnLotTest {

    @TempDir
    Path dossier;

    private ObservationDao observationDao;
    private RevueEnLot revueEnLot;
    private JeuDeDonneesPassage jeu;

    @BeforeEach
    void preparer() {
        SourceDeDonnees source = new SourceDeDonnees(new Workspace(dossier));
        new MigrationSchema(source).migrer();
        jeu = JeuDeDonneesPassage.dans(source).nuit(1, 2026, "2026-06-20").semer();
        jeu.ajouterResultats();

        observationDao = new ObservationDao(source);
        revueEnLot = new RevueEnLot(observationDao, new TaxonDao(source));
    }

    private long inserer(String taxonTadarida) {
        return jeu.ajouterObservation(taxonTadarida);
    }

    /// Statut dérivé (comme la projection) : non touché si pas de taxon observateur ; validé si égal au
    /// taxon Tadarida ; corrigé sinon.
    private StatutObservation statut(long id) {
        Observation o = observationDao.findById(id).orElseThrow();
        if (o.taxonObservateur() == null) {
            return StatutObservation.NON_TOUCHEE;
        }
        return o.taxonObservateur().equals(o.taxonTadarida()) ? StatutObservation.VALIDEE : StatutObservation.CORRIGEE;
    }

    @Test
    @DisplayName("validerLot valide exactement les ids visés (mode Activité, sans propagation) et compte")
    void valider_lot_traite_les_ids_vises() {
        long pippip = inserer("Pippip");
        long nyclei = inserer("Nyclei");
        long noise = inserer("noise");

        int traites = revueEnLot.valider(List.of(pippip, nyclei));

        assertThat(traites).isEqualTo(2);
        assertThat(statut(pippip)).isEqualTo(StatutObservation.VALIDEE);
        assertThat(statut(nyclei)).isEqualTo(StatutObservation.VALIDEE);
        // Le 3e n'est pas dans le lot : aucune propagation ne l'a touché.
        assertThat(statut(noise)).isEqualTo(StatutObservation.NON_TOUCHEE);
        assertThat(observationDao.findById(pippip).orElseThrow().taxonObservateur())
                .isEqualTo("Pippip");
    }

    @Test
    @DisplayName("marquerReferenceLot marque puis retire un lot")
    void marquer_reference_lot() {
        long a = inserer("Pippip");
        long b = inserer("Nyclei");

        assertThat(revueEnLot.marquerReference(List.of(a, b), true)).isEqualTo(2);
        assertThat(observationDao.findById(a).orElseThrow().reference()).isTrue();
        assertThat(observationDao.findById(b).orElseThrow().reference()).isTrue();

        revueEnLot.marquerReference(List.of(a), false);
        assertThat(observationDao.findById(a).orElseThrow().reference()).isFalse();
    }

    @Test
    @DisplayName("marquerDouteux marque puis retire le drapeau douteux d'un lot (#160)")
    void marquer_douteux_lot() {
        long a = inserer("Pippip");
        long b = inserer("Nyclei");

        assertThat(revueEnLot.marquerDouteux(List.of(a, b), true)).isEqualTo(2);
        assertThat(observationDao.findById(a).orElseThrow().douteux()).isTrue();
        assertThat(observationDao.findById(b).orElseThrow().douteux()).isTrue();

        revueEnLot.marquerDouteux(List.of(a), false);
        assertThat(observationDao.findById(a).orElseThrow().douteux()).isFalse();
    }

    @Test
    @DisplayName("corrigerLot retient un taxon sur tout le lot ; un taxon inconnu est refusé sans rien écrire")
    void corriger_lot_et_taxon_inconnu() {
        long a = inserer("noise");
        long b = inserer("noise");

        assertThat(revueEnLot.corriger(List.of(a, b), "Pippip")).isEqualTo(2);
        assertThat(observationDao.findById(a).orElseThrow().taxonObservateur()).isEqualTo("Pippip");
        assertThat(statut(b)).isEqualTo(StatutObservation.CORRIGEE);

        long c = inserer("noise");
        assertThatThrownBy(() -> revueEnLot.corriger(List.of(c), "ZZZZZZ")).isInstanceOf(RegleMetierException.class);
        assertThat(statut(c)).as("taxon inconnu refusé avant toute écriture").isEqualTo(StatutObservation.NON_TOUCHEE);
    }
}

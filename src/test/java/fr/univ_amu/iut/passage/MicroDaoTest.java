package fr.univ_amu.iut.passage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.commun.persistence.DataAccessException;
import fr.univ_amu.iut.commun.persistence.MigrationSchema;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.passage.model.Enregistreur;
import fr.univ_amu.iut.passage.model.Micro;
import fr.univ_amu.iut.passage.model.dao.EnregistreurDao;
import fr.univ_amu.iut.passage.model.dao.MicroDao;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * CRUD du {@link MicroDao} + contraintes : mapping booléen ({@code is_active}), colonnes nullables,
 * FK vers l'enregistreur et suppression en cascade quand l'enregistreur porteur est supprimé.
 */
class MicroDaoTest {

  private static final String SERIE = "1925492";

  @TempDir Path dossier;
  private MicroDao dao;
  private EnregistreurDao enregistreurDao;

  @BeforeEach
  void preparer() {
    SourceDeDonnees source = new SourceDeDonnees(new Workspace(dossier));
    new MigrationSchema(source).migrer();
    // L'enregistreur porteur doit exister (FK microphone.recorder_id).
    enregistreurDao = new EnregistreurDao(source);
    enregistreurDao.insert(new Enregistreur(SERIE, "V1.01", null));
    dao = new MicroDao(source);
  }

  private Micro micro(String modele, boolean actif) {
    return new Micro(
        null, modele, "8-150 kHz", "-42 dBV/Pa", "2026-05-01", null, actif, null, SERIE);
  }

  @Test
  @DisplayName("insert attribue un id et rend le micro relisible (booléen actif compris)")
  void inserer_attribue_un_id_et_rend_le_micro_relisible() {
    Micro insere = dao.insert(micro("Knowles FG-23329", true));

    assertThat(insere.id()).isNotNull();
    Micro relu = dao.findById(insere.id()).orElseThrow();
    assertThat(relu.modeleReference()).isEqualTo("Knowles FG-23329");
    assertThat(relu.actif()).isTrue();
    assertThat(relu.bandePassante()).isEqualTo("8-150 kHz");
  }

  @Test
  @DisplayName("un micro retiré (actif=false, date de retrait) est persisté tel quel")
  void micro_retire_est_persiste() {
    Micro retire =
        new Micro(
            null, "SPU0410LR5H-QB", null, null, "2025-01-01", "2026-04-30", false, null, SERIE);

    Micro relu = dao.findById(dao.insert(retire).id()).orElseThrow();

    assertThat(relu.actif()).isFalse();
    assertThat(relu.retireLe()).isEqualTo("2026-04-30");
    assertThat(relu.bandePassante()).isNull();
    assertThat(relu.sensibilite()).isNull();
  }

  @Test
  @DisplayName("on retrouve le micro actif d'un enregistreur parmi son historique")
  void trouver_le_micro_actif_par_enregistreur() {
    dao.insert(new Micro(null, "ancien", null, null, null, "2026-04-30", false, null, SERIE));
    dao.insert(micro("courant", true));

    assertThat(dao.findByEnregistreur(SERIE)).hasSize(2);
    assertThat(dao.trouverActifParEnregistreur(SERIE).orElseThrow().modeleReference())
        .isEqualTo("courant");
  }

  @Test
  @DisplayName("FK active : un enregistreur inconnu est rejeté")
  void clef_etrangere_active_un_enregistreur_inconnu_est_rejete() {
    Micro orphelin = new Micro(null, "micro", null, null, null, null, true, null, "inconnu");

    assertThatThrownBy(() -> dao.insert(orphelin))
        .as("PRAGMA foreign_keys=ON doit refuser une FK vers un enregistreur absent")
        .isInstanceOf(DataAccessException.class);
  }

  @Test
  @DisplayName("supprimer l'enregistreur supprime ses micros en cascade")
  void supprimer_l_enregistreur_supprime_ses_micros_en_cascade() {
    dao.insert(micro("m1", false));
    dao.insert(micro("m2", true));
    assertThat(dao.findByEnregistreur(SERIE)).hasSize(2);

    enregistreurDao.delete(SERIE);

    assertThat(dao.findByEnregistreur(SERIE))
        .as("ON DELETE CASCADE doit avoir supprimé les micros de l'enregistreur")
        .isEmpty();
  }
}

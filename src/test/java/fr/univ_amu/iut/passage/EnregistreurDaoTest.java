package fr.univ_amu.iut.passage;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.commun.persistence.MigrationSchema;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.passage.model.Enregistreur;
import fr.univ_amu.iut.passage.model.dao.EnregistreurDao;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/// CRUD du [EnregistreurDao] sur une base SQLite jetable (@TempDir). Cas central : la **clé
/// naturelle** (`serial_number`) et le comportement d'**upsert** de `insert`.
class EnregistreurDaoTest {

  @TempDir Path dossier;
  private EnregistreurDao dao;

  @BeforeEach
  void preparer() {
    SourceDeDonnees source = new SourceDeDonnees(new Workspace(dossier));
    new MigrationSchema(source).migrer();
    // recorder n'a aucune FK parente : aucun pré-requis à insérer.
    dao = new EnregistreurDao(source);
  }

  @Test
  @DisplayName("insert d'un enregistreur le rend relisible par sa clé naturelle")
  void inserer_rend_l_enregistreur_relisible() {
    Enregistreur insere = dao.insert(new Enregistreur("1925492", "V1.01, T4.1", "neuf"));

    assertThat(insere.numeroSerie()).isEqualTo("1925492");
    Enregistreur relu = dao.findById("1925492").orElseThrow();
    assertThat(relu.versionModele()).isEqualTo("V1.01, T4.1");
    assertThat(relu.commentaire()).isEqualTo("neuf");
  }

  @Test
  @DisplayName("insert sur une clé existante fait un upsert (met à jour, ne plante pas)")
  void inserer_deux_fois_la_meme_serie_fait_un_upsert() {
    dao.insert(new Enregistreur("1925492", "V1.00", "premier import"));

    // Même n° de série rencontré sur un nouveau passage : on rafraîchit ses métadonnées.
    dao.insert(new Enregistreur("1925492", "V1.01, T4.1", "firmware mis à jour"));

    assertThat(dao.findAll()).as("un seul enregistreur, pas de doublon de clé").hasSize(1);
    Enregistreur relu = dao.findById("1925492").orElseThrow();
    assertThat(relu.versionModele()).isEqualTo("V1.01, T4.1");
    assertThat(relu.commentaire()).isEqualTo("firmware mis à jour");
  }

  @Test
  @DisplayName("update modifie les champs d'un enregistreur existant")
  void mettre_a_jour_modifie_les_champs() {
    dao.insert(new Enregistreur("1925492", "V1.00", null));

    dao.update(new Enregistreur("1925492", "V2.00", "carte SD remplacée"));

    Enregistreur relu = dao.findById("1925492").orElseThrow();
    assertThat(relu.versionModele()).isEqualTo("V2.00");
    assertThat(relu.commentaire()).isEqualTo("carte SD remplacée");
  }

  @Test
  @DisplayName("delete retire l'enregistreur")
  void supprimer_retire_l_enregistreur() {
    dao.insert(new Enregistreur("1925492", null, null));
    assertThat(dao.findById("1925492")).isPresent();

    dao.delete("1925492");

    assertThat(dao.findById("1925492")).isEmpty();
  }
}

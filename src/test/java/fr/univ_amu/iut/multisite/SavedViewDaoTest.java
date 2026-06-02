package fr.univ_amu.iut.multisite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.commun.persistence.DataAccessException;
import fr.univ_amu.iut.commun.persistence.MigrationSchema;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.multisite.model.SavedView;
import fr.univ_amu.iut.multisite.model.dao.SavedViewDao;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/// CRUD du [SavedViewDao] sur une base SQLite jetable (@TempDir), initialisée par
/// [MigrationSchema]. La table `saved_view` est autonome (aucune clé étrangère) : on vérifie le
/// CRUD relisible, la requête métier `findByNom`, et les contraintes `NOT NULL` sur le
/// nom et sur le JSON des filtres.
class SavedViewDaoTest {

    @TempDir
    Path dossier;

    private SavedViewDao dao;

    @BeforeEach
    void preparer() {
        SourceDeDonnees source = new SourceDeDonnees(new Workspace(dossier));
        new MigrationSchema(source).migrer();
        dao = new SavedViewDao(source);
    }

    private SavedView nouvelleVue(String nom) {
        return new SavedView(null, nom, "{\"statut\":\"À vérifier\",\"sites\":[640380,640381]}");
    }

    @Test
    @DisplayName("Insérer attribue un id et rend la vue relisible (filtres JSON inclus)")
    void inserer_attribue_un_id_et_rend_la_vue_relisible() {
        SavedView insere = dao.insert(nouvelleVue("Mes nuits à vérifier"));

        assertThat(insere.id()).as("la clé auto-incrémentée est renseignée").isNotNull();
        SavedView relue = dao.findById(insere.id()).orElseThrow();
        assertThat(relue.nom()).isEqualTo("Mes nuits à vérifier");
        assertThat(relue.filtresJson())
                .as("le JSON des filtres est persisté tel quel")
                .isEqualTo("{\"statut\":\"À vérifier\",\"sites\":[640380,640381]}");
    }

    @Test
    @DisplayName("Mettre à jour modifie le nom et les filtres")
    void mettre_a_jour_modifie_les_champs() {
        SavedView insere = dao.insert(nouvelleVue("Brouillon"));

        dao.update(new SavedView(insere.id(), "Vue définitive", "{\"verdict\":\"Validé\"}"));

        SavedView relue = dao.findById(insere.id()).orElseThrow();
        assertThat(relue.nom()).isEqualTo("Vue définitive");
        assertThat(relue.filtresJson()).isEqualTo("{\"verdict\":\"Validé\"}");
    }

    @Test
    @DisplayName("Supprimer retire la vue")
    void supprimer_retire_la_vue() {
        SavedView insere = dao.insert(nouvelleVue("À supprimer"));
        assertThat(dao.findById(insere.id())).isPresent();

        dao.delete(insere.id());

        assertThat(dao.findById(insere.id())).isEmpty();
    }

    @Test
    @DisplayName("findByNom remonte toutes les vues enregistrées sous un nom donné")
    void rechercher_par_nom_remonte_les_vues_correspondantes() {
        dao.insert(nouvelleVue("Saison 2026"));
        dao.insert(nouvelleVue("Saison 2026"));
        dao.insert(nouvelleVue("Autre vue"));

        assertThat(dao.findByNom("Saison 2026")).hasSize(2);
        assertThat(dao.findByNom("Autre vue")).extracting(SavedView::nom).containsExactly("Autre vue");
        assertThat(dao.findByNom("Inconnue")).isEmpty();
    }

    @Test
    @DisplayName("findAll restitue toutes les vues enregistrées")
    void lister_restitue_toutes_les_vues() {
        dao.insert(nouvelleVue("Vue A"));
        dao.insert(nouvelleVue("Vue B"));

        assertThat(dao.findAll()).extracting(SavedView::nom).containsExactlyInAnyOrder("Vue A", "Vue B");
    }

    @Test
    @DisplayName("Le nom est obligatoire (contrainte NOT NULL sur name)")
    void nom_obligatoire_est_refuse() {
        SavedView sansNom = new SavedView(null, null, "{}");

        assertThatThrownBy(() -> dao.insert(sansNom))
                .as("name NOT NULL doit refuser une vue sans nom")
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    @DisplayName("Le JSON des filtres est obligatoire (contrainte NOT NULL sur filters_json)")
    void filtres_json_obligatoire_est_refuse() {
        SavedView sansFiltres = new SavedView(null, "Vue vide", null);

        assertThatThrownBy(() -> dao.insert(sansFiltres))
                .as("filters_json NOT NULL doit refuser une vue sans critères")
                .isInstanceOf(DataAccessException.class);
    }
}

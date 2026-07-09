package fr.univ_amu.iut.commun.model.dao;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.commun.model.LienVigieChiro;
import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.commun.persistence.MigrationSchema;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/// CRUD du [LienVigieChiroDao] sur une base SQLite jetable (@TempDir), initialisée par [MigrationSchema]
/// (table `vigiechiro_link`, migration V15). On vérifie la lecture d'une correspondance absente,
/// l'upsert unitaire idempotent, la **portée par entité** (même `ref_locale` sous deux entités = deux
/// correspondances), et la **resynchronisation** transactionnelle [LienVigieChiroDao#remplacer] (purge +
/// réinsertion, sans toucher aux autres entités).
class LienVigieChiroDaoTest {

    @TempDir
    Path dossier;

    private LienVigieChiroDao dao;

    @BeforeEach
    void preparer() {
        SourceDeDonnees source = new SourceDeDonnees(new Workspace(dossier));
        new MigrationSchema(source).migrer();
        dao = new LienVigieChiroDao(source);
    }

    @Test
    @DisplayName("objectidPour une correspondance jamais posée renvoie vide")
    void objectid_absent_renvoie_vide() {
        assertThat(dao.objectidPour(LienVigieChiro.ENTITE_TAXON, "Pippip")).isEmpty();
    }

    @Test
    @DisplayName("upsert puis objectidPour restitue l'objectid")
    void upsert_puis_lire() {
        dao.upsert(new LienVigieChiro(LienVigieChiro.ENTITE_TAXON, "Pippip", "5a1"));

        assertThat(dao.objectidPour(LienVigieChiro.ENTITE_TAXON, "Pippip")).contains("5a1");
    }

    @Test
    @DisplayName("upsert deux fois la même clé remplace l'objectid (idempotent, pas de doublon)")
    void upsert_est_idempotent() {
        dao.upsert(new LienVigieChiro(LienVigieChiro.ENTITE_TAXON, "Pippip", "ancien"));
        dao.upsert(new LienVigieChiro(LienVigieChiro.ENTITE_TAXON, "Pippip", "nouveau"));

        assertThat(dao.objectidPour(LienVigieChiro.ENTITE_TAXON, "Pippip")).contains("nouveau");
        assertThat(dao.compter(LienVigieChiro.ENTITE_TAXON)).isEqualTo(1);
    }

    @Test
    @DisplayName("l'entité discrimine : même ref_locale sous taxon et site = deux correspondances")
    void ref_locale_scoping_par_entite() {
        dao.upsert(new LienVigieChiro(LienVigieChiro.ENTITE_TAXON, "42", "taxon-42"));
        dao.upsert(new LienVigieChiro(LienVigieChiro.ENTITE_SITE, "42", "site-42"));

        assertThat(dao.objectidPour(LienVigieChiro.ENTITE_TAXON, "42")).contains("taxon-42");
        assertThat(dao.objectidPour(LienVigieChiro.ENTITE_SITE, "42")).contains("site-42");
    }

    @Test
    @DisplayName("tous(entite) renvoie la table ref_locale -> objectid de cette entité seulement")
    void tous_par_entite() {
        dao.upsert(new LienVigieChiro(LienVigieChiro.ENTITE_TAXON, "Pippip", "5a1"));
        dao.upsert(new LienVigieChiro(LienVigieChiro.ENTITE_TAXON, "Barbar", "5a2"));
        dao.upsert(new LienVigieChiro(LienVigieChiro.ENTITE_SITE, "7", "site-7"));

        assertThat(dao.tous(LienVigieChiro.ENTITE_TAXON)).containsOnly(entry("Pippip", "5a1"), entry("Barbar", "5a2"));
    }

    @Test
    @DisplayName("remplacer purge puis réinsère l'entité, sans toucher les autres, et reste idempotent")
    void remplacer_resynchronise() {
        dao.upsert(new LienVigieChiro(LienVigieChiro.ENTITE_TAXON, "Obsolete", "vieux"));
        dao.upsert(new LienVigieChiro(LienVigieChiro.ENTITE_SITE, "9", "site-9"));

        Map<String, String> frais = new LinkedHashMap<>();
        frais.put("Pippip", "5a1");
        frais.put("Barbar", "5a2");
        dao.remplacer(LienVigieChiro.ENTITE_TAXON, frais);

        // La correspondance obsolète a disparu ; les deux fraîches sont là ; l'entité site est intacte.
        assertThat(dao.objectidPour(LienVigieChiro.ENTITE_TAXON, "Obsolete")).isEmpty();
        assertThat(dao.tous(LienVigieChiro.ENTITE_TAXON)).containsOnly(entry("Pippip", "5a1"), entry("Barbar", "5a2"));
        assertThat(dao.objectidPour(LienVigieChiro.ENTITE_SITE, "9")).contains("site-9");

        // Rejouer la même resynchro ne change rien (idempotent).
        dao.remplacer(LienVigieChiro.ENTITE_TAXON, frais);
        assertThat(dao.compter(LienVigieChiro.ENTITE_TAXON)).isEqualTo(2);
    }

    private static Map.Entry<String, String> entry(String cle, String valeur) {
        return Map.entry(cle, valeur);
    }
}

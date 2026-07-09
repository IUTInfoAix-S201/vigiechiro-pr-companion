package fr.univ_amu.iut.validation.model.dao;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.commun.persistence.MigrationSchema;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.validation.model.Taxon;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/// Fusion **conservatrice** du référentiel officiel VigieChiro (#717, axe 2) par
/// [TaxonDao#fusionnerReferentielOfficiel], sur une base migrée jetable (seed V05 présent, groupe
/// « Référentiel VigieChiro » de V16). On vérifie les trois comportements : ajout d'un taxon officiel
/// absent, complétion du nom latin d'une souche (latin NULL), et **préservation** d'un taxon curé.
class TaxonDaoTest {

    @TempDir
    Path dossier;

    private TaxonDao taxonDao;
    private GroupeTaxonomiqueDao groupeDao;

    @BeforeEach
    void preparer() {
        SourceDeDonnees source = new SourceDeDonnees(new Workspace(dossier));
        new MigrationSchema(source).migrer();
        taxonDao = new TaxonDao(source);
        groupeDao = new GroupeTaxonomiqueDao(source);
    }

    @Test
    @DisplayName("taxon officiel absent : inséré (nom latin, vernaculaire NULL, groupe « Référentiel VigieChiro »)")
    void insere_un_taxon_officiel_absent() {
        assertThat(taxonDao.findById("Zzztst")).isEmpty();

        taxonDao.fusionnerReferentielOfficiel(Map.of("Zzztst", "Latinus testus"));

        Taxon ajoute = taxonDao.findById("Zzztst").orElseThrow();
        assertThat(ajoute.nomLatin()).isEqualTo("Latinus testus");
        assertThat(ajoute.nomVernaculaireFr()).isNull();
        assertThat(groupeDao.findById(ajoute.idGroupe()).orElseThrow().nom()).isEqualTo("Référentiel VigieChiro");
    }

    @Test
    @DisplayName("souche existante sans nom latin : le nom latin officiel est complété")
    void complete_le_nom_latin_manquant() {
        long groupe = groupeDao.findAll().getFirst().id();
        taxonDao.insert(new Taxon("Souche", null, null, groupe));

        taxonDao.fusionnerReferentielOfficiel(Map.of("Souche", "Souchus latinus"));

        assertThat(taxonDao.findById("Souche").orElseThrow().nomLatin()).isEqualTo("Souchus latinus");
    }

    @Test
    @DisplayName("taxon curé (nom latin + vernaculaire) : jamais écrasé par l'officiel")
    void preserve_un_taxon_cure() {
        Taxon cure = taxonDao.findAll().stream()
                .filter(taxon -> taxon.nomLatin() != null && taxon.nomVernaculaireFr() != null)
                .findFirst()
                .orElseThrow();

        taxonDao.fusionnerReferentielOfficiel(Map.of(cure.code(), "Faux latin écrasant"));

        Taxon apres = taxonDao.findById(cure.code()).orElseThrow();
        assertThat(apres.nomLatin()).isEqualTo(cure.nomLatin());
        assertThat(apres.nomVernaculaireFr()).isEqualTo(cure.nomVernaculaireFr());
    }
}

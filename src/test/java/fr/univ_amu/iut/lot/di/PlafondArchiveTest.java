package fr.univ_amu.iut.lot.di;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.commun.model.Reglages;
import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.commun.model.dao.ReglagesDao;
import fr.univ_amu.iut.commun.persistence.MigrationSchema;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.lot.viewmodel.OngletReglagesDepot;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/// Plafond des archives de dépôt (#1047) : priorité propriété système > réglage persisté > défaut
/// 700 Mo (contrainte plateforme), sur des réglages **réels** (SQLite jetable).
class PlafondArchiveTest {

    @TempDir
    Path dossier;

    private Reglages reglages;

    @BeforeEach
    void preparer() {
        SourceDeDonnees source = new SourceDeDonnees(new Workspace(dossier));
        new MigrationSchema(source).migrer();
        reglages = new Reglages(new ReglagesDao(source));
    }

    @AfterEach
    void nettoyerPropriete() {
        System.clearProperty("vigiechiro.depot.taille-max-mo");
    }

    @Test
    @DisplayName("sans réglage ni propriété : 700 Mo (contrainte plateforme, base 1000)")
    void defaut_700_mo() {
        assertThat(LotModule.plafondArchiveOctets(reglages)).isEqualTo(700_000_000L);
    }

    @Test
    @DisplayName("réglage persisté (écran Réglages) : appliqué en base 1000")
    void reglage_persiste_applique() {
        reglages.ecrireEntier(OngletReglagesDepot.CLE_TAILLE_MAX, 300);

        assertThat(LotModule.plafondArchiveOctets(reglages)).isEqualTo(300_000_000L);
    }

    @Test
    @DisplayName("la propriété système vigiechiro.depot.taille-max-mo reste prioritaire (tests/outils)")
    void propriete_systeme_prioritaire() {
        reglages.ecrireEntier(OngletReglagesDepot.CLE_TAILLE_MAX, 300);
        System.setProperty("vigiechiro.depot.taille-max-mo", "500");

        assertThat(LotModule.plafondArchiveOctets(reglages)).isEqualTo(500_000_000L);
    }
}

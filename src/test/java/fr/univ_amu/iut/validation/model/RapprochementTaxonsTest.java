package fr.univ_amu.iut.validation.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.univ_amu.iut.commun.api.ClientVigieChiro;
import fr.univ_amu.iut.commun.api.TaxonVigieChiro;
import fr.univ_amu.iut.commun.model.LienVigieChiro;
import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.commun.model.dao.LienVigieChiroDao;
import fr.univ_amu.iut.commun.persistence.MigrationSchema;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.validation.model.dao.TaxonDao;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/// Rapprochement des taxons (#728), API et DAO taxon **mockés**, DAO de liens réel (base jetable) : on
/// vérifie que seuls les taxons **présents localement** sont reliés (par `code == libelle_court`), et que
/// l'absence de réponse (hors-ligne) **ne purge pas** les correspondances déjà acquises.
@ExtendWith(MockitoExtension.class)
class RapprochementTaxonsTest {

    @TempDir
    Path dossier;

    @Mock
    private ClientVigieChiro client;

    @Mock
    private TaxonDao taxonDao;

    private LienVigieChiroDao liens;
    private RapprochementTaxons rapprochement;

    @BeforeEach
    void preparer() {
        SourceDeDonnees source = new SourceDeDonnees(new Workspace(dossier));
        new MigrationSchema(source).migrer();
        liens = new LienVigieChiroDao(source);
        rapprochement = new RapprochementTaxons(taxonDao, liens);
    }

    private static Taxon taxon(String code) {
        return new Taxon(code, null, null, 1L);
    }

    @Test
    @DisplayName("relie uniquement les taxons locaux, par code == libelle_court")
    void relie_les_taxons_locaux() {
        when(taxonDao.findAll()).thenReturn(List.of(taxon("Pippip"), taxon("Barbar"), taxon("Inconnu")));
        when(client.taxons())
                .thenReturn(List.of(
                        new TaxonVigieChiro("5a1", "Pippip", "Pipistrellus pipistrellus"),
                        new TaxonVigieChiro("5a2", "Barbar", "Barbastella barbastellus"),
                        new TaxonVigieChiro("5a9", "Rhirhi", "Rhinolophus"))); // pas dans nos taxons locaux

        rapprochement.synchroniser(client);

        // Pippip/Barbar reliés ; Rhirhi (non local) et Inconnu (absent côté plateforme) écartés.
        assertThat(liens.tous(LienVigieChiro.ENTITE_TAXON))
                .containsOnly(Map.entry("Pippip", "5a1"), Map.entry("Barbar", "5a2"));
    }

    @Test
    @DisplayName("hors-ligne (aucun taxon renvoyé) : ne purge pas les correspondances existantes")
    void hors_ligne_ne_purge_pas() {
        liens.upsert(new LienVigieChiro(LienVigieChiro.ENTITE_TAXON, "Pippip", "5a1"));
        when(taxonDao.findAll()).thenReturn(List.of(taxon("Pippip")));
        when(client.taxons()).thenReturn(List.of());

        rapprochement.synchroniser(client);

        assertThat(liens.objectidPour(LienVigieChiro.ENTITE_TAXON, "Pippip")).contains("5a1");
    }
}

package fr.univ_amu.iut.commun.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Tests du client de l'API VigieChiro (#725/#728) : mapping du profil `GET /moi`, des listes de taxons
/// (`GET /taxons/liste`) et de sites (`GET /moi/sites`), construction de l'en-tête d'authentification, et
/// **dégradation propre** (pas de token / réseau indisponible → vide). On ne teste pas d'appel réseau
/// réel : la logique de lecture est isolée dans des méthodes pures.
class ClientVigieChiroTest {

    private static final FournisseurToken SANS_TOKEN = Optional::empty;
    private static final FournisseurToken TOKEN_ABC = () -> Optional.of("abc");

    @Test
    @DisplayName("lireProfil lit _id / pseudo / role d'un profil complet")
    void lire_profil_complet() {
        String corps = "{\"_id\":\"698ddf3d\",\"pseudo\":\"Sébastien\",\"role\":\"Observateur\","
                + "\"donnees_publiques\":true}";

        Optional<ProfilVigieChiro> profil = ClientVigieChiro.lireProfil(corps);

        assertThat(profil).contains(new ProfilVigieChiro("698ddf3d", "Sébastien", "Observateur"));
    }

    @Test
    @DisplayName("lireProfil tolère les champs absents ou null (hors _id)")
    void lire_profil_champs_absents() {
        Optional<ProfilVigieChiro> profil = ClientVigieChiro.lireProfil("{\"_id\":\"x\",\"pseudo\":null}");

        assertThat(profil).isPresent();
        assertThat(profil.orElseThrow().id()).isEqualTo("x");
        assertThat(profil.orElseThrow().pseudo()).isNull();
        assertThat(profil.orElseThrow().role()).isNull();
    }

    @Test
    @DisplayName("lireProfil : sans _id → vide ; JSON illisible → vide (jamais d'exception)")
    void lire_profil_invalide_est_vide() {
        assertThat(ClientVigieChiro.lireProfil("{\"pseudo\":\"x\"}")).isEmpty();
        assertThat(ClientVigieChiro.lireProfil("pas du json")).isEmpty();
        assertThat(ClientVigieChiro.lireProfil("[]")).isEmpty();
    }

    @Test
    @DisplayName("enteteAuthorization : Basic base64(token:) ; token en username, mot de passe vide")
    void entete_authorization() {
        ClientVigieChiro client = new ClientVigieChiro("http://localhost:1", TOKEN_ABC);

        // base64("abc:") = "YWJjOg=="
        assertThat(client.enteteAuthorization()).contains("Basic YWJjOg==");
    }

    @Test
    @DisplayName("enteteAuthorization : sans token → vide (non connecté)")
    void entete_sans_token() {
        ClientVigieChiro client = new ClientVigieChiro("http://localhost:1", SANS_TOKEN);

        assertThat(client.enteteAuthorization()).isEmpty();
    }

    @Test
    @DisplayName("get / moi sans token → vide, sans même toucher le réseau")
    void moi_sans_token_est_vide() {
        ClientVigieChiro client = new ClientVigieChiro("http://localhost:1", SANS_TOKEN);

        assertThat(client.get("/moi")).isEmpty();
        assertThat(client.moi()).isEmpty();
    }

    @Test
    @DisplayName("get / moi hors-ligne (URL injoignable) → vide, sans lever")
    void moi_hors_ligne_est_vide() {
        ClientVigieChiro client = new ClientVigieChiro("http://localhost:1/api/v1", TOKEN_ABC);

        assertThat(client.get("/moi")).isEmpty();
        assertThat(client.moi()).isEmpty();
    }

    @Test
    @DisplayName("lireTaxons lit _id / libelle_court / libelle_long depuis la clé _items")
    void lire_taxons() {
        String corps = "{\"_items\":["
                + "{\"_id\":\"5a1\",\"libelle_court\":\"Pippip\",\"libelle_long\":\"Pipistrellus pipistrellus\"},"
                + "{\"_id\":\"5a2\",\"libelle_court\":\"Barbar\",\"libelle_long\":\"Barbastella barbastellus\"}]}";

        List<TaxonVigieChiro> taxons = ClientVigieChiro.lireTaxons(corps);

        assertThat(taxons)
                .containsExactly(
                        new TaxonVigieChiro("5a1", "Pippip", "Pipistrellus pipistrellus"),
                        new TaxonVigieChiro("5a2", "Barbar", "Barbastella barbastellus"));
    }

    @Test
    @DisplayName("lireTaxons : élément sans _id ou sans libelle_court ignoré, libelle_long absent → null")
    void lire_taxons_tolerant() {
        String corps = "{\"_items\":["
                + "{\"_id\":\"5a1\",\"libelle_court\":\"Pippip\"},"
                + "{\"libelle_court\":\"SansId\"},"
                + "{\"_id\":\"5a3\"}]}";

        List<TaxonVigieChiro> taxons = ClientVigieChiro.lireTaxons(corps);

        assertThat(taxons).containsExactly(new TaxonVigieChiro("5a1", "Pippip", null));
    }

    @Test
    @DisplayName("lireTaxons : corps illisible ou forme inattendue → liste vide (jamais d'exception)")
    void lire_taxons_illisible() {
        assertThat(ClientVigieChiro.lireTaxons("pas du json")).isEmpty();
        assertThat(ClientVigieChiro.lireTaxons("{\"autre\":1}")).isEmpty();
    }

    @Test
    @DisplayName("lireSites lit _id / titre / verrouille (défaut false), y compris en tableau nu")
    void lire_sites() {
        String corps = "{\"_items\":["
                + "{\"_id\":\"6b1\",\"titre\":\"Carre 810123\",\"verrouille\":true},"
                + "{\"_id\":\"6b2\",\"titre\":\"Mon jardin\"}],\"_meta\":{\"total\":2}}";

        List<SiteVigieChiro> sites = ClientVigieChiro.lireSites(corps);

        assertThat(sites)
                .containsExactly(
                        new SiteVigieChiro("6b1", "Carre 810123", true),
                        new SiteVigieChiro("6b2", "Mon jardin", false));
        // Un tableau JSON nu (sans enveloppe _items) est également accepté.
        assertThat(ClientVigieChiro.lireSites("[{\"_id\":\"6b3\",\"titre\":\"T\"}]"))
                .containsExactly(new SiteVigieChiro("6b3", "T", false));
    }

    @Test
    @DisplayName("lireSites : élément sans _id ignoré ; corps illisible → liste vide")
    void lire_sites_tolerant() {
        assertThat(ClientVigieChiro.lireSites("{\"_items\":[{\"titre\":\"sans id\"}]}"))
                .isEmpty();
        assertThat(ClientVigieChiro.lireSites("nope")).isEmpty();
    }

    @Test
    @DisplayName("taxons / mesSites sans token → listes vides, sans toucher le réseau")
    void listes_sans_token_sont_vides() {
        ClientVigieChiro client = new ClientVigieChiro("http://localhost:1/api/v1", SANS_TOKEN);

        assertThat(client.taxons()).isEmpty();
        assertThat(client.mesSites()).isEmpty();
    }
}

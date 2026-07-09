package fr.univ_amu.iut.commun.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/// Client HTTP de l'**API REST VigieChiro** (backend Eve, base `…/api/v1`, cf. #142). Socle réseau
/// destiné à être réutilisé par les features (identité, sites, taxons, participations, fichiers).
///
/// Calqué sur [fr.univ_amu.iut.passage.model.MeteoOpenMeteo] : `java.net.http`, timeout court et
/// **dégradation propre** — absence de token, réponse non-`200` ou panne réseau sont converties en
/// [Optional#empty()] ; aucune exception ne remonte à l'IHM.
///
/// **Authentification** : le token (fourni par [FournisseurToken]) est envoyé en **HTTP Basic**, token
/// en nom d'utilisateur et mot de passe vide, soit `Authorization: Basic base64("<token>:")`
/// (convention du backend Eve).
public final class ClientVigieChiro {

    private static final String URL_DEFAUT = "https://vigiechiro.herokuapp.com/api/v1";
    private static final Duration DELAI = Duration.ofSeconds(10);
    /// Clé de l'identifiant MongoDB, commune à tous les documents Eve (`_id`).
    private static final String CLE_ID = "_id";

    private final String baseUrl;
    private final FournisseurToken fournisseurToken;
    private final HttpClient client;

    public ClientVigieChiro(FournisseurToken fournisseurToken) {
        this(URL_DEFAUT, fournisseurToken);
    }

    /// Constructeur d'injection de l'URL de base (tests hors-ligne : une URL injoignable donne `empty`).
    ClientVigieChiro(String baseUrl, FournisseurToken fournisseurToken) {
        this.baseUrl = Objects.requireNonNull(baseUrl, "baseUrl");
        this.fournisseurToken = Objects.requireNonNull(fournisseurToken, "fournisseurToken");
        this.client = HttpClient.newBuilder().connectTimeout(DELAI).build();
    }

    /// Profil de l'utilisateur connecté (`GET /moi`), ou vide si non connecté / indisponible.
    public Optional<ProfilVigieChiro> moi() {
        return get("/moi").flatMap(ClientVigieChiro::lireProfil);
    }

    /// Référentiel officiel des taxons (`GET /taxons/liste`, résumé non paginé : `_id` + libellés).
    /// Liste vide si non connecté / indisponible (dégradation propre).
    public List<TaxonVigieChiro> taxons() {
        return get("/taxons/liste").map(ClientVigieChiro::lireTaxons).orElseGet(List::of);
    }

    /// Sites de l'observateur connecté (`GET /moi/sites`). Liste vide si non connecté / indisponible.
    ///
    /// La réponse Eve est paginée : on ne lit que la **première page** (`_items`). Un observateur a en
    /// pratique une poignée de sites (bien en deçà de la taille de page par défaut) ; on ne pagine donc
    /// pas ici.
    public List<SiteVigieChiro> mesSites() {
        return get("/moi/sites").map(ClientVigieChiro::lireSites).orElseGet(List::of);
    }

    /// **GET authentifié** sur `chemin` (relatif à la base) : renvoie le corps de la réponse si `200`,
    /// vide dans tous les autres cas (pas de token, `401`, autre non-`200`, réseau indisponible).
    Optional<String> get(String chemin) {
        Optional<String> entete = enteteAuthorization();
        if (entete.isEmpty()) {
            return Optional.empty();
        }
        try {
            HttpRequest requete = HttpRequest.newBuilder(URI.create(baseUrl + chemin))
                    .timeout(DELAI)
                    .header("Authorization", entete.get())
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> reponse = client.send(requete, HttpResponse.BodyHandlers.ofString());
            return reponse.statusCode() == 200 ? Optional.of(reponse.body()) : Optional.empty();
        } catch (InterruptedException interrompu) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (RuntimeException | IOException indisponible) {
            return Optional.empty();
        }
    }

    /// En-tête `Authorization` (`Basic base64("<token>:")`), ou vide si aucun token (non connecté).
    Optional<String> enteteAuthorization() {
        return fournisseurToken.token().map(ClientVigieChiro::basic);
    }

    private static String basic(String token) {
        String encode = Base64.getEncoder().encodeToString((token + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + encode;
    }

    /// Lit un [ProfilVigieChiro] depuis le corps JSON de `GET /moi`. Tolérant : JSON illisible ou sans
    /// `_id` donne vide. Package-visible : testable sur une réponse figée, sans réseau.
    static Optional<ProfilVigieChiro> lireProfil(String corps) {
        try {
            JsonObject objet = JsonParser.parseString(corps).getAsJsonObject();
            String id = texte(objet, CLE_ID);
            if (id == null) {
                return Optional.empty();
            }
            return Optional.of(new ProfilVigieChiro(id, texte(objet, "pseudo"), texte(objet, "role")));
        } catch (RuntimeException illisible) {
            return Optional.empty();
        }
    }

    /// Lit la liste des taxons depuis le corps JSON de `GET /taxons/liste`. Tolérant : éléments sans
    /// `_id` ou sans `libelle_court` ignorés, corps illisible → liste vide. Package-visible : testable
    /// sur une réponse figée, sans réseau.
    static List<TaxonVigieChiro> lireTaxons(String corps) {
        List<TaxonVigieChiro> taxons = new ArrayList<>();
        for (JsonElement element : items(corps)) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject objet = element.getAsJsonObject();
            String id = texte(objet, CLE_ID);
            String court = texte(objet, "libelle_court");
            if (id != null && court != null) {
                taxons.add(new TaxonVigieChiro(id, court, texte(objet, "libelle_long")));
            }
        }
        return taxons;
    }

    /// Lit la liste des sites depuis le corps JSON de `GET /moi/sites`. Tolérant : éléments sans `_id`
    /// ignorés, corps illisible → liste vide. Package-visible : testable sur une réponse figée.
    static List<SiteVigieChiro> lireSites(String corps) {
        List<SiteVigieChiro> sites = new ArrayList<>();
        for (JsonElement element : items(corps)) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject objet = element.getAsJsonObject();
            String id = texte(objet, CLE_ID);
            if (id != null) {
                sites.add(new SiteVigieChiro(id, texte(objet, "titre"), booleen(objet, "verrouille")));
            }
        }
        return sites;
    }

    /// Éléments d'une réponse de liste Eve : le tableau `_items` (réponses paginées) ou le corps
    /// lui-même s'il est déjà un tableau JSON. Corps illisible / forme inattendue → tableau vide.
    private static JsonArray items(String corps) {
        try {
            JsonElement racine = JsonParser.parseString(corps);
            if (racine.isJsonArray()) {
                return racine.getAsJsonArray();
            }
            if (racine.isJsonObject()) {
                JsonElement items = racine.getAsJsonObject().get("_items");
                if (items != null && items.isJsonArray()) {
                    return items.getAsJsonArray();
                }
            }
        } catch (RuntimeException illisible) {
            // corps non-JSON : on retombe sur un tableau vide (dégradation propre).
        }
        return new JsonArray();
    }

    private static String texte(JsonObject objet, String cle) {
        JsonElement element = objet.get(cle);
        return element == null || element.isJsonNull() ? null : element.getAsString();
    }

    private static boolean booleen(JsonObject objet, String cle) {
        JsonElement element = objet.get(cle);
        try {
            return element != null && !element.isJsonNull() && element.getAsBoolean();
        } catch (RuntimeException pasUnBooleen) {
            return false;
        }
    }
}

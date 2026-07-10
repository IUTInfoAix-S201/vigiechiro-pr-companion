package fr.univ_amu.iut.commun.api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Map;

/// Construction des **corps JSON des écritures** VigieChiro (#142) : pendant en écriture de
/// [ReponsesVigieChiro]. Fonctions **pures** (`record` / paramètres → `String`), testables sans réseau.
///
/// Gson en `snake_case` (`dateDebut` → `date_debut`, …) et **champs `null` omis** (défaut Gson) : un
/// `numero` ou `commentaire` absent ne pollue pas le corps envoyé au backend Eve.
final class RequetesVigieChiro {

    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    private RequetesVigieChiro() {}

    /// Corps de `POST /sites/#id/participations` (création de participation).
    static String participation(ParticipationADeposer participation) {
        return GSON.toJson(participation);
    }

    /// Corps de `POST /fichiers` (déclaration d'un fichier, upload simple). Le **mime n'est pas envoyé** :
    /// l'API le déduit de l'extension du titre ; il est fourni ensuite au `PUT` S3 (`Content-Type`).
    static String fichier(String titre) {
        return GSON.toJson(Map.of("titre", titre, "multipart", false));
    }

    /// Corps de finalisation `POST /fichiers/#id` : aucun champ requis (accusé de fin d'upload).
    static String finalisation() {
        return "{}";
    }
}

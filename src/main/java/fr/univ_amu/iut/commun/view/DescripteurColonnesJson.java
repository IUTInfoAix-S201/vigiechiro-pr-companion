package fr.univ_amu.iut.commun.view;

import com.google.gson.Gson;
import java.util.Objects;

/// (Dé)sérialisation JSON d'un [DescripteurColonnes] pour la **persistance de la disposition des colonnes**
/// (#994), via **Gson** (support natif des records). Format déterministe (ordre de déclaration des
/// composants) : `{"colonnes":[{"libelle":"…","visible":true}, …]}`.
///
/// Miroir de [DescripteurFiltreJson] pour les colonnes : sérialisation canonique de l'état produit par
/// [GestionnaireColonnes#decrire], transportable entre vues et stockable (vues mémorisées #623, préférence
/// par écran).
public final class DescripteurColonnesJson {

    private static final Gson GSON = new Gson();

    private DescripteurColonnesJson() {}

    /// Sérialise le descripteur en JSON déterministe.
    public static String serialiser(DescripteurColonnes descripteur) {
        Objects.requireNonNull(descripteur, "descripteur");
        return GSON.toJson(descripteur);
    }

    /// Reconstruit le descripteur depuis le JSON produit par [#serialiser(DescripteurColonnes)].
    public static DescripteurColonnes interpreter(String json) {
        Objects.requireNonNull(json, "json");
        return GSON.fromJson(json, DescripteurColonnes.class);
    }
}

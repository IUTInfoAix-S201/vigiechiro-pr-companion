package fr.univ_amu.iut.passage.model;

import java.util.Locale;

/// Force du vent d'un passage, telle que demandée au dépôt VigieChiro : domaine **fermé** à quatre
/// catégories d'appréciation (**nul / faible / moyen / fort**) plutôt qu'une vitesse chiffrée, car
/// c'est ainsi que l'observateur la renseigne sur le terrain.
///
/// Persistée sous son [#name] (`NUL`…`FORT`) dans la clé `vent` de `passage.weather_data` ; la relecture
/// est **tolérante** ([#depuisTexte] : valeur absente ou inconnue → `null`). Une vitesse Open-Meteo
/// (km/h) est ramenée à une catégorie par [#depuisVitesse].
public enum Vent {
    NUL("Nul"),
    FAIBLE("Faible"),
    MOYEN("Moyen"),
    FORT("Fort");

    /// Bornes de vitesse (km/h, vent à 10 m) séparant les catégories, calées sur l'échelle de Beaufort
    /// (approximation : l'observateur reste juge). `[0,5[` nul, `[5,20[` faible, `[20,40[` moyen,
    /// `[40,∞[` fort.
    private static final double SEUIL_FAIBLE_KMH = 5.0;
    private static final double SEUIL_MOYEN_KMH = 20.0;
    private static final double SEUIL_FORT_KMH = 40.0;

    private final String libelle;

    Vent(String libelle) {
        this.libelle = libelle;
    }

    /// Libellé lisible pour l'IHM (ex. « Faible »).
    public String libelle() {
        return libelle;
    }

    /// Lit une force de vent depuis un texte stocké/saisi : `null` ou vide → `null` ; sinon la constante
    /// correspondante (insensible à la casse), ou `null` si le texte ne correspond à aucune (tolérant).
    public static Vent depuisTexte(String texte) {
        if (texte == null || texte.isBlank()) {
            return null;
        }
        try {
            return valueOf(texte.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException inconnue) {
            return null;
        }
    }

    /// Ramène une **vitesse** (km/h, convention Open-Meteo) à une catégorie d'appréciation, selon les
    /// bornes de Beaufort ([#SEUIL_FAIBLE_KMH] etc.).
    public static Vent depuisVitesse(double vitesseKmh) {
        if (vitesseKmh < SEUIL_FAIBLE_KMH) {
            return NUL;
        }
        if (vitesseKmh < SEUIL_MOYEN_KMH) {
            return FAIBLE;
        }
        if (vitesseKmh < SEUIL_FORT_KMH) {
            return MOYEN;
        }
        return FORT;
    }
}

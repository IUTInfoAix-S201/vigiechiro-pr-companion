package fr.univ_amu.iut.passage.model;

import java.util.Locale;

/// Couverture nuageuse d'un passage, telle que demandée au dépôt VigieChiro : domaine **fermé** à quatre
/// **tranches** de ciel (0-25 / 25-50 / 50-75 / 75-100 %) plutôt qu'un pourcentage chiffré, car c'est
/// ainsi que l'observateur la renseigne.
///
/// Persistée sous son [#name] dans la clé `couvertureNuageuse` de `passage.weather_data` ; la relecture
/// est **tolérante** ([#depuisTexte] : valeur absente ou inconnue → `null`). Un pourcentage Open-Meteo
/// est ramené à une tranche par [#depuisPourcentage].
public enum CouvertureNuageuse {
    DE_0_A_25("0 à 25 %"),
    DE_25_A_50("25 à 50 %"),
    DE_50_A_75("50 à 75 %"),
    DE_75_A_100("75 à 100 %");

    /// Bornes (%) séparant les tranches. Une valeur sur une borne rejoint la tranche **supérieure**
    /// (`25` → 25-50, `50` → 50-75, `75` → 75-100), pour une partition sans recouvrement.
    private static final double SEUIL_BAS = 25.0;
    private static final double SEUIL_MOYEN = 50.0;
    private static final double SEUIL_HAUT = 75.0;

    private final String libelle;

    CouvertureNuageuse(String libelle) {
        this.libelle = libelle;
    }

    /// Libellé lisible pour l'IHM (ex. « 25 à 50 % »).
    public String libelle() {
        return libelle;
    }

    /// Lit une tranche depuis un texte stocké/saisi : `null` ou vide → `null` ; sinon la constante
    /// correspondante (insensible à la casse), ou `null` si le texte ne correspond à aucune (tolérant).
    public static CouvertureNuageuse depuisTexte(String texte) {
        if (texte == null || texte.isBlank()) {
            return null;
        }
        try {
            return valueOf(texte.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException inconnue) {
            return null;
        }
    }

    /// Ramène un **pourcentage** de couverture (convention Open-Meteo, 0 à 100) à sa tranche.
    public static CouvertureNuageuse depuisPourcentage(double pourcentage) {
        if (pourcentage < SEUIL_BAS) {
            return DE_0_A_25;
        }
        if (pourcentage < SEUIL_MOYEN) {
            return DE_25_A_50;
        }
        if (pourcentage < SEUIL_HAUT) {
            return DE_50_A_75;
        }
        return DE_75_A_100;
    }
}

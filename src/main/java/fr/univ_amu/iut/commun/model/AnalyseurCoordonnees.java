package fr.univ_amu.iut.commun.model;

/// Analyse une coordonnée GPS **d'un axe** (latitude ou longitude) saisie dans des formats variés et la
/// convertit en **degrés décimaux** (#153). Les observateurs collent des coordonnées venues de sources
/// hétérogènes ; on accepte donc :
///
/// - **degrés décimaux (DD)** : `43.401`, `43,401` (virgule française tolérée), `-1.574`, `43.401 N` ;
/// - **degrés/minutes/secondes (DMS)** : `43°24'3.6"N`, `43 24 3.6 N`, `43:24:3.6 N` ;
/// - **degrés-minutes décimales** : `43°24.06'N`, `43 24.06 N`.
///
/// Le **signe** vient de l'hémisphère (`N`/`E` positif, `S`/`W` négatif) s'il est présent, sinon d'un
/// éventuel `-` de tête. La méthode **ne valide pas la plage** (−90..90 / −180..180) : c'est la
/// responsabilité de l'appelant, qui connaît l'axe. Elle lève `NumberFormatException` sur une saisie
/// inanalysable (lettres parasites, minutes/secondes ≥ 60, plus de trois composantes…).
public final class AnalyseurCoordonnees {

    private AnalyseurCoordonnees() {}

    /// Convertit `saisie` en degrés décimaux (DD ou DMS). Lève `NumberFormatException` si le format est
    /// invalide. Ne borne pas la valeur (validation de plage à la charge de l'appelant).
    public static double enDegresDecimaux(String saisie) {
        if (saisie == null || saisie.isBlank()) {
            throw new NumberFormatException("Coordonnée vide");
        }
        String texte = saisie.trim().replace(',', '.');

        int signeHemisphere = extraireSigneHemisphere(texte);
        String sansHemisphere = texte.replaceAll("[NSEWnsew]", "").trim();

        String[] composantes = sansHemisphere.split("[°ºd'\"′″:\\s]+");
        double[] nombres = nombres(composantes);
        if (nombres.length == 0 || nombres.length > 3) {
            throw new NumberFormatException("Coordonnée invalide : " + saisie);
        }

        double degres = Math.abs(nombres[0]);
        double minutes = nombres.length >= 2 ? nombres[1] : 0.0;
        double secondes = nombres.length >= 3 ? nombres[2] : 0.0;
        // En DMS (au moins deux composantes) minutes et secondes sont dans [0, 60[.
        if (nombres.length >= 2 && (minutes < 0 || minutes >= 60 || secondes < 0 || secondes >= 60)) {
            throw new NumberFormatException("Minutes/secondes hors bornes : " + saisie);
        }

        double amplitude = degres + minutes / 60.0 + secondes / 3600.0;
        int signe = signeHemisphere != 0 ? signeHemisphere : (nombres[0] < 0 ? -1 : 1);
        return signe * amplitude;
    }

    /// −1 pour un hémisphère sud/ouest, +1 pour nord/est, 0 si aucun. Lève si plusieurs lettres
    /// d'hémisphère sont présentes (`43N24S` n'a pas de sens).
    private static int extraireSigneHemisphere(String texte) {
        String lettres = texte.replaceAll("[^NSEWnsew]", "");
        if (lettres.isEmpty()) {
            return 0;
        }
        if (lettres.length() > 1) {
            throw new NumberFormatException("Hémisphère ambigu : " + texte);
        }
        char hemisphere = Character.toUpperCase(lettres.charAt(0));
        return hemisphere == 'S' || hemisphere == 'W' ? -1 : 1;
    }

    /// Parse les composantes numériques non vides ; lève sur un jeton non numérique (lettre parasite).
    private static double[] nombres(String[] composantes) {
        return java.util.Arrays.stream(composantes)
                .filter(composante -> !composante.isBlank())
                .mapToDouble(Double::parseDouble)
                .toArray();
    }
}

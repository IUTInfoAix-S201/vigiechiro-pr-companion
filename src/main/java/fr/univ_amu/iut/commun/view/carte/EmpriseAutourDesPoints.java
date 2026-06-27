package fr.univ_amu.iut.commun.view.carte;

import java.util.List;
import java.util.Optional;

/// Fournisseur de repli (#152) : emprise **2 km centrée sur le barycentre** des points géolocalisés du
/// carré. Honnête en l'absence du carroyage officiel : le carré n'est pas calé sur la grille nationale,
/// mais il est ancré sur la position **réelle** de ses points. Sans aucun point géolocalisé, renvoie vide
/// (le carré ne sera pas tracé).
///
/// Conversion km → degrés : 1° de latitude ≈ 111 km ; 1° de longitude ≈ 111 km × cos(latitude). Suffisant
/// à l'échelle de 2 km.
public final class EmpriseAutourDesPoints implements FournisseurEmpriseCarre {

    /// Demi-côté du carré, en kilomètres (carré Vigie-Chiro de 2 km de côté).
    private static final double DEMI_COTE_KM = 1.0;

    private static final double KM_PAR_DEGRE_LAT = 111.0;

    @Override
    public Optional<EmpriseCarre> emprise(String numeroCarre, List<PointGeo> pointsDuCarre) {
        List<PointGeo> geolocalises = pointsDuCarre.stream()
                .filter(EmpriseAutourDesPoints::estGeolocalise)
                .toList();
        if (geolocalises.isEmpty()) {
            return Optional.empty();
        }
        double latCentre =
                geolocalises.stream().mapToDouble(PointGeo::latitude).average().orElseThrow();
        double lonCentre =
                geolocalises.stream().mapToDouble(PointGeo::longitude).average().orElseThrow();
        double demiLat = DEMI_COTE_KM / KM_PAR_DEGRE_LAT;
        double demiLon = DEMI_COTE_KM / (KM_PAR_DEGRE_LAT * Math.cos(Math.toRadians(latCentre)));
        return Optional.of(
                new EmpriseCarre(latCentre - demiLat, lonCentre - demiLon, latCentre + demiLat, lonCentre + demiLon));
    }

    /// Un point est exploitable s'il a des coordonnées finies (pas de NaN issu d'un GPS manquant).
    private static boolean estGeolocalise(PointGeo point) {
        return Double.isFinite(point.latitude()) && Double.isFinite(point.longitude());
    }
}

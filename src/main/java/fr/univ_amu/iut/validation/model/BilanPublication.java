package fr.univ_amu.iut.validation.model;

import java.util.List;

/// Bilan d'une **publication des corrections** vers VigieChiro (#723) : ce qui a été écrit côté
/// plateforme, ce qui a été écarté et pourquoi, et le détail des refus. La publication est
/// **idempotente** (re-pousser une correction identique réécrit la même valeur) : relancer après
/// avoir complété les manques ou rétabli le réseau est toujours sûr.
///
/// @param poussees corrections effectivement écrites côté plateforme
/// @param sansCertitude observations revues mais **sans certitude déclarée** : « à compléter avant
///     publication » (la plateforme exige la certitude avec le taxon, jamais posée par défaut)
/// @param sansAncrage observations revues mais **sans ancrage plateforme** (import CSV, ou import
///     antérieur au chantier #1139) : réimporter depuis VigieChiro pour les ancrer
/// @param horsReferentiel taxon observateur **sans objectid** VigieChiro (hors référentiel) : non
///     publiable, cas normal à afficher
/// @param echecs détail des refus, une entrée par observation (identification locale + cause)
public record BilanPublication(
        int poussees, int sansCertitude, int sansAncrage, int horsReferentiel, List<String> echecs) {

    /// `true` si tout ce qui était publiable a été écrit (aucun refus ; il peut rester des écartées).
    public boolean sansEchec() {
        return echecs.isEmpty();
    }

    /// Nombre d'observations écartées avant envoi (à compléter, sans ancrage ou hors référentiel).
    public int ecartees() {
        return sansCertitude + sansAncrage + horsReferentiel;
    }
}

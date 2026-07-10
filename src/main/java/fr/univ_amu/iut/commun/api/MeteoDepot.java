package fr.univ_amu.iut.commun.api;

/// Bloc **météo** d'une participation à déposer (#142) : les deux champs conservés par l'API VigieChiro
/// (`meteo.vent`, `meteo.couverture`). Les valeurs suivent les énumérations du backend (déjà celles de
/// l'app, #702) : `vent` ∈ `NUL | FAIBLE | MOYEN | FORT`, `couverture` ∈ `0-25 | 25-50 | 50-75 | 75-100`.
/// Sérialisé tel quel (clés `vent` / `couverture`) par [RequetesVigieChiro].
///
/// @param vent code de force du vent (peut être `null` si non renseigné)
/// @param couverture tranche de couverture nuageuse (peut être `null` si non renseignée)
public record MeteoDepot(String vent, String couverture) {}

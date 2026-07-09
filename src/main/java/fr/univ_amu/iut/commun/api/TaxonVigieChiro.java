package fr.univ_amu.iut.commun.api;

/// Taxon du référentiel **VigieChiro** (`GET /taxons/liste`, #728). Vue minimale servant au
/// rapprochement : l'`id` MongoDB et le **code court** (`libelle_court`), qui correspond au `code`
/// Tadarida de nos taxons locaux (ex. `Pippip`, `Barbar`). Le nom long (`libelle_long`, nom latin)
/// est conservé pour l'affichage et le diagnostic.
///
/// @param id identifiant VigieChiro (`_id`, 24 caractères hexadécimaux)
/// @param libelleCourt code court (`libelle_court`), aligné sur `taxon.code`
/// @param libelleLong libellé long (`libelle_long`, nom latin), éventuellement `null`
public record TaxonVigieChiro(String id, String libelleCourt, String libelleLong) {}

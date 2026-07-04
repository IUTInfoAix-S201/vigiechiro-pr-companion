package fr.univ_amu.iut.commun.model;

/// Port socle de **comptage des validations observateur menacées** par une opération destructive.
///
/// Supprimer un passage ou « écraser et réimporter » une nuit détruit **en cascade** les observations du
/// passage, donc le travail de validation déjà saisi (taxon corrigé, marquage référence, commentaire). À
/// la différence d'une ré-importation de CSV (où ces validations sont réattachées), cette perte est
/// **irréversible** : les confirmations correspondantes doivent en rendre compte pour ne pas la rendre
/// silencieuse.
///
/// Le socle inverse la dépendance (même esprit que les contrats `Ouvrir*`) : la feature `validation` en
/// fournit l'implémentation ([fr.univ_amu.iut.validation.model.ServiceValidation]) et la lie dans son
/// module ; les features `passage` (suppression) et `importation` (écrasement) l'injectent pour afficher le
/// nombre menacé **sans dépendre** de `validation` (le graphe de slices reste acyclique).
public interface CompteurValidations {

    /// Nombre d'observations du passage `idPassage` portant une validation observateur (taxon corrigé,
    /// marquage référence ou commentaire) : le travail qui serait **définitivement perdu** si le passage
    /// était supprimé. `0` si le passage n'a pas de résultats importés ou aucune observation validée.
    int menaceesPourPassage(Long idPassage);
}

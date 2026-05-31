package fr.univ_amu.iut.multisite.model;

/// Vue sauvegardée de la navigation multi-sites (table `saved_view`). Mémorise un jeu de
/// filtres nommé (le menu « ⭐ Mes vues » de l'écran M-MultiSite, story E5.S3) afin que
/// l'utilisateur puisse rejouer une combinaison de critères sans la ressaisir.
///
/// L'`id` (clé technique auto-incrémentée) vaut `null` tant que la vue n'a pas été
/// insérée : [fr.univ_amu.iut.multisite.model.dao.SavedViewDao#insert(SavedView)] renvoie une
/// copie avec l'id généré par SQLite.
///
/// Les critères (`filtresJson`) sont stockés tels quels, sérialisés en JSON dans une
/// colonne `TEXT NOT NULL` : le modèle ne les interprète pas, il transporte la chaîne brute.
///
/// @param id clé technique, `null` avant insertion
/// @param nom nom convivial de la vue (obligatoire, colonne `name`)
/// @param filtresJson critères de filtrage sérialisés en JSON (obligatoire, colonne
///     `filters_json`)
public record SavedView(Long id, String nom, String filtresJson) {}

package fr.univ_amu.iut.validation.model;

/**
 * Groupe taxonomique : niveau hiérarchique au-dessus du taxon (C15, table {@code taxonomic_group}).
 *
 * <p>Sert de filtre groupé dans l'IHM (« tous les murins », « toutes les pipistrelles »). Chaque
 * groupe regroupe {@code 1..*} {@link Taxon} via {@code taxon.group_id}.
 *
 * <p>L'{@code id} (clé technique auto-incrémentée) vaut {@code null} tant que le groupe n'a pas été
 * inséré : {@link
 * fr.univ_amu.iut.validation.model.dao.GroupeTaxonomiqueDao#insert(GroupeTaxonomique)} renvoie une
 * copie avec l'id généré par SQLite.
 *
 * <p>Le {@code niveau} (« Genre » / « Famille » / « Ordre ») est conceptuellement un énum mais
 * reste stocké en {@code TEXT} libre : aucun énum n'est fourni par {@code commun.model} pour ce
 * point de variation (cf. note d'intégration), on le modélise donc en {@link String} comme {@code
 * Site#numeroCarre()} pour rester fidèle au patron de la feature de référence.
 *
 * @param id clé technique, {@code null} avant insertion
 * @param niveau niveau hiérarchique (ex. {@code "Genre"}, {@code "Famille"}, {@code "Ordre"})
 * @param nom nom du groupe (ex. {@code "Pipistrellus"}, {@code "Vespertilionidae"})
 */
public record GroupeTaxonomique(Long id, String niveau, String nom) {}

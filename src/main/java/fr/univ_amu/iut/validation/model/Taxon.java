package fr.univ_amu.iut.validation.model;

/**
 * Taxon : code 6 lettres de la nomenclature Tadarida (C14, table {@code taxon}).
 *
 * <p>Contrairement aux entités à clé technique, le taxon a une <b>clé naturelle</b> : son {@code
 * code} (3 lettres du genre + 3 de l'espèce, ex. {@code Pippip}, {@code Nyclei}). Les pseudo-taxons
 * {@code noise} (bruit) et {@code piaf} (oiseau) suivent la même table. L'insertion ne récupère
 * donc aucune clé générée (cf. {@code UtilisateurDao}) : {@link
 * fr.univ_amu.iut.validation.model.dao.TaxonDao#insert(Taxon)} renvoie l'entité telle quelle.
 *
 * <p>Le nom latin et le nom vernaculaire sont optionnels ({@code null}) : les pseudo-taxons n'ont
 * pas de nom latin.
 *
 * @param code clé naturelle, code 6 lettres (ou pseudo-taxon {@code noise} / {@code piaf})
 * @param nomLatin nom latin (optionnel, ex. {@code "Pipistrellus pipistrellus"})
 * @param nomVernaculaireFr nom vernaculaire français (optionnel, ex. {@code "Pipistrelle commune"})
 * @param idGroupe identifiant du groupe taxonomique parent (FK → {@code taxonomic_group.id})
 */
public record Taxon(String code, String nomLatin, String nomVernaculaireFr, Long idGroupe) {}

package fr.univ_amu.iut.validation.model;

/**
 * Résultats d'identification : fichier CSV produit par Tadarida et importé pour la validation
 * taxonomique (C12, table {@code identification_results}).
 *
 * <p>Rattaché à <b>un seul passage</b> ({@code passage_id} unique, cardinalité 0:1 côté passage) :
 * un passage est annoté par au plus un jeu de résultats. Agrège {@code 1..*} {@link Observation}
 * via {@code observation.results_id}.
 *
 * <p>L'{@code id} (clé technique auto-incrémentée) vaut {@code null} tant que les résultats n'ont
 * pas été insérés. Le {@code formatDetecte} (« Brut » / « Vu ») est un énum stocké en {@code TEXT}
 * libre (aucun énum {@code commun.model} fourni pour ce point de variation, cf. note
 * d'intégration).
 *
 * @param id clé technique, {@code null} avant insertion
 * @param cheminFichier chemin du CSV sur disque (sous-dossier {@code transformes/}, R23)
 * @param formatDetecte format détecté (ex. {@code "Brut"} avec guillemets, {@code "Vu"}
 *     réinjectable)
 * @param dateImport date/heure d'import (ISO-8601)
 * @param idPassage identifiant du passage annoté (FK → {@code passage.id}, unique)
 */
public record ResultatsIdentification(
    Long id, String cheminFichier, String formatDetecte, String dateImport, Long idPassage) {}

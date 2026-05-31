package fr.univ_amu.iut.bibliotheque.model;

/**
 * Une entrée de la <b>bibliothèque de sons de référence</b> (parcours P10, COULD) : le
 * rapprochement d'une {@link fr.univ_amu.iut.validation.model.Observation} marquée « référence » et
 * de la {@link fr.univ_amu.iut.passage.model.SequenceDEcoute} dont elle est extraite.
 *
 * <p><b>Objet de présentation</b> (pas une entité persistée) : il transporte uniquement ce qui est
 * nécessaire au récapitulatif exporté (CSV) et à la copie du fichier audio correspondant. Aucune
 * dépendance JavaFX (couche {@code model} pure).
 *
 * @param taxon code du taxon retenu : taxon observateur s'il a été validé, sinon taxon Tadarida
 *     (jamais {@code null}, {@code taxon_tadarida} étant obligatoire au schéma)
 * @param nomSequence nom de fichier de la séquence d'écoute source ({@code
 *     listening_sequence.file_name})
 * @param cheminFichier chemin sur disque du fichier de séquence à copier ({@code
 *     listening_sequence.file_path}, sous-dossier {@code transformes/})
 * @param frequenceHz fréquence médiane en Hz de l'observation ({@code null} si absente)
 * @param commentaire commentaire libre de l'observateur ({@code null} si absent)
 */
public record EntreeBiblio(
    String taxon,
    String nomSequence,
    String cheminFichier,
    Integer frequenceHz,
    String commentaire) {}

package fr.univ_amu.iut.bibliotheque.model;

import fr.univ_amu.iut.commun.model.EcrivainCsv;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Bibliothèque de sons de référence exportable (parcours P10, story E8, COULD).
 *
 * <p>Produite par {@link ServiceBibliotheque#exporterBibliotheque()}, elle agrège les {@link
 * EntreeBiblio entrées} issues des observations marquées « référence ». C'est un <b>objet de
 * présentation</b> (pas une entité persistée) : il sait se sérialiser en deux artefacts <b>sans
 * aucun accès réseau ni effet de bord caché</b> :
 *
 * <ul>
 *   <li>un <b>CSV récapitulatif</b> (colonnes {@code taxon}, {@code sequence source}, {@code
 *       fichier}, {@code frequence}, {@code commentaire}) via l'{@link EcrivainCsv} partagé du
 *       socle {@code commun} ;
 *   <li>la <b>liste des chemins de fichiers de séquences à copier</b> (dédupliquée, ordre stable),
 *       que la couche IHM matérialisera ensuite par une copie disque (hors périmètre {@code
 *       model}).
 * </ul>
 *
 * <p><b>Déterminisme</b> (cf. SERVICE-CONVENTIONS §5) : aucun horodatage ni hash dans la sortie,
 * ordre des colonnes et des lignes figé (le service trie les entrées avant de construire l'export)
 * — deux exécutions produisent le même octet, ce qui rend le CSV testable par <i>approval</i>.
 */
public record ExportBiblioSons(List<EntreeBiblio> entrees) {

  /** En-tête du CSV récapitulatif. Ordre des colonnes figé (déterminisme). */
  public static final List<String> ENTETE =
      List.of("taxon", "sequence source", "fichier", "frequence", "commentaire");

  /** Copie défensive immuable de la liste d'entrées. */
  public ExportBiblioSons {
    entrees = List.copyOf(entrees);
  }

  /** Nombre d'entrées (observations de référence exportées). */
  public int nombre() {
    return entrees.size();
  }

  /**
   * Lignes du CSV récapitulatif : l'{@link #ENTETE en-tête} suivi d'une ligne par entrée. Les
   * valeurs {@code null} (fréquence, commentaire absents) deviennent une chaîne vide.
   */
  public List<List<String>> lignesCsv() {
    List<List<String>> lignes = new ArrayList<>();
    lignes.add(ENTETE);
    for (EntreeBiblio entree : entrees) {
      lignes.add(
          List.of(
              texte(entree.taxon()),
              texte(entree.nomSequence()),
              texte(entree.cheminFichier()),
              entree.frequenceHz() == null ? "" : String.valueOf(entree.frequenceHz()),
              texte(entree.commentaire())));
    }
    return lignes;
  }

  /** CSV récapitulatif sérialisé (séparateur {@code ;}, guillemets seulement si nécessaire). */
  public String versCsv() {
    return EcrivainCsv.minimal().versChaine(lignesCsv());
  }

  /** Écrit le CSV récapitulatif en UTF-8 dans {@code fichier} (crée les dossiers parents). */
  public void ecrireCsv(Path fichier) {
    EcrivainCsv.minimal().ecrire(fichier, lignesCsv());
  }

  /**
   * Chemins des fichiers de séquences à copier, <b>dédupliqués</b> (une séquence portant plusieurs
   * observations de référence n'est copiée qu'une fois) et dans l'ordre des entrées.
   */
  public List<String> cheminsSequences() {
    LinkedHashSet<String> chemins = new LinkedHashSet<>();
    for (EntreeBiblio entree : entrees) {
      if (entree.cheminFichier() != null) {
        chemins.add(entree.cheminFichier());
      }
    }
    return List.copyOf(chemins);
  }

  private static String texte(String valeur) {
    return valeur == null ? "" : valeur;
  }
}

package fr.univ_amu.iut.bibliotheque.model;

import fr.univ_amu.iut.commun.model.EcrivainCsv;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/// Bibliothèque de sons de référence exportable (parcours P10, story E8, COULD).
///
/// Produite par [ServiceBibliotheque#exporterBibliotheque()], elle agrège les
/// [entrées][EntreeBiblio] issues des observations marquées « référence ». C'est un **objet de
/// présentation** (pas une entité persistée) : il sait se sérialiser en deux artefacts **sans
/// aucun accès réseau ni effet de bord caché** :
///
/// - un **CSV récapitulatif** (colonnes `taxon`, `sequence source`, `fichier`, `frequence`,
///   `commentaire`) via l'[EcrivainCsv] partagé du socle `commun` ;
/// - la **liste des chemins de fichiers de séquences à copier** (dédupliquée, ordre stable), que la
///   couche IHM matérialisera ensuite par une copie disque (hors périmètre `model`).
///
/// **Déterminisme** (cf. SERVICE-CONVENTIONS §5) : aucun horodatage ni hash dans la sortie,
/// ordre des colonnes et des lignes figé (le service trie les entrées avant de construire
/// l'export) — deux exécutions produisent le même octet, ce qui rend le CSV testable par
/// *approval*.
public record ExportBiblioSons(List<EntreeBiblio> entrees) {

    /// En-tête du CSV récapitulatif. Ordre des colonnes figé (déterminisme).
    public static final List<String> ENTETE =
            List.of("taxon", "sequence source", "fichier", "frequence", "commentaire");

    /// Copie défensive immuable de la liste d'entrées.
    public ExportBiblioSons {
        entrees = List.copyOf(entrees);
    }

    /// Nombre d'entrées (observations de référence exportées).
    public int nombre() {
        return entrees.size();
    }

    /// Lignes du CSV récapitulatif : l'[en-tête][#ENTETE] suivi d'une ligne par entrée. Les
    /// valeurs `null` (fréquence, commentaire absents) deviennent une chaîne vide.
    public List<List<String>> lignesCsv() {
        List<List<String>> lignes = new ArrayList<>();
        lignes.add(ENTETE);
        for (EntreeBiblio entree : entrees) {
            lignes.add(List.of(
                    texte(entree.taxon()),
                    texte(entree.nomSequence()),
                    texte(entree.cheminFichier()),
                    entree.frequenceHz() == null ? "" : String.valueOf(entree.frequenceHz()),
                    texte(entree.commentaire())));
        }
        return lignes;
    }

    /// CSV récapitulatif sérialisé (séparateur `;`, guillemets seulement si nécessaire).
    public String versCsv() {
        return EcrivainCsv.minimal().versChaine(lignesCsv());
    }

    /// Écrit le CSV récapitulatif en UTF-8 dans `fichier` (crée les dossiers parents).
    public void ecrireCsv(Path fichier) {
        EcrivainCsv.minimal().ecrire(fichier, lignesCsv());
    }

    /// Chemins des fichiers de séquences à copier, **dédupliqués** (une séquence portant plusieurs
    /// observations de référence n'est copiée qu'une fois) et dans l'ordre des entrées.
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

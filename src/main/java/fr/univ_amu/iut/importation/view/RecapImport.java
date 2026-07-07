package fr.univ_amu.iut.importation.view;

import fr.univ_amu.iut.importation.model.ResultatImport;
import fr.univ_amu.iut.importation.model.ResultatImportMultiNuits;
import fr.univ_amu.iut.importation.viewmodel.EtatImport;

/// Récapitulatif textuel d'un import (statut affiché sous le bouton « Importer »). Helper de vue extrait
/// du controller : met en phrase l'issue selon l'état et le résultat (mono-nuit ou multi-nuits).
///
/// - **annulé** → « Opération annulée. » ;
/// - **terminé, multi-nuits** → « N passage(s) créé(s) (nuits du … au …), M séquence(s) » ;
/// - **terminé, mono-nuit** → « M séquence(s) à partir de N original(aux) » + avertissements (#155/#214) ;
/// - sinon (en cours, prêt) → chaîne vide.
final class RecapImport {

    private RecapImport() {}

    /// Libellé de statut pour l'état et le(s) résultat(s) courant(s) (`resultatMultiNuits` non nul en
    /// import multi-nuits, sinon `resultatMono`).
    static String libelle(EtatImport etat, ResultatImport resultatMono, ResultatImportMultiNuits resultatMultiNuits) {
        if (etat == EtatImport.ANNULE) {
            return "Opération annulée.";
        }
        if (etat != EtatImport.TERMINE) {
            return "";
        }
        if (resultatMultiNuits != null) {
            return libelleNuits(resultatMultiNuits);
        }
        if (resultatMono == null) {
            return "";
        }
        // Rapport d'import (#155, #214) : doublon de nuit, fichiers ignorés et rejetés, délégué au rapport.
        return "✓ Import terminé : "
                + resultatMono.nombreSequences()
                + " séquence(s) produite(s) à partir de "
                + resultatMono.nombreOriginaux()
                + " original(aux)."
                + resultatMono.rapport().avertissements();
    }

    /// Récapitulatif multi-nuits : nombre de passages créés, plage de dates couverte, total de séquences.
    private static String libelleNuits(ResultatImportMultiNuits resultat) {
        var passages = resultat.parNuit();
        String premiere = passages.getFirst().passage().dateEnregistrement();
        String derniere = passages.getLast().passage().dateEnregistrement();
        String plage = premiere.equals(derniere) ? "nuit du " + premiere : "nuits du " + premiere + " au " + derniere;
        return "✓ Import terminé : "
                + resultat.nombrePassages()
                + " passage(s) créé(s) (" + plage + "), "
                + resultat.nombreSequencesTotal()
                + " séquence(s) produite(s).";
    }
}

package fr.univ_amu.iut.passage.model;

import java.util.List;

/// Résultat d'une hydratation réussie (#1650, #1651) : le **bilan** du rebranchement, la **fréquence
/// d'acquisition** lue du log, et les **bruts** effectivement rebranchés avec leurs séquences.
///
/// La fréquence et les bruts servent à l'**adoption** (#1651) : remplacer le placeholder du passage
/// reconstruit par les vrais originaux régénérés.
///
/// @param bilan ce qui a été rebranché / refusé / manque (concordance acoustique en indice comprise)
/// @param frequenceAcquisitionHz la Fe utilisée pour régénérer (celle qu'auront les vrais originaux)
/// @param brutsRebranches un élément par brut rebranché, avec ses séquences
record ResultatHydratation(BilanReactivation bilan, int frequenceAcquisitionHz, List<BrutRebranche> brutsRebranches) {

    ResultatHydratation {
        brutsRebranches = List.copyOf(brutsRebranches);
    }
}

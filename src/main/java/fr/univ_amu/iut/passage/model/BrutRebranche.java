package fr.univ_amu.iut.passage.model;

import java.util.List;

/// Un brut hydraté et **les séquences qu'il a produites** (#1651) : de quoi remplacer, après coup, le
/// placeholder d'un passage reconstruit par un **vrai** enregistrement original et y rattacher ses
/// séquences.
///
/// @param brut le brut inventorié (chemin source + nom R6)
/// @param sequences les séquences de ce brut (celles dont il a régénéré les tranches)
record BrutRebranche(BrutInventorie brut, List<SequenceDEcoute> sequences) {

    BrutRebranche {
        sequences = List.copyOf(sequences);
    }
}

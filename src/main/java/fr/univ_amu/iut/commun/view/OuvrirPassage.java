package fr.univ_amu.iut.commun.view;

import fr.univ_amu.iut.commun.viewmodel.ContexteSite;

/// Contrat de navigation inter-feature : « ouvrir l'écran pivot M-Passage pour un passage donné ».
///
/// Défini dans le socle (`commun.view`) pour permettre à `sites` (M-Site-detail) d'ouvrir M-Passage
/// **sans dépendre du `view` de la feature `passage`** (règle ArchUnit
/// `pas_de_dependance_inter_feature_vers_la_vue`). La feature `passage` en fournit
/// l'implémentation (`NavigationPassage`, bindée par `PassageModule`). Même esprit que
/// [OuvrirVerification].
public interface OuvrirPassage {

    /// Ouvre l'écran pivot du passage `idPassage`, avec le [ContexteSite] (carré/code/nom du site)
    /// fourni par l'écran appelant.
    void ouvrir(Long idPassage, ContexteSite contexte);
}

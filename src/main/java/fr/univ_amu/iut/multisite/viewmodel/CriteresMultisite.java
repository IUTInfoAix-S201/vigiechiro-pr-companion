package fr.univ_amu.iut.multisite.viewmodel;

import fr.univ_amu.iut.commun.model.StatutWorkflow;
import fr.univ_amu.iut.commun.model.Verdict;
import fr.univ_amu.iut.multisite.model.FiltresMultisite;
import fr.univ_amu.iut.multisite.model.LignePassage;
import java.util.function.Predicate;

/// Fabrique les **prédicats** des filtres composables de la vue multi-sites (#537) à partir des
/// valeurs de critère saisies (numéro de carré, statut, verdict, année). Chaque fabrique **réutilise
/// la sémantique de** [FiltresMultisite#accepte(LignePassage)] : un critère absent (`null` ou saisie
/// en blanc) rend `null`, c'est-à-dire **aucun filtre**, que le socle
/// [fr.univ_amu.iut.commun.viewmodel.Filtres] retire de la conjonction.
///
/// Pendant, côté multisite, du `CriteresAudio` de la feature audio : contrôles fixes (pas de barre à
/// puces « à la Notion »), donc de simples prédicats plutôt que des `CritereFiltre` porteurs d'un
/// éditeur. Pur Java sans JavaFX (helper de view-model).
final class CriteresMultisite {

    private CriteresMultisite() {}

    /// Prédicat « n° de carré », ou `null` (aucun filtre) si la saisie est vide/en blanc.
    static Predicate<LignePassage> parCarre(String valeur) {
        String carre = texteOuNull(valeur);
        return carre == null ? null : FiltresMultisite.parSite(carre)::accepte;
    }

    /// Prédicat « statut de workflow », ou `null` (aucun filtre) si non renseigné.
    static Predicate<LignePassage> parStatut(StatutWorkflow statut) {
        return statut == null ? null : FiltresMultisite.parStatut(statut)::accepte;
    }

    /// Prédicat « verdict de vérification », ou `null` (aucun filtre) si non renseigné.
    static Predicate<LignePassage> parVerdict(Verdict verdict) {
        return verdict == null ? null : FiltresMultisite.parVerdict(verdict)::accepte;
    }

    /// Prédicat « année », ou `null` (aucun filtre) si non renseignée.
    static Predicate<LignePassage> parAnnee(Integer annee) {
        return annee == null ? null : FiltresMultisite.parAnnee(annee)::accepte;
    }

    /// Normalise une saisie texte : `null` si vide ou en blanc, sinon la valeur sans espaces de bord.
    static String texteOuNull(String valeur) {
        return valeur == null || valeur.isBlank() ? null : valeur.trim();
    }
}

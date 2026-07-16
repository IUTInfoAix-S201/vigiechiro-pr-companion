package fr.univ_amu.iut.audio.view;

import fr.univ_amu.iut.audio.viewmodel.AudioViewModel;
import fr.univ_amu.iut.commun.view.Confirmateur;
import java.nio.file.Path;

/// Déclenche l'import d'un CSV Tadarida sur la vue audio : import direct s'il n'y a pas encore de jeu de
/// résultats, sinon **demande confirmation** du remplacement (un seul jeu par passage) avant de
/// réimporter. Isolé du controller (même patron que [DepotFichier]) pour le garder léger.
///
/// La confirmation passe par le port [Confirmateur] du socle (#1013) et non plus par un `Alert` en dur
/// (#1405) : c'est ce qui rend le **réimport** jouable dans un test. Or c'est justement le geste qui
/// **perd les validations en cours** - celui qu'on veut voir couvert.
final class ImportTadarida {

    private ImportTadarida() {}

    /// Lance l'import de `cheminCsv` via le `viewModel` : import direct si aucun résultat, sinon réimport
    /// (remplacement) après confirmation de l'utilisateur ([DecisionRemplacementJeu], décision partagée avec
    /// l'import ☰ VigieChiro). Renvoie `true` **seulement si un import a réellement abouti** : `false` si
    /// l'utilisateur annule la confirmation de remplacement ou si l'import échoue, pour que le
    /// glisser-déposer ne signale pas un dépôt réussi à tort.
    static boolean lancer(AudioViewModel viewModel, Path cheminCsv, Confirmateur confirmateur) {
        return DecisionRemplacementJeu.resoudre(
                        viewModel.resultatsDisponiblesProperty().get(), confirmateur, "ce nouvel import")
                .map(remplacer -> viewModel.importer(cheminCsv, remplacer))
                .orElse(false);
    }
}

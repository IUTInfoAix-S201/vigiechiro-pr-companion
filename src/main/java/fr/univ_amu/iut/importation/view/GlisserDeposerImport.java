package fr.univ_amu.iut.importation.view;

import fr.univ_amu.iut.importation.model.ExtracteurZip;
import java.io.File;
import java.nio.file.Path;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/// Installe le **glisser-déposer** (#139) d'un dossier ou d'un `.zip` sur l'écran d'import, avec un
/// **retour visuel** pendant le survol (classe CSS `zone-depot-active`). Helper de vue extrait du
/// controller : la mécanique DnD (survol / sortie / dépôt + acceptabilité) est autonome, paramétrée par
/// un test d'occupation (formulaire gelé pendant un traitement) et une action de dépôt.
final class GlisserDeposerImport {

    /// Classe CSS du retour visuel de glisser-déposer, posée sur la racine pendant le survol.
    private static final String CLASSE_ZONE_DEPOT_ACTIVE = "zone-depot-active";

    private GlisserDeposerImport() {}

    /// Câble les gestionnaires DnD sur `racine` : un dépôt valide (dossier ou `.zip`, hors traitement)
    /// appelle `surDepot` avec le chemin déposé ; `occupe` gèle l'écran pendant un import/décompression.
    static void installer(Node racine, BooleanSupplier occupe, Consumer<Path> surDepot) {
        racine.setOnDragOver(evenement -> {
            if (acceptable(evenement.getDragboard(), occupe)) {
                evenement.acceptTransferModes(TransferMode.COPY);
                if (!racine.getStyleClass().contains(CLASSE_ZONE_DEPOT_ACTIVE)) {
                    racine.getStyleClass().add(CLASSE_ZONE_DEPOT_ACTIVE);
                }
            }
            evenement.consume();
        });
        racine.setOnDragExited(evenement -> {
            racine.getStyleClass().remove(CLASSE_ZONE_DEPOT_ACTIVE);
            evenement.consume();
        });
        racine.setOnDragDropped(evenement -> {
            boolean accepte = acceptable(evenement.getDragboard(), occupe);
            if (accepte) {
                surDepot.accept(evenement.getDragboard().getFiles().get(0).toPath());
            }
            racine.getStyleClass().remove(CLASSE_ZONE_DEPOT_ACTIVE);
            evenement.setDropCompleted(accepte);
            evenement.consume();
        });
    }

    /// Vrai si le glisser porte un **dossier** ou un **.zip** (seules sources d'import acceptables) et
    /// qu'aucun traitement n'est en cours (`occupe` : import ou décompression, formulaire gelé).
    private static boolean acceptable(Dragboard dragboard, BooleanSupplier occupe) {
        if (occupe.getAsBoolean() || !dragboard.hasFiles()) {
            return false;
        }
        File premier = dragboard.getFiles().get(0);
        return premier.isDirectory() || ExtracteurZip.estZip(premier.toPath());
    }
}

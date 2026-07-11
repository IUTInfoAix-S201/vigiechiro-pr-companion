package fr.univ_amu.iut.lot.view;

import fr.univ_amu.iut.commun.view.TableSuivi;
import fr.univ_amu.iut.commun.viewmodel.Formats;
import fr.univ_amu.iut.lot.viewmodel.LigneArchive;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/// Configure la table de suivi du dépôt (#820) : une [LigneArchive] par ZIP. Délègue au socle
/// [TableSuivi] (colonnes `#`/Progression, cellule état/barre, coloration de la ligne selon l'état) et
/// n'ajoute que les colonnes propres au dépôt : Fichiers et Taille. Encapsulé hors du controller pour
/// garder celui-ci en pur câblage.
final class TableSuiviArchives {

    private TableSuiviArchives() {}

    /// Pose colonnes, cellules et rangées colorées sur `table` (l'alimentation en items reste au controller).
    static void configurer(TableView<LigneArchive> table) {
        TableSuivi.configurer(table, "Aucune archive de dépôt pour l'instant.", colFichiers(), colTaille());
    }

    private static TableColumn<LigneArchive, Integer> colFichiers() {
        TableColumn<LigneArchive, Integer> col = new TableColumn<>("Fichiers");
        col.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().nombreFichiers()));
        col.setPrefWidth(90);
        col.setSortable(false);
        return col;
    }

    /// Colonne « Taille » : estimée (préfixée d'un `~`) tant que la ligne n'est pas terminée, réelle ensuite.
    /// La valeur suit la taille ET l'état de la ligne (elle passe d'estimée à réelle à la fin).
    private static TableColumn<LigneArchive, String> colTaille() {
        TableColumn<LigneArchive, String> col = new TableColumn<>("Taille");
        col.setCellValueFactory(c -> {
            LigneArchive l = c.getValue();
            return Bindings.createStringBinding(
                    () -> (l.tailleEstimee() ? "~ " : "")
                            + Formats.octetsLisibles(l.tailleOctetsProperty().get()),
                    l.tailleOctetsProperty(),
                    l.etatProperty());
        });
        col.setPrefWidth(110);
        col.setSortable(false);
        return col;
    }
}

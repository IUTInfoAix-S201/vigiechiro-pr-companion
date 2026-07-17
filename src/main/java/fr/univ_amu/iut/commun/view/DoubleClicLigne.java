package fr.univ_amu.iut.commun.view;

import java.util.function.Consumer;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;

/// Câble les gestes de ligne d'une [TableView], de façon **uniforme** sur toutes les vues tabulaires :
///
/// - **double-clic gauche** sur une ligne non vide → `action` (navigation ou ouverture propre à la vue) ;
/// - **clic droit** sur une ligne non vide **pas encore sélectionnée** → sélection de cette seule ligne,
///   pour que le menu contextuel cible la ligne survolée. Une ligne déjà sélectionnée est laissée en place :
///   une sélection **multiple** (validation en lot) n'est donc pas cassée par un clic droit en son sein.
///
/// Remplace les `setRowFactory` manuels dupliqués dans les contrôleurs (Inventaire, Multisite, Détail
/// site, Audit…). Pose son propre `rowFactory` : à appeler sur une table qui n'en a pas d'autre (aucune
/// des tables du projet ne stylait ses lignes via un `rowFactory`).
public final class DoubleClicLigne {

    private DoubleClicLigne() {}

    /// Installe les gestes sur `table` : double-clic gauche → `action` (reçoit l'élément de la ligne),
    /// clic droit → sélection de la ligne survolée (sans casser une sélection multiple existante).
    public static <T> void installer(TableView<T> table, Consumer<T> action) {
        table.setRowFactory(tableau -> {
            TableRow<T> ligne = new TableRow<>();
            ligne.setOnMousePressed(evenement -> {
                if (evenement.getButton() == MouseButton.SECONDARY
                        && !ligne.isEmpty()
                        && !table.getSelectionModel().isSelected(ligne.getIndex())) {
                    table.getSelectionModel().clearAndSelect(ligne.getIndex());
                }
            });
            ligne.setOnMouseClicked(evenement -> {
                if (evenement.getButton() == MouseButton.PRIMARY
                        && evenement.getClickCount() == 2
                        && !ligne.isEmpty()) {
                    action.accept(ligne.getItem());
                }
            });
            return ligne;
        });
    }
}

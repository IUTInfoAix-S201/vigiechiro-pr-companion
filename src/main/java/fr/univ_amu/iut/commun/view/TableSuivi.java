package fr.univ_amu.iut.commun.view;

import fr.univ_amu.iut.commun.viewmodel.EtatUnite;
import fr.univ_amu.iut.commun.viewmodel.LigneSuivi;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

/// Configure une **table de suivi par unité** : une [LigneSuivi] par unité de travail, avec la colonne
/// `#`, les colonnes propres à la feature au milieu, la cellule état/barre ([CelluleProgressionUnite])
/// en dernière colonne et la **coloration de la ligne** selon l'état (classes CSS `.ligne-suivi.etat-…`,
/// définies dans `design.css`). Socle partagé (génération des archives #820, suivi des fichiers
/// d'import…), encapsulé hors des controllers pour les garder en pur câblage.
public final class TableSuivi {

    private TableSuivi() {}

    /// Pose colonnes, cellules et rangées colorées sur `table` (l'alimentation en items reste au
    /// controller) : `#`, puis `colonnesSpecifiques` (dans l'ordre), puis « Progression ».
    @SafeVarargs
    public static <L extends LigneSuivi> void configurer(
            TableView<L> table, String placeholder, TableColumn<L, ?>... colonnesSpecifiques) {
        List<TableColumn<L, ?>> colonnes = new ArrayList<>();
        colonnes.add(colNumero());
        colonnes.addAll(List.of(colonnesSpecifiques));
        colonnes.add(colProgression());
        table.getColumns().setAll(colonnes);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setRowFactory(t -> ligneColoreeSelonEtat());
        table.setPlaceholder(new Label(placeholder));
    }

    private static <L extends LigneSuivi> TableColumn<L, Integer> colNumero() {
        TableColumn<L, Integer> col = new TableColumn<>("#");
        col.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().numero()));
        col.setPrefWidth(48);
        col.setSortable(false);
        return col;
    }

    private static <L extends LigneSuivi> TableColumn<L, L> colProgression() {
        TableColumn<L, L> col = new TableColumn<>("Progression");
        col.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));
        col.setCellFactory(c -> new CelluleProgressionUnite<>());
        col.setPrefWidth(220);
        col.setSortable(false);
        return col;
    }

    /// Rangée dont la classe d'état (`.ligne-suivi.etat-…`) suit **en place** l'état de sa ligne : la
    /// couleur de fond / des icônes change quand l'unité passe en cours, terminée ou en échec.
    private static <L extends LigneSuivi> TableRow<L> ligneColoreeSelonEtat() {
        return new TableRow<>() {
            private final ChangeListener<EtatUnite> maj = (obs, avant, apres) -> appliquer(apres);

            @Override
            protected void updateItem(L ligne, boolean vide) {
                L ancien = getItem();
                if (ancien != null) {
                    ancien.etatProperty().removeListener(maj);
                }
                super.updateItem(ligne, vide);
                if (vide || ligne == null) {
                    appliquer(null);
                } else {
                    ligne.etatProperty().addListener(maj);
                    appliquer(ligne.etatProperty().get());
                }
            }

            private void appliquer(EtatUnite etat) {
                getStyleClass().removeIf(c -> c.equals("ligne-suivi") || c.startsWith("etat-"));
                if (etat != null) {
                    getStyleClass().addAll("ligne-suivi", classePour(etat));
                }
            }
        };
    }

    private static String classePour(EtatUnite etat) {
        return switch (etat) {
            case EN_ATTENTE -> "etat-attente";
            case EN_COURS -> "etat-cours";
            case TERMINEE -> "etat-terminee";
            case ECHEC -> "etat-echec";
        };
    }
}

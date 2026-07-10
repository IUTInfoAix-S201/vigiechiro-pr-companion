package fr.univ_amu.iut.commun.view;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

/// Validation de formulaire « en direct » (#790) : plutôt que de laisser fermer une boîte de dialogue sur
/// un clic puis afficher une Alert détachée du champ fautif, on **désactive le bouton de validation** tant
/// que la saisie est invalide et on **marque les champs fautifs** (classe CSS `champ-invalide`, bordure
/// rouge, définie dans `design.css`).
///
/// Généralise ce que la modale point d'écoute faisait déjà à la main, pour les boîtes bâties en code
/// (`Dialog`/`DialogPane`) qui n'ont pas de contrôleur FXML dédié.
public final class ValidationFormulaire {

    /// Classe CSS d'un champ en erreur (bordure rouge), définie dans `commun/view/design.css`. Le
    /// [DialogPane] concerné doit charger cette feuille (cf. [#appliquerStyles]).
    public static final String CLASSE_CHAMP_INVALIDE = "champ-invalide";

    private ValidationFormulaire() {}

    /// Désactive le bouton `type` du [DialogPane] tant que `valide` est faux. Le nœud du bouton n'est créé
    /// par le DialogPane qu'une fois son [ButtonType] déclaré : à appeler **après**
    /// `getButtonTypes().add(...)`.
    public static void gaterBouton(DialogPane pane, ButtonType type, ObservableBooleanValue valide) {
        pane.lookupButton(type).disableProperty().bind(Bindings.not(valide));
    }

    /// Ajoute (ou retire) la classe [#CLASSE_CHAMP_INVALIDE] sur `champ` selon `invalide`, réactivement.
    /// À alimenter avec une condition « saisi **mais** incorrect » : un champ encore vide ne doit pas
    /// rougir avant toute saisie.
    public static void marquerInvalide(Node champ, ObservableBooleanValue invalide) {
        appliquer(champ, invalide.get());
        invalide.addListener((observable, avant, apres) -> appliquer(champ, apres));
    }

    /// Charge sur `pane` les feuilles de style partagées (palette + design) pour qu'une boîte bâtie en
    /// code résolve `champ-invalide` et les jetons de couleur, comme un écran FXML.
    public static void appliquerStyles(DialogPane pane) {
        pane.getStylesheets()
                .addAll(
                        ValidationFormulaire.class.getResource("palette.css").toExternalForm(),
                        ValidationFormulaire.class.getResource("design.css").toExternalForm());
    }

    private static void appliquer(Node champ, boolean invalide) {
        champ.getStyleClass().remove(CLASSE_CHAMP_INVALIDE);
        if (invalide) {
            champ.getStyleClass().add(CLASSE_CHAMP_INVALIDE);
        }
    }
}

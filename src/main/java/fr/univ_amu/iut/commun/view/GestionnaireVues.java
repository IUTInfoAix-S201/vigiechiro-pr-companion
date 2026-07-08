package fr.univ_amu.iut.commun.view;

import fr.univ_amu.iut.commun.model.DepotVues;
import fr.univ_amu.iut.commun.model.VueSauvegardee;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/// Barre d'**onglets de vues mémorisées** « à la Notion » (#623), au-dessus d'une table : un onglet par
/// vue enregistrée de la `feature`, plus un bouton **« + Vue »**. Cliquer un onglet **rejoue** sa
/// combinaison de filtres (via [GestionnaireFiltres#restaurer(DescripteurFiltre)]) ; « + Vue » enregistre
/// les filtres **courants** (via [GestionnaireFiltres#decrire()]) ; chaque onglet offre renommer (✎) et
/// supprimer (✕).
///
/// Générique sur le type de ligne `T` (il ne connaît que le [GestionnaireFiltres] qu'il pilote). Socle
/// partagé (`commun`) des vues tabulaires : multisite d'abord, puis audio / analyse. La **saisie du nom**
/// (nouvelle vue ou renommage) dépend de l'IHM de l'écran hôte : elle est **injectée** (`saisieNom`), ce
/// qui garde ce gestionnaire indépendant de tout dialogue concret et **testable** sans boîte modale.
///
/// @param <T> type des lignes filtrées (ex. `LignePassage`)
public final class GestionnaireVues<T> {

    private final Pane onglets;
    private final GestionnaireFiltres<T> filtres;
    private final DepotVues depot;
    private final String feature;
    private final Function<String, Optional<String>> saisieNom;

    /// Vue actuellement **active** (onglet surligné), ou `null` (aucune vue rejouée / filtres libres).
    private VueSauvegardee active;

    /// `true` si les filtres courants ont **divergé** de l'instantané de la vue active (modifications non
    /// enregistrées) : l'onglet actif propose alors « 💾 Enregistrer dans la vue ». Toujours faux sans vue
    /// active.
    private boolean modifiee;

    /// Garde : vrai le temps de [#appliquer(VueSauvegardee)], pour **ignorer** les changements de filtres émis
    /// par la restauration elle-même (sinon la vue tout juste rejouée serait aussitôt marquée « modifiée »).
    private boolean enApplication;

    /// Construit la barre d'onglets et la peuple immédiatement (vues persistées de la `feature`).
    ///
    /// @param onglets conteneur des onglets (fourni par la vue, typiquement un `HBox`/`FlowPane`)
    /// @param filtres barre de filtres pilotée (source de `decrire` / cible de `restaurer`)
    /// @param depot dépôt de persistance des vues mémorisées
    /// @param feature clé de l'écran/table (chaque écran ne voit que ses vues)
    /// @param saisieNom demande un nom à l'utilisateur (défaut fourni en argument) ; vide = annulation
    public GestionnaireVues(
            Pane onglets,
            GestionnaireFiltres<T> filtres,
            DepotVues depot,
            String feature,
            Function<String, Optional<String>> saisieNom) {
        this.onglets = Objects.requireNonNull(onglets, "onglets");
        this.filtres = Objects.requireNonNull(filtres, "filtres");
        this.depot = Objects.requireNonNull(depot, "depot");
        this.feature = Objects.requireNonNull(feature, "feature");
        this.saisieNom = Objects.requireNonNull(saisieNom, "saisieNom");
        this.filtres.surChangement(this::auChangementFiltres);
        rafraichir();
    }

    /// Réévalue si les filtres courants divergent de la vue active à chaque changement de filtre, et ne
    /// reconstruit la barre **qu'au basculement** de l'état (apparition/disparition du « 💾 »), pas à chaque
    /// frappe. Ignore les changements émis par [#appliquer(VueSauvegardee)] (garde [#enApplication]).
    private void auChangementFiltres() {
        if (enApplication) {
            return;
        }
        boolean neo = active != null
                && !DescripteurFiltreJson.serialiser(filtres.decrire()).equals(active.descripteurJson());
        if (neo != modifiee) {
            modifiee = neo;
            rafraichir();
        }
    }

    /// Construit la barre d'onglets avec la **boîte de saisie standard** du nom de vue : un [TextInputDialog]
    /// ancré sur la fenêtre de `onglets`. Les écrans tabulaires (audio, analyse, multisite) partagent ce
    /// dialogue au lieu d'en dupliquer un chacun. Le constructeur à `saisieNom` explicite reste disponible
    /// pour les tests (qui pilotent le nommage sans ouvrir de modale).
    ///
    /// @param onglets conteneur des onglets (fournit aussi la fenêtre propriétaire du dialogue)
    /// @param filtres barre de filtres pilotée
    /// @param depot dépôt de persistance des vues
    /// @param feature clé de l'écran/table
    public static <T> GestionnaireVues<T> avecDialogue(
            Pane onglets, GestionnaireFiltres<T> filtres, DepotVues depot, String feature) {
        return new GestionnaireVues<>(onglets, filtres, depot, feature, defaut -> demanderNom(onglets, defaut));
    }

    /// Boîte de saisie standard du nom d'une vue (création ou renommage) : renvoie le nom nettoyé, ou vide
    /// si l'utilisateur annule ou laisse le champ blanc.
    private static Optional<String> demanderNom(Pane onglets, String defaut) {
        TextInputDialog dialogue = new TextInputDialog(defaut);
        dialogue.initOwner(onglets.getScene().getWindow());
        dialogue.setHeaderText("Nom de la vue");
        dialogue.setContentText("Nom :");
        return dialogue.showAndWait().map(String::trim).filter(nom -> !nom.isBlank());
    }

    /// Recharge les vues de la `feature` et reconstruit la barre (un onglet par vue + le bouton « + Vue »).
    public void rafraichir() {
        onglets.getChildren().clear();
        for (VueSauvegardee vue : depot.findByFeature(feature)) {
            onglets.getChildren().add(construireOnglet(vue));
        }
        onglets.getChildren().add(boutonNouvelle());
    }

    /// Enregistre les **filtres courants** (via `decrire`) comme une nouvelle vue nommée `nom`, l'active et
    /// rafraîchit la barre. Un nom vide/en blanc est refusé (retourne `null`).
    ///
    /// @return la vue insérée (avec son `id`), ou `null` si le nom est vide
    public VueSauvegardee enregistrer(String nom) {
        if (nom == null || nom.isBlank()) {
            return null;
        }
        VueSauvegardee vue = depot.insert(
                new VueSauvegardee(null, feature, nom.trim(), DescripteurFiltreJson.serialiser(filtres.decrire())));
        active = vue;
        modifiee = false; // la nouvelle vue capture exactement les filtres courants
        rafraichir();
        return vue;
    }

    /// Rejoue la combinaison de filtres de `vue` (la restaure sur la barre de filtres) et l'active. La garde
    /// [#enApplication] empêche que la restauration elle-même ne marque aussitôt la vue « modifiée ».
    public void appliquer(VueSauvegardee vue) {
        enApplication = true;
        filtres.restaurer(DescripteurFiltreJson.interpreter(vue.descripteurJson()));
        enApplication = false;
        active = vue;
        modifiee = false;
        rafraichir();
    }

    /// Réécrit l'instantané de la **vue active** avec les filtres courants (« 💾 Enregistrer dans la vue ») :
    /// les modifications en cours deviennent le nouvel état enregistré de la vue. Sans effet si aucune vue
    /// n'est active.
    public void enregistrerDansActive() {
        if (active == null) {
            return;
        }
        VueSauvegardee misAJour = new VueSauvegardee(
                active.id(), feature, active.nom(), DescripteurFiltreJson.serialiser(filtres.decrire()));
        depot.update(misAJour);
        active = misAJour;
        modifiee = false;
        rafraichir();
    }

    /// Renomme `vue` (le descripteur est conservé) et rafraîchit. Un nom vide/en blanc est ignoré.
    public void renommer(VueSauvegardee vue, String nom) {
        if (nom != null && !nom.isBlank()) {
            VueSauvegardee renommee = new VueSauvegardee(vue.id(), vue.feature(), nom.trim(), vue.descripteurJson());
            depot.update(renommee);
            if (vue.equals(active)) {
                active = renommee;
            }
            rafraichir();
        }
    }

    /// Supprime `vue` (la désactive si c'était l'onglet actif) puis rafraîchit.
    public void supprimer(VueSauvegardee vue) {
        depot.delete(vue.id());
        if (vue.equals(active)) {
            active = null;
            modifiee = false;
        }
        rafraichir();
    }

    private HBox construireOnglet(VueSauvegardee vue) {
        Label nom = new Label(vue.nom());
        nom.getStyleClass().add("onglet-vue-nom");
        nom.setOnMouseClicked(evenement -> appliquer(vue));

        HBox onglet = new HBox(4.0, nom);
        onglet.getStyleClass().add("onglet-vue");
        boolean estActive = vue.equals(active);
        if (estActive) {
            onglet.getStyleClass().add("onglet-vue-actif");
        }
        // Vue active dont les filtres ont divergé de l'instantané : indicateur « modifié » (le bouton 💾
        // n'apparaît que dans cet état) + enregistrement explicite, sans écraser la vue par surprise.
        if (estActive && modifiee) {
            onglet.getStyleClass().add("onglet-vue-modifie");
            onglet.getChildren()
                    .add(bouton(
                            "💾",
                            "Enregistrer les filtres courants dans la vue " + vue.nom(),
                            this::enregistrerDansActive));
        }
        onglet.getChildren()
                .add(bouton(
                        "✎",
                        "Renommer la vue " + vue.nom(),
                        () -> saisieNom.apply(vue.nom()).ifPresent(nouveau -> renommer(vue, nouveau))));
        onglet.getChildren().add(bouton("✕", "Supprimer la vue " + vue.nom(), () -> supprimer(vue)));
        return onglet;
    }

    /// Bouton d'action d'un onglet (💾 / ✎ / ✕) : style et libellé accessible communs.
    private static Button bouton(String texte, String accessible, Runnable action) {
        Button bouton = new Button(texte);
        bouton.getStyleClass().add("onglet-vue-action");
        bouton.setAccessibleText(accessible);
        bouton.setOnAction(evenement -> action.run());
        return bouton;
    }

    private Button boutonNouvelle() {
        Button ajout = new Button("+ Vue");
        ajout.getStyleClass().add("onglet-vue-nouvelle");
        ajout.setAccessibleText("Enregistrer les filtres courants comme une nouvelle vue");
        ajout.setOnAction(evenement -> saisieNom.apply("").ifPresent(this::enregistrer));
        return ajout;
    }
}

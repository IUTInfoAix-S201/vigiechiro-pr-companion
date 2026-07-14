package fr.univ_amu.iut.audio.view;

import fr.univ_amu.iut.audio.viewmodel.AudioViewModel;
import fr.univ_amu.iut.audio.viewmodel.FormatAvisValidateur;
import fr.univ_amu.iut.validation.model.LigneObservationAudio;
import fr.univ_amu.iut.validation.model.MessageObservation;
import java.util.List;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/// Le **fil de discussion** de l'observation sélectionnée, donné à lire à côté du spectrogramme (#1417).
///
/// **Pourquoi pas une modale.** Un `showAndWait()` fige un test headless (leçon #1013 / #1405), et surtout
/// un fil se lit *en écoutant* : l'obliger à passer par une fenêtre qui masque le son, c'est séparer deux
/// gestes que l'utilisateur fait ensemble. Le panneau vit donc à droite du lecteur.
///
/// Il n'apparaît que **s'il y a quelque chose à lire** : sur la grande majorité des détections, personne
/// n'a jamais écrit, et un cadre vide permanent volerait de la largeur au spectrogramme pour ne rien dire.
///
/// En **lecture** seule : répondre viendra avec #1418 (une écriture définitive, qui demande sa propre
/// confirmation).
final class PanneauDiscussion {

    /// Largeur du panneau : assez pour une phrase, assez peu pour ne pas écraser le spectrogramme.
    private static final double LARGEUR = 320;

    private final VBox racine = new VBox(6);
    private final VBox messages = new VBox(10);

    PanneauDiscussion() {
        racine.setId("panneauDiscussion");
        racine.getStyleClass().add("panneau-discussion");
        racine.setPrefWidth(LARGEUR);
        racine.setMinWidth(LARGEUR);
        racine.setVisible(false);
        racine.setManaged(false);

        Label titre = new Label("Discussion avec le validateur");
        titre.getStyleClass().add("titre-discussion");

        messages.setId("filDiscussion");
        ScrollPane cadre = new ScrollPane(messages);
        cadre.setFitToWidth(true);
        VBox.setVgrow(cadre, javafx.scene.layout.Priority.ALWAYS);

        racine.getChildren().addAll(titre, cadre);
    }

    /// Nœud à insérer dans le panneau d'écoute.
    VBox racine() {
        return racine;
    }

    /// **Installe** le fil dans l'écran : il prend place dans `hote`, à droite du lecteur, et suit la
    /// sélection de `table`. Le chargement passe par le ViewModel — la vue ne touche jamais la base.
    ///
    /// Installé ici plutôt qu'épelé dans le contrôleur (patron de [MenuCertitude#installer]) : celui-ci est
    /// au plafond de NcssCount, et ce câblage forme une unité cohésive qui n'a rien à y faire.
    static PanneauDiscussion installer(
            StackPane hote, TableView<LigneObservationAudio> table, AudioViewModel viewModel) {
        PanneauDiscussion panneau = new PanneauDiscussion();
        hote.getChildren().add(panneau.racine());
        hote.visibleProperty().bind(panneau.racine().visibleProperty());
        hote.managedProperty().bind(panneau.racine().managedProperty());
        table.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, ancienne, nouvelle) -> panneau.recharger(nouvelle, viewModel));
        return panneau;
    }

    /// Recharge le fil de `ligne`. Une **séquence non identifiée** (aucune observation) n'existe pas côté
    /// plateforme : elle ne peut pas porter de discussion, le panneau se referme.
    private void recharger(LigneObservationAudio ligne, AudioViewModel viewModel) {
        Long idObservation = ligne == null ? null : ligne.idObservation();
        afficher(viewModel.filDeLObservation(idObservation), viewModel.idProfilConnecte());
    }

    /// Affiche `fil`, ou **efface le panneau** s'il est vide : le panneau ne s'ouvre que quand un
    /// validateur (ou nous) a réellement écrit. `idProfilConnecte` sert à dire « Vous » sans appel réseau.
    void afficher(List<MessageObservation> fil, String idProfilConnecte) {
        messages.getChildren().clear();
        boolean aQuelqueChoseADire = !fil.isEmpty();
        racine.setVisible(aQuelqueChoseADire);
        racine.setManaged(aQuelqueChoseADire);
        if (aQuelqueChoseADire) {
            fil.forEach(message -> messages.getChildren().add(bulle(message, idProfilConnecte)));
        }
    }

    /// Un message : qui, quand, quoi. Savoir **qui parle** est la moitié de l'information dans une
    /// discussion — l'entête le dit en clair, et le style distingue nos messages de ceux de l'expert.
    private static VBox bulle(MessageObservation message, String idProfilConnecte) {
        Label entete = new Label(entete(message, idProfilConnecte));
        entete.getStyleClass().add(message.deMoi(idProfilConnecte) ? "message-de-moi" : "message-du-validateur");

        Label texte = new Label(message.texte());
        texte.setWrapText(true);
        texte.setMaxWidth(LARGEUR - 30);

        VBox bulle = new VBox(2, entete, texte);
        bulle.getStyleClass().add("bulle-message");
        return bulle;
    }

    /// « Vous · 11/07/2026 21:04 », ou l'auteur seul quand le serveur n'a pas daté le message.
    private static String entete(MessageObservation message, String idProfilConnecte) {
        String auteur = FormatAvisValidateur.auteur(message, idProfilConnecte);
        String quand = FormatAvisValidateur.quand(message);
        return quand.isEmpty() ? auteur : auteur + " · " + quand;
    }
}

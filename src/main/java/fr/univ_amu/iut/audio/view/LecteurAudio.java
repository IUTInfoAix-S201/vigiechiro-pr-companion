package fr.univ_amu.iut.audio.view;

import fr.nedjar.vigiechiro.audio.AudioView;
import java.util.List;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SeparatorMenuItem;

/// Options de **lecture** de la vue audio (#483), ajoutées en tête du menu « ☰ » et câblées sur l'[AudioView] :
///
/// - **Lecture automatique à la sélection** (cochée par défaut, désactivable) : à chaque clip **prêt**
///   (`readyProperty` passe à vrai après le chargement de la séquence sélectionnée), la lecture démarre.
/// - **Lecture en boucle** : la boucle de l'`AudioView` suit la case.
///
/// Créées **programmatiquement** (comme le « Colonnes… » du [GestionnaireColonnes]) plutôt qu'en `@FXML` :
/// concerne toutes les sources (pas de visibilité conditionnelle) et garde le controller (pur câblage) sous
/// son seuil de cohésion PMD. Sorti du controller comme [RepereCriAudio] / [MetriquesAcoustiquesAudio].
final class LecteurAudio {

    private LecteurAudio() {}

    /// Ajoute les deux options en **fin** du menu `menu`, après un séparateur (ce sont des **réglages** de
    /// lecture, regroupés en bas comme « Colonnes… », les actions d'import/export restant en tête), et les
    /// branche sur `audioView` : boucle liée à sa case ; auto-lecture déclenchée à chaque clip prêt si sa
    /// case est cochée.
    static void installer(AudioView audioView, MenuButton menu) {
        CheckMenuItem lectureAuto = new CheckMenuItem("🔊 Lecture automatique à la sélection");
        lectureAuto.setSelected(true);
        CheckMenuItem boucle = new CheckMenuItem("🔁 Lecture en boucle");

        audioView.loopProperty().bind(boucle.selectedProperty());
        audioView.readyProperty().addListener((obs, avant, pret) -> {
            if (Boolean.TRUE.equals(pret) && lectureAuto.isSelected()) {
                audioView.setPlaying(true);
            }
        });

        menu.getItems().addAll(List.of(new SeparatorMenuItem(), lectureAuto, boucle));
    }
}

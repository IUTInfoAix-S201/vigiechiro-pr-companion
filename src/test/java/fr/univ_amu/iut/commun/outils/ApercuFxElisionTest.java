package fr.univ_amu.iut.commun.outils;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.framework.junit5.ApplicationExtension;

/// Garde-fou du garde-fou (#2049) : [ApercuFx] doit **refuser** d'écrire une capture où un libellé
/// enroulable a été comprimé, plutôt que de produire une image qui ment.
///
/// L'application monte ses vues dans un `ScrollPane` permanent : ce qui déborde défile. La capture n'a
/// pas ce recours et **comprime** - un `Label` en `wrapText` se rabat sur une ligne et se termine par
/// une ellipse, sans que rien ne le signale. C'est ainsi que deux consignes de M-Lot ont disparu des
/// aperçus, et qu'un défaut de mise en page a été ouvert (#2027) puis fermé comme non fondé : le FXML
/// était correct, c'était la scène qui était trop courte.
///
/// Sans ce test, désactiver le contrôle ne casserait rien de visible.
@ExtendWith(ApplicationExtension.class)
class ApercuFxElisionTest {

    /// Un texte assez long pour demander plusieurs lignes à la largeur imposée par [#scene].
    private static final String TEXTE_LONG =
            "Décochez pour économiser l'espace disque : les enregistrements sont transformés directement"
                    + " depuis la carte SD, sans en copier une archive dans le dossier « bruts ». Les séquences"
                    + " d'écoute et le journal sont produits normalement.";

    /// Exécute `capture` **sur le fil JavaFX** et rend ce qu'elle a jeté, ou `null`.
    ///
    /// `enregistrerPng` monte un `Stage` transitoire, ce que JavaFX n'autorise que sur son fil ;
    /// [ApplicationExtension] démarre le toolkit mais laisse la méthode de test sur le fil JUnit.
    private static Throwable executerSurLeFilFx(Runnable capture) throws InterruptedException {
        AtomicReference<Throwable> jetee = new AtomicReference<>();
        CountDownLatch fini = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                capture.run();
            } catch (RuntimeException | Error probleme) {
                jetee.set(probleme);
            } finally {
                fini.countDown();
            }
        });
        assertThat(fini.await(30, TimeUnit.SECONDS))
                .as("la capture doit rendre la main")
                .isTrue();
        return jetee.get();
    }

    private static Scene scene(double hauteur, Label libelle) {
        return new Scene(new VBox(libelle), 300, hauteur);
    }

    private static Label libelleEnroulable() {
        Label libelle = new Label(TEXTE_LONG);
        libelle.setWrapText(true);
        return libelle;
    }

    @Test
    @DisplayName("Une scène trop courte pour son libellé fait échouer la capture, en le nommant")
    void scene_trop_courte_refusee(@TempDir Path dossier) throws InterruptedException {
        Path fichier = dossier.resolve("apercu.png");

        Throwable refus = executerSurLeFilFx(() -> ApercuFx.enregistrerPng(scene(20, libelleEnroulable()), fichier));

        assertThat(refus).isInstanceOf(IllegalStateException.class);
        assertThat(refus.getMessage())
                .contains("Capture tronquee")
                .as("le message doit désigner le libellé fautif, sinon il faut le chercher à la main")
                .contains("Décochez pour économiser")
                .as("il doit aussi chiffrer le manque, sinon la hauteur se corrige en tâtonnant")
                .containsPattern("manque \\d+ px");
        assertThat(fichier)
                .as("une capture qui ment ne doit pas être écrite : elle deviendrait la documentation")
                .doesNotExist();
    }

    @Test
    @DisplayName("Une scène assez haute passe : le contrôle ne crie pas sur un rendu sain")
    void scene_assez_haute_acceptee(@TempDir Path dossier) throws InterruptedException {
        Path fichier = dossier.resolve("apercu.png");

        assertThat(executerSurLeFilFx(() -> ApercuFx.enregistrerPng(scene(400, libelleEnroulable()), fichier)))
                .isNull();
        assertThat(fichier).exists();
    }

    @Test
    @DisplayName("Un libellé MASQUÉ n'est pas un libellé comprimé")
    void libelle_masque_ignore(@TempDir Path dossier) throws InterruptedException {
        // Premier faux positif rencontré : le repère GPS du Diagnostic, absent quand le passage est
        // introuvable. Un nœud masqué a une hauteur nulle tout en gardant une hauteur préférée ; sans ce
        // filtre, tout libellé conditionnel ferait échouer la capture de l'état où il n'apparaît pas.
        Label masque = libelleEnroulable();
        masque.setVisible(false);
        masque.setManaged(false);
        Path fichier = dossier.resolve("apercu.png");

        assertThat(executerSurLeFilFx(() -> ApercuFx.enregistrerPng(scene(20, masque), fichier)))
                .isNull();
    }

    @Test
    @DisplayName("Un libellé NON enroulable est hors sujet : il ne demande jamais plus d'une ligne")
    void libelle_non_enroulable_ignore(@TempDir Path dossier) throws InterruptedException {
        Path fichier = dossier.resolve("apercu.png");

        assertThat(executerSurLeFilFx(() -> ApercuFx.enregistrerPng(scene(20, new Label(TEXTE_LONG)), fichier)))
                .isNull();
    }
}

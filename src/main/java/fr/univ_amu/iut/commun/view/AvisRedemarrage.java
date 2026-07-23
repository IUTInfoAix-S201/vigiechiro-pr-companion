package fr.univ_amu.iut.commun.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/// Avis « effet au prochain démarrage », partagé (#2258, harmonisation) entre les onglets de réglages
/// dont une bascule ne s'applique qu'au **prochain lancement** : « Fonctionnalités » (#1057) et
/// « Emplacements » (#1038). Chacun le construisait à sa façon ; le voici en un seul endroit.
///
/// Deux usages, un seul composant : sans bouton, c'est un **bandeau d'information** toujours visible
/// (le réglage est différé par nature) ; avec un bouton « Quitter l'application », c'est un **avis
/// d'action** qu'on montre une fois un choix appliqué, pour proposer de redémarrer tout de suite.
public final class AvisRedemarrage {

    private AvisRedemarrage() {}

    /// Construit l'avis. Si `quitter` est non nul, un bouton « Quitter l'application » le déclenche ;
    /// sinon l'avis est un simple bandeau. L'appelant décide de sa visibilité (toujours affiché, ou
    /// montré après un choix).
    public static VBox creer(String message, Runnable quitter) {
        Label texte = new Label(message);
        texte.setWrapText(true);
        texte.getStyleClass().add("avis-redemarrage-texte");

        VBox avis = new VBox(texte);
        avis.getStyleClass().add("avis-redemarrage");
        if (quitter != null) {
            Button bouton = new Button("Quitter l'application");
            bouton.getStyleClass().addAll("avis-redemarrage-quitter", "bouton-primaire");
            bouton.setOnAction(evenement -> quitter.run());
            avis.getChildren().add(bouton);
        }
        return avis;
    }
}

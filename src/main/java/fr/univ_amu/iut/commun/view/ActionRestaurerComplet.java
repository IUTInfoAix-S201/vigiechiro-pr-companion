package fr.univ_amu.iut.commun.view;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.persistence.ServiceSauvegarde;
import javafx.stage.Window;

/// Entrée ☰ **« Restaurer une sauvegarde complète »** (#1346) : le pendant de [ActionSauvegarderComplet].
///
/// Restaure la base **et** les dossiers de session. Destructif (l'état local est écrasé) : confirmé, et
/// mené sous le voile du chrome avec le libellé d'opération critique (#906).
public final class ActionRestaurerComplet implements ActionMenu {

    /// Plomberie de l'entrée : l'unique action, et la fenêtre du clic (#1405).
    private final GesteSauvegarde geste;

    @Inject
    ActionRestaurerComplet(ServiceSauvegarde service, Navigateur navigateur, OccupationChrome occupation) {
        this.geste = new GesteSauvegarde(service, navigateur, occupation);
    }

    @Override
    public GroupeMenu groupe() {
        return GroupeMenu.BASE;
    }

    /// Juste après « Restaurer une sauvegarde… » (ordre 20), même logique de lecture par paire.
    @Override
    public int ordre() {
        return 25;
    }

    @Override
    public String libelle() {
        return "↩ Restaurer une sauvegarde complète…";
    }

    @Override
    public void executer(Window proprietaire) {
        geste.sous(proprietaire).restaurerComplet();
    }

    /// Geste exposé aux tests (#1405) : `geste().actions()` porte les trois dialogues.
    GesteSauvegarde geste() {
        return geste;
    }
}

package fr.univ_amu.iut.commun.view;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.persistence.ServiceSauvegarde;
import javafx.stage.Window;

/// Entrée ☰ **« Restaurer une sauvegarde »** (#148), migrée en [ActionMenu] contribué (#930).
/// Réutilise [ActionsSauvegarde] : sélecteur de fichier, **confirmation** du remplacement destructif,
/// puis retour à l'accueil pour relire la base restaurée.
public final class ActionRestaurer implements ActionMenu {

    /// Plomberie de l'entrée : l'unique action, et la fenêtre du clic (#1405).
    private final GesteSauvegarde geste;

    @Inject
    ActionRestaurer(ServiceSauvegarde service, Navigateur navigateur, OccupationChrome occupation) {
        this.geste = new GesteSauvegarde(service, navigateur, occupation);
    }

    @Override
    public GroupeMenu groupe() {
        return GroupeMenu.BASE;
    }

    @Override
    public int ordre() {
        return 20;
    }

    @Override
    public String libelle() {
        return "↩ Restaurer une sauvegarde…";
    }

    @Override
    public void executer(Window proprietaire) {
        geste.sous(proprietaire).restaurer();
    }

    /// Geste exposé aux tests (#1405) : `geste().actions()` porte les trois dialogues.
    GesteSauvegarde geste() {
        return geste;
    }
}

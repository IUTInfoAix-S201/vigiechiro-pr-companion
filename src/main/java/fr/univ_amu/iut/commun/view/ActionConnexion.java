package fr.univ_amu.iut.commun.view;

import com.google.inject.Inject;
import java.util.Objects;
import javafx.stage.Window;

/// Entrée ☰ **« Se connecter à VigieChiro… »** (#741), migrée en [ActionMenu] (#930). Son [#libelle()]
/// reflète l'état courant (via [OuvrirConnexion#libelleMenu()]), réévalué à chaque ouverture du menu.
///
/// Contribuée par le **socle** en P2.1 ; elle sera déplacée dans le module de la feature `connexion`
/// en P2.2 (le socle cessera alors de connaître la connexion).
public final class ActionConnexion implements ActionMenu {

    private final OuvrirConnexion connexion;

    @Inject
    ActionConnexion(OuvrirConnexion connexion) {
        this.connexion = Objects.requireNonNull(connexion, "connexion");
    }

    @Override
    public GroupeMenu groupe() {
        return GroupeMenu.COMPTE;
    }

    @Override
    public int ordre() {
        return 10;
    }

    @Override
    public String libelle() {
        return connexion.libelleMenu();
    }

    @Override
    public void executer(Window proprietaire) {
        connexion.ouvrir();
    }
}

package fr.univ_amu.iut.commun.view;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.persistence.ServicePurgeOriginaux;
import java.util.Objects;
import javafx.stage.Window;

/// Entrée ☰ **« Purger les originaux importés »**, migrée en [ActionMenu] contribué (#930). Réutilise
/// [ActionsPurge] : annonce de l'espace récupérable, **confirmation** de la suppression destructive,
/// puis retour à l'accueil pour rafraîchir les volumes affichés.
public final class ActionPurger implements ActionMenu {

    private final ServicePurgeOriginaux service;
    private final Navigateur navigateur;

    @Inject
    ActionPurger(ServicePurgeOriginaux service, Navigateur navigateur) {
        this.service = Objects.requireNonNull(service, "service");
        this.navigateur = Objects.requireNonNull(navigateur, "navigateur");
    }

    @Override
    public GroupeMenu groupe() {
        return GroupeMenu.MAINTENANCE;
    }

    @Override
    public int ordre() {
        return 10;
    }

    @Override
    public String libelle() {
        return "🧹 Purger les originaux importés…";
    }

    @Override
    public void executer(Window proprietaire) {
        new ActionsPurge(service, () -> proprietaire, navigateur::afficherAccueil).purger();
    }
}

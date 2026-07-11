package fr.univ_amu.iut.commun.view;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.persistence.ServiceSauvegarde;
import java.util.Objects;
import javafx.stage.Window;

/// Entrée ☰ **« Restaurer une sauvegarde »** (#148), migrée en [ActionMenu] contribué (#930).
/// Réutilise [ActionsSauvegarde] : sélecteur de fichier, **confirmation** du remplacement destructif,
/// puis retour à l'accueil pour relire la base restaurée.
public final class ActionRestaurer implements ActionMenu {

    private final ServiceSauvegarde service;
    private final Navigateur navigateur;

    @Inject
    ActionRestaurer(ServiceSauvegarde service, Navigateur navigateur) {
        this.service = Objects.requireNonNull(service, "service");
        this.navigateur = Objects.requireNonNull(navigateur, "navigateur");
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
        new ActionsSauvegarde(service, () -> proprietaire, navigateur::afficherAccueil).restaurer();
    }
}

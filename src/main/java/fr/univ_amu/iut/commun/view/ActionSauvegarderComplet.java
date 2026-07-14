package fr.univ_amu.iut.commun.view;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.persistence.ServiceSauvegarde;
import java.util.Objects;
import javafx.stage.Window;

/// Entrée ☰ **« Sauvegarde complète (base + audio) »** (#1346), à côté de la sauvegarde de la base seule.
///
/// Le moteur existait depuis #1142, mais **aucun appelant** : ni menu, ni CLI. Or c'est la **seule
/// sauvegarde qui protège vraiment**. La base seule ne garde que les métadonnées et les observations ; si
/// l'audio disparaît du disque, la plateforme ne le rendra **pas** (un dépôt en archives n'en laisse aucun,
/// #1297) — le passage devient *archivé*, consultable mais muet.
///
/// C'est aussi le prérequis déclaré du reset guidé (#1151) : on ne repart d'une base neuve qu'après avoir
/// mis l'audio à l'abri.
public final class ActionSauvegarderComplet implements ActionMenu {

    private final ServiceSauvegarde service;
    private final Navigateur navigateur;
    private final OccupationChrome occupation;

    @Inject
    ActionSauvegarderComplet(ServiceSauvegarde service, Navigateur navigateur, OccupationChrome occupation) {
        this.service = Objects.requireNonNull(service, "service");
        this.navigateur = Objects.requireNonNull(navigateur, "navigateur");
        this.occupation = Objects.requireNonNull(occupation, "occupation");
    }

    @Override
    public GroupeMenu groupe() {
        return GroupeMenu.BASE;
    }

    /// Juste après « Sauvegarder la base… » (ordre 10) : les deux se lisent ensemble, la complète en second
    /// car elle est plus lourde et plus rare.
    @Override
    public int ordre() {
        return 15;
    }

    @Override
    public String libelle() {
        return "🗄 Sauvegarde complète (base + audio)…";
    }

    @Override
    public void executer(Window proprietaire) {
        new ActionsSauvegarde(service, occupation, () -> proprietaire, navigateur::afficherAccueil)
                .sauvegarderComplet();
    }
}

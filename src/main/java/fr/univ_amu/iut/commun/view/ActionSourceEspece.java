package fr.univ_amu.iut.commun.view;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.model.PreferenceSourceEspece;
import fr.univ_amu.iut.commun.viewmodel.ReglagesReactifs;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.stage.Window;

/// Entrée ☰ **« Fiches espèces sur Wikipédia (sinon GBIF) »** (#849), migrée en [ActionMenu] de type
/// **bascule** (#930). Liée à la MÊME Property réactive que l'onglet « Général » de l'écran Réglages
/// (clé [PreferenceSourceEspece#CLE]) : menu et onglet restent synchronisés, chaque bascule est
/// persistée (effet immédiat sur les prochaines fiches ouvertes).
public final class ActionSourceEspece implements ActionMenu {

    private final ReglagesReactifs reactifs;

    @Inject
    ActionSourceEspece(ReglagesReactifs reactifs) {
        this.reactifs = Objects.requireNonNull(reactifs, "reactifs");
    }

    @Override
    public GroupeMenu groupe() {
        return GroupeMenu.PREFERENCES;
    }

    @Override
    public int ordre() {
        return 10;
    }

    @Override
    public String libelle() {
        return "Fiches espèces sur Wikipédia (sinon GBIF)";
    }

    @Override
    public boolean estBascule() {
        return true;
    }

    @Override
    public BooleanProperty selection() {
        return reactifs.proprieteBooleen(PreferenceSourceEspece.CLE, false);
    }

    @Override
    public void executer(Window proprietaire) {
        // Bascule : l'effet passe par la case liée à selection(), pas par un clic d'action.
    }
}

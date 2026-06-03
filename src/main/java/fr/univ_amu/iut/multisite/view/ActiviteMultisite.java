package fr.univ_amu.iut.multisite.view;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.view.ActiviteAccueil;
import java.util.Objects;

/// Carte d'accueil de la feature `multisite` : ouvre l'écran « Vue multi-sites ».
///
/// Implémente le contrat du socle [ActiviteAccueil] et délègue l'ouverture à
/// [NavigationMultisite] (même feature). Enregistrée dans le `Multibinder<ActiviteAccueil>` par
/// [fr.univ_amu.iut.multisite.di.MultisiteModule]. Rang 40 : après « Mes sites » (10),
/// « Importer une nuit » (20) et « Bibliothèque de sons » (30).
public final class ActiviteMultisite implements ActiviteAccueil {

    private final NavigationMultisite navigation;

    @Inject
    public ActiviteMultisite(NavigationMultisite navigation) {
        this.navigation = Objects.requireNonNull(navigation, "navigation");
    }

    @Override
    public int ordre() {
        return 40;
    }

    @Override
    public String icone() {
        return "🗂";
    }

    @Override
    public String titre() {
        return "Vue multi-sites";
    }

    @Override
    public String description() {
        return "Tous vos passages : filtres, tri et export.";
    }

    @Override
    public void ouvrir() {
        navigation.ouvrirAccueil();
    }
}

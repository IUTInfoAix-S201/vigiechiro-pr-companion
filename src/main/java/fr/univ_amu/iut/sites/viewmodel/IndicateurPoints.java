package fr.univ_amu.iut.sites.viewmodel;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.view.IndicateurAccueil;
import fr.univ_amu.iut.sites.model.dao.PointDao;
import java.util.Objects;

/// Compteur d'accueil de la feature `sites` : nombre de points d'écoute. Enregistré dans le
/// `Multibinder<IndicateurAccueil>` par [fr.univ_amu.iut.sites.di.SitesModule].
public final class IndicateurPoints implements IndicateurAccueil {

    private final PointDao pointDao;

    @Inject
    public IndicateurPoints(PointDao pointDao) {
        this.pointDao = Objects.requireNonNull(pointDao, "pointDao");
    }

    @Override
    public int ordre() {
        return 20;
    }

    @Override
    public String icone() {
        return "📍";
    }

    @Override
    public String libelle() {
        return "Points d'écoute";
    }

    @Override
    public long valeur() {
        return pointDao.compter();
    }
}

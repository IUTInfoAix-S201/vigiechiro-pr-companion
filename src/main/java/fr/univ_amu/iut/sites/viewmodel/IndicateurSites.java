package fr.univ_amu.iut.sites.viewmodel;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.view.IndicateurAccueil;
import fr.univ_amu.iut.sites.model.dao.SiteDao;
import java.util.Objects;

/// Compteur d'accueil de la feature `sites` : nombre de sites de suivi. Enregistré dans le
/// `Multibinder<IndicateurAccueil>` par [fr.univ_amu.iut.sites.di.SitesModule].
public final class IndicateurSites implements IndicateurAccueil {

    private final SiteDao siteDao;

    @Inject
    public IndicateurSites(SiteDao siteDao) {
        this.siteDao = Objects.requireNonNull(siteDao, "siteDao");
    }

    @Override
    public int ordre() {
        return 10;
    }

    @Override
    public String icone() {
        return "🗺";
    }

    @Override
    public String libelle() {
        return "Sites";
    }

    @Override
    public long valeur() {
        return siteDao.compter();
    }
}

package fr.univ_amu.iut.validation.viewmodel;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.view.IndicateurAccueil;
import fr.univ_amu.iut.validation.model.dao.ObservationDao;
import java.util.Objects;

/// Compteur d'accueil de la feature `validation` : nombre d'observations (détections de
/// chauves-souris). Enregistré dans le `Multibinder<IndicateurAccueil>` par
/// [fr.univ_amu.iut.validation.di.ValidationModule].
public final class IndicateurObservations implements IndicateurAccueil {

    private final ObservationDao observationDao;

    @Inject
    public IndicateurObservations(ObservationDao observationDao) {
        this.observationDao = Objects.requireNonNull(observationDao, "observationDao");
    }

    @Override
    public int ordre() {
        return 40;
    }

    @Override
    public String icone() {
        return "🦇";
    }

    @Override
    public String libelle() {
        return "Observations";
    }

    @Override
    public long valeur() {
        return observationDao.compter();
    }
}

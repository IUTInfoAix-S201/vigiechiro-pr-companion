package fr.univ_amu.iut.passage.viewmodel;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.view.IndicateurAccueil;
import fr.univ_amu.iut.passage.model.dao.PassageDao;
import java.util.Objects;

/// Compteur d'accueil de la feature `passage` : nombre de passages (nuits de capture).
/// Enregistré dans le `Multibinder<IndicateurAccueil>` par [fr.univ_amu.iut.passage.di.PassageModule].
public final class IndicateurPassages implements IndicateurAccueil {

    private final PassageDao passageDao;

    @Inject
    public IndicateurPassages(PassageDao passageDao) {
        this.passageDao = Objects.requireNonNull(passageDao, "passageDao");
    }

    @Override
    public int ordre() {
        return 30;
    }

    @Override
    public String icone() {
        return "🌙";
    }

    @Override
    public String libelle() {
        return "Passages";
    }

    @Override
    public long valeur() {
        return passageDao.compter();
    }
}

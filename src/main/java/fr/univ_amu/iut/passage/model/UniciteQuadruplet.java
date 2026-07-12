package fr.univ_amu.iut.passage.model;

import fr.univ_amu.iut.commun.model.RegleMetierException;
import fr.univ_amu.iut.passage.model.dao.PassageDao;
import java.util.Objects;

/// Règle dure **R5** : le quadruplet `(point, année, n° de passage)` est unique. Pré-vérifiée via
/// [PassageDao#trouverParPointAnneePassage] (filet : contrainte `UNIQUE` du schéma), partagée entre la
/// **création** d'un passage ([ServicePassage]) et le **rattachement rétroactif**
/// ([ServiceRattachement]) qui vise un nouveau quadruplet (#1192).
final class UniciteQuadruplet {

    private final PassageDao passageDao;

    UniciteQuadruplet(PassageDao passageDao) {
        this.passageDao = Objects.requireNonNull(passageDao, "passageDao");
    }

    /// Lève si un passage occupe déjà le quadruplet visé.
    ///
    /// @throws RegleMetierException si le quadruplet existe déjà (R5)
    void exiger(Long idPoint, int annee, int numeroPassage) {
        if (passageDao
                .trouverParPointAnneePassage(idPoint, annee, numeroPassage)
                .isPresent()) {
            throw new RegleMetierException("Un passage n°"
                    + numeroPassage
                    + " existe déjà pour ce point en "
                    + annee
                    + " (le quadruplet point/année/n° de passage doit être unique).");
        }
    }
}

package fr.univ_amu.iut.validation.model;

import fr.univ_amu.iut.validation.model.dao.ObservationDao;
import fr.univ_amu.iut.validation.model.dao.ResultatsIdentificationDao;
import java.util.List;
import java.util.Objects;

/// Questions d'**ancrage plateforme** d'un passage : ses observations portent-elles leur
/// `idDonneeVigieChiro` (la cible du `PATCH /donnees/{id}/observations/{index}`, contrat #1203) ? Un
/// passage **reconstruit par CSV** (#1565) n'a aucun ancrage tant qu'il n'a pas été **réactivé** (#1571 :
/// l'audio revenu permet de le racquérir). Extrait de [ServiceValidation] (cohésion, plafond GodClass) :
/// ces deux questions forment une unité, distincte de la revue et de l'import.
public final class EtatAncragePassage {

    private final ResultatsIdentificationDao resultatsDao;
    private final ObservationDao observationDao;

    public EtatAncragePassage(ResultatsIdentificationDao resultatsDao, ObservationDao observationDao) {
        this.resultatsDao = Objects.requireNonNull(resultatsDao, "resultatsDao");
        this.observationDao = Objects.requireNonNull(observationDao, "observationDao");
    }

    /// **Au moins une** observation du passage est sans ancrage (`idDonneeVigieChiro == null`) : de quoi
    /// décider la **ré-acquisition** de l'ancrage à la réactivation (#1571). `false` si le passage n'a pas
    /// d'observations, ou si toutes sont déjà ancrées.
    public boolean manquant(Long idPassage) {
        return observations(idPassage).stream().anyMatch(observation -> observation.idDonneeVigieChiro() == null);
    }

    /// **Aucune** observation du passage n'est ancrée (toutes `idDonneeVigieChiro == null`) : l'état d'un
    /// passage reconstruit par CSV non réactivé, où **rien** n'est publiable. L'IHM grise alors
    /// proactivement « publier les corrections » (#1596). `false` dès qu'au moins une est ancrée (une
    /// publication partielle reste possible, les non ancrées étant écartées à l'envoi), ou si le passage
    /// n'a pas d'observations.
    public boolean aucun(Long idPassage) {
        List<Observation> observations = observations(idPassage);
        return !observations.isEmpty()
                && observations.stream().allMatch(observation -> observation.idDonneeVigieChiro() == null);
    }

    /// Les observations du jeu de résultats du passage, ou une liste vide s'il n'a pas encore d'import.
    private List<Observation> observations(Long idPassage) {
        Objects.requireNonNull(idPassage, "idPassage");
        return resultatsDao
                .findByPassage(idPassage)
                .map(resultats -> observationDao.findByResults(resultats.id()))
                .orElseGet(List::of);
    }
}

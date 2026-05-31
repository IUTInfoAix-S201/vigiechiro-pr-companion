package fr.univ_amu.iut.diagnostic.model;

import fr.univ_amu.iut.commun.model.Horloge;
import fr.univ_amu.iut.commun.model.RegleMetierException;
import fr.univ_amu.iut.passage.model.Passage;
import fr.univ_amu.iut.passage.model.SessionDEnregistrement;
import fr.univ_amu.iut.passage.model.dao.JournalDuCapteurDao;
import fr.univ_amu.iut.passage.model.dao.PassageDao;
import fr.univ_amu.iut.passage.model.dao.ReleveClimatiqueDao;
import fr.univ_amu.iut.passage.model.dao.SessionDao;
import fr.univ_amu.iut.sites.model.PointDEcoute;
import fr.univ_amu.iut.sites.model.dao.PointDao;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Service métier de la feature {@code diagnostic} (parcours P6, épopée E6) : lit l'état
 * matériel/technique d'une nuit <b>déjà importée</b> et l'expose pour l'onglet « Diagnostic » de la
 * fiche passage. Suit le patron du service de référence {@code ServiceSites} : pure Java testable,
 * dépendances (DAO + {@link Horloge}) reçues par constructeur avec {@code requireNonNull}, assemblé
 * par le {@code *Module} de la feature.
 *
 * <p><b>Lecture seule, sans re-parsing lourd</b> : le service ne relit ni les originaux ni le
 * journal {@code LogPR} brut ; il exploite ce qui a été persisté à l'import :
 *
 * <ul>
 *   <li>les colonnes JSON {@code sensor_log.parsed_events} / {@code detected_anomalies} via {@link
 *       AnalyseAnomalies} (R19) ;
 *   <li>la série climatique relue du fichier {@code THLog} via {@link LectureThLog} (R20) ;
 *   <li>les coordonnées GPS du point d'écoute via le {@link PointDao} de la feature {@code sites}.
 * </ul>
 *
 * <p><b>Dépendances inter-features</b> (sens autorisé, graphe acyclique) : {@code diagnostic →
 * passage.model.dao} (passage, session, journal, relevé) et {@code diagnostic → sites.model.dao}
 * (point/GPS), toutes en lecture seule. Aucune arête inverse n'est créée.
 */
public class ServiceDiagnostic {

  private final PassageDao passageDao;
  private final SessionDao sessionDao;
  private final JournalDuCapteurDao journalDao;
  private final ReleveClimatiqueDao releveDao;
  private final PointDao pointDao;
  private final Horloge horloge;

  public ServiceDiagnostic(
      PassageDao passageDao,
      SessionDao sessionDao,
      JournalDuCapteurDao journalDao,
      ReleveClimatiqueDao releveDao,
      PointDao pointDao,
      Horloge horloge) {
    this.passageDao = Objects.requireNonNull(passageDao, "passageDao");
    this.sessionDao = Objects.requireNonNull(sessionDao, "sessionDao");
    this.journalDao = Objects.requireNonNull(journalDao, "journalDao");
    this.releveDao = Objects.requireNonNull(releveDao, "releveDao");
    this.pointDao = Objects.requireNonNull(pointDao, "pointDao");
    this.horloge = Objects.requireNonNull(horloge, "horloge");
  }

  /**
   * Construit le diagnostic d'un passage importé.
   *
   * @param idPassage identifiant du passage à diagnostiquer
   * @return l'état consolidé (anomalies R19, série climatique R20, GPS, horodatage)
   * @throws RegleMetierException si le passage ou sa session d'enregistrement est introuvable
   */
  public Diagnostic diagnostiquer(Long idPassage) {
    Passage passage =
        passageDao
            .findById(idPassage)
            .orElseThrow(() -> new RegleMetierException("Passage introuvable : " + idPassage));
    SessionDEnregistrement session =
        sessionDao
            .trouverParPassage(idPassage)
            .orElseThrow(
                () ->
                    new RegleMetierException(
                        "Session d'enregistrement introuvable pour le passage " + idPassage + "."));
    Long idSession = session.id();

    // R19 : anomalies/évènements du journal (1:1 session) ; analyse vide si le journal manque.
    AnalyseAnomalies anomalies =
        journalDao
            .trouverParSession(idSession)
            .map(AnalyseAnomalies::depuisJournal)
            .orElseGet(AnalyseAnomalies::vide);

    // R20 : relevé climatique optionnel ; absence explicitement signalée, sinon série relue du
    // THLog.
    SerieClimatique climat =
        releveDao
            .trouverParSession(idSession)
            .map(
                releve ->
                    SerieClimatique.presente(LectureThLog.lire(chemin(releve.cheminFichier()))))
            .orElseGet(SerieClimatique::absente);

    // GPS depuis le point d'écoute (feature sites) ; nullable si point introuvable ou non
    // géolocalisé.
    Double latitude = null;
    Double longitude = null;
    PointDEcoute point = pointDao.findById(passage.idPoint()).orElse(null);
    if (point != null) {
      latitude = point.latitude();
      longitude = point.longitude();
    }

    return new Diagnostic(
        idPassage,
        idSession,
        passage.idEnregistreur(),
        anomalies,
        climat,
        latitude,
        longitude,
        horloge.maintenant());
  }

  private static Path chemin(String valeur) {
    return valeur == null || valeur.isBlank() ? null : Path.of(valeur);
  }
}

package fr.univ_amu.iut.diagnostic.model;

import java.time.LocalDateTime;

/**
 * État matériel/technique consolidé d'une nuit déjà importée (parcours P6, épopée E6), tel
 * qu'affiché dans l'onglet « Diagnostic » de la fiche passage.
 *
 * <p>Agrège, en <b>lecture seule</b> (aucun re-parsing lourd des originaux) :
 *
 * <ul>
 *   <li>{@link #anomalies()} : anomalies et évènements du journal du capteur (R19) ;
 *   <li>{@link #climat()} : série T°/hygrométrie prête pour un graphe, avec signalement d'absence
 *       (R20) ;
 *   <li>{@link #gpsLatitude()} / {@link #gpsLongitude()} : coordonnées du point d'écoute (feature
 *       {@code sites}), socle d'un futur encart « cohérence horaires » astronomique (P6-CA3/CA4),
 *       {@code null} si non renseignées ;
 *   <li>{@link #numeroSerieEnregistreur()} : clé de comparaison inter-passages (P6-CA5).
 * </ul>
 *
 * @param idPassage passage diagnostiqué
 * @param idSession session d'enregistrement rattachée
 * @param numeroSerieEnregistreur n° de série de l'enregistreur (comparaison inter-passages)
 * @param anomalies analyse des anomalies du journal (R19)
 * @param climat série climatique de la nuit (R20)
 * @param gpsLatitude latitude du point d'écoute, ou {@code null}
 * @param gpsLongitude longitude du point d'écoute, ou {@code null}
 * @param genereLe horodatage de calcul du diagnostic (issu de l'horloge injectée)
 */
public record Diagnostic(
    Long idPassage,
    Long idSession,
    String numeroSerieEnregistreur,
    AnalyseAnomalies anomalies,
    SerieClimatique climat,
    Double gpsLatitude,
    Double gpsLongitude,
    LocalDateTime genereLe) {

  /** {@code true} si aucun relevé climatique n'est rattaché à la session (R20, à signaler). */
  public boolean releveClimatiqueAbsent() {
    return !climat.present();
  }

  /**
   * {@code true} si les coordonnées GPS du point sont disponibles (précondition encart horaires).
   */
  public boolean coordonneesGpsDisponibles() {
    return gpsLatitude != null && gpsLongitude != null;
  }
}

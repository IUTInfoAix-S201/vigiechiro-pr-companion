package fr.univ_amu.iut.diagnostic.model;

import java.time.LocalTime;

/// Cohérence entre les **horaires d'enregistrement** d'une nuit et la **fenêtre nocturne** réelle au
/// point d'écoute (coucher → lever du soleil), calculée par [AnalyseCoherenceHoraire] (#548).
///
/// Repère de contrôle pour un protocole chiroptère : le matériel devrait démarrer après le coucher et
/// s'arrêter avant le lever. Un [#demarrageHorsNuit()] ou un [#arretHorsNuit()] signale une portion
/// d'enregistrement en plein jour (mauvais réglage d'horloge, minuterie inadaptée…).
///
/// Quand le calcul est impossible (pas de GPS, horaires manquants ou illisibles, jour/nuit polaire),
/// l'instance est [#indisponible()] : `disponible == false`, heures `null`, aucun écart signalé.
///
/// @param disponible `true` si la fenêtre nocturne a pu être calculée
/// @param coucherSoleil heure **locale** du coucher du soleil, ou `null` si indisponible
/// @param leverSoleil heure **locale** du lever du soleil, ou `null` si indisponible
/// @param demarrageHorsNuit `true` si l'enregistrement a démarré avant le coucher du soleil
/// @param arretHorsNuit `true` si l'enregistrement s'est arrêté après le lever du soleil
public record CoherenceHoraire(
        boolean disponible,
        LocalTime coucherSoleil,
        LocalTime leverSoleil,
        boolean demarrageHorsNuit,
        boolean arretHorsNuit) {

    private static final CoherenceHoraire INDISPONIBLE = new CoherenceHoraire(false, null, null, false, false);

    /// Instance signalant qu'aucune vérification n'a pu être faite (GPS/horaires absents ou latitude
    /// polaire).
    public static CoherenceHoraire indisponible() {
        return INDISPONIBLE;
    }

    /// `true` si un écart a été détecté (démarrage ou arrêt hors de la fenêtre nocturne).
    public boolean aUnEcart() {
        return disponible && (demarrageHorsNuit || arretHorsNuit);
    }
}

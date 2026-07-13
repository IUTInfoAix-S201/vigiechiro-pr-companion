package fr.univ_amu.iut.commun.api;

/// Résultat d'une **publication de correction d'observation**
/// ([ClientVigieChiro#corrigerObservation], #723) : succès, ou **détail de l'échec** (statut HTTP +
/// corps de la réponse VigieChiro, ou cause réseau). Comme pour [ResultatParticipation] : une
/// écriture refusée doit être **expliquée** à l'utilisateur (message exploitable), jamais réduite à
/// un booléen opaque.
///
/// @param echec détail de l'échec (statut + réponse, ou cause réseau), ou `null` en cas de succès
public record ResultatCorrection(String echec) {

    public static ResultatCorrection reussie() {
        return new ResultatCorrection(null);
    }

    public static ResultatCorrection echouee(String echec) {
        return new ResultatCorrection(echec);
    }

    /// `true` si la correction a été écrite côté plateforme.
    public boolean estReussie() {
        return echec == null;
    }
}

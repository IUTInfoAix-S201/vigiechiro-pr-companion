package fr.univ_amu.iut.validation.model;

/// Bilan d'un import Tadarida **tolérant** (#audio) : ce qui a été réellement importé et ce qui a été
/// écarté. Un CSV Tadarida réel référence souvent des segments dont l'audio n'a pas été conservé et des
/// taxons hors du référentiel semé : plutôt que de tout rejeter, l'import garde ce qu'il peut et rend
/// compte. La vue audio en fait un message de retour.
///
/// @param resultats jeu de résultats d'identification créé
/// @param importees nombre d'observations insérées (séquence audio présente en base)
/// @param ignoreesSequence nombre de lignes ignorées faute de séquence audio en base
/// @param taxonsHorsReferentiel nombre de taxons inconnus auto-enregistrés en souches
public record BilanImport(
        ResultatsIdentification resultats, int importees, int ignoreesSequence, int taxonsHorsReferentiel) {

    /// Identifiant du jeu de résultats créé (raccourci sur [#resultats()]).
    public Long idResultats() {
        return resultats.id();
    }
}

package fr.univ_amu.iut.audio.view;

import fr.univ_amu.iut.commun.view.Confirmateur;
import java.util.Optional;

/// Décision UI « un seul jeu par passage » avant un import, **partagée** par les deux fronts de « Sons &
/// validation » : l'import ☰ VigieChiro ([ImportVigieChiroUI]) et le glisser-déposer d'un CSV Tadarida
/// local ([ImportTadarida]). Sans jeu existant, importer directement ; avec un jeu, demander confirmation
/// du remplacement (les validations en cours seront perdues) avant de remplacer. Un seul endroit pour
/// cette décision et sa question, là où elles étaient copiées-collées entre les deux fronts.
///
/// Seule la **décision** est partagée : le geste d'import lui-même reste propre à chaque front (synchrone
/// pour un CSV local, en modale de progression annulable pour VigieChiro), de même que le rattachement et
/// le routage d'erreur, qui n'existent que côté VigieChiro. L'invariant côté base est garanti par le noyau
/// d'import (#1657) ; ici on l'anticipe pour offrir une **question** là où l'utilisateur récolterait sinon
/// une erreur métier.
final class DecisionRemplacementJeu {

    private DecisionRemplacementJeu() {}

    /// Résout le drapeau `remplacer` d'un import, en confirmant le remplacement d'un jeu existant.
    ///
    /// @param jeuExistant le passage a-t-il déjà un jeu de résultats ?
    /// @param confirmateur port de confirmation du socle (#1013)
    /// @param parQuoi ce qui remplacera le jeu, inséré dans la question (« ce nouvel import », « ceux de
    ///     VigieChiro »...)
    /// @return `Optional.of(false)` : pas de jeu, importer directement ; `Optional.of(true)` : jeu existant
    ///     et remplacement **confirmé** ; `Optional.empty()` : remplacement **refusé**, ne rien importer
    static Optional<Boolean> resoudre(boolean jeuExistant, Confirmateur confirmateur, String parQuoi) {
        if (!jeuExistant) {
            return Optional.of(false);
        }
        boolean confirme =
                confirmateur.confirmer("Des résultats Tadarida existent déjà pour ce passage. Les remplacer par "
                        + parQuoi + " ?" + " Les validations en cours sur ce passage seront perdues.");
        return confirme ? Optional.of(true) : Optional.empty();
    }
}

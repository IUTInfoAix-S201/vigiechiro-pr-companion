package fr.univ_amu.iut.passage.model;

/// Décompte des séquences d'écoute d'un passage présentes sur disque : `presentes` / `total`,
/// directement utilisable par les libellés IHM (« 4 230 / 4 806 séquences ») et le rapport
/// d'audit. La [DisponibiliteAudio] s'en dérive ([#disponibilite()]).
///
/// @param presentes nombre de séquences dont le fichier est présent sur disque (0..total)
/// @param total nombre de séquences d'écoute persistées pour le passage
public record DecompteAudio(int presentes, int total) {

    public DecompteAudio {
        if (presentes < 0 || total < 0 || presentes > total) {
            throw new IllegalArgumentException("Décompte incohérent : " + presentes + "/" + total);
        }
    }

    /// Disponibilité dérivée du décompte. Un passage sans aucune séquence persistée (`total` = 0)
    /// est [DisponibiliteAudio#ABSENTE] : il n'y a rien à écouter (passage jamais importé
    /// localement, cf. issue H #1305).
    public DisponibiliteAudio disponibilite() {
        if (presentes == 0) {
            return DisponibiliteAudio.ABSENTE;
        }
        return presentes == total ? DisponibiliteAudio.COMPLETE : DisponibiliteAudio.PARTIELLE;
    }
}

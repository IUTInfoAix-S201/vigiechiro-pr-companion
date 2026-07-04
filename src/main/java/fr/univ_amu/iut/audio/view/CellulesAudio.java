package fr.univ_amu.iut.audio.view;

import fr.univ_amu.iut.validation.model.LigneObservationAudio;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;

/// Cellules personnalisées de la table de la vue audio, sorties du [SonsValidationController] pour
/// l'alléger (cohésion, seuil PMD) : une cellule texte à **infobulle** (le nom de fichier transformé est
/// long) et la cellule d'**indicateur de commentaire** (icône « 💬 » + texte du commentaire en infobulle).
final class CellulesAudio {

    private CellulesAudio() {}

    /// `true` si la chaîne porte une valeur affichable (non nulle, non blanche).
    static boolean estRenseigne(String valeur) {
        return valeur != null && !valeur.isBlank();
    }

    /// Cellule texte qui **élide** un contenu long et en expose la valeur complète via une infobulle au
    /// survol. Ni infobulle ni décoration pour un contenu vide ou le tiret « — ».
    static TableCell<LigneObservationAudio, String> avecInfobulle() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String valeur, boolean vide) {
                super.updateItem(valeur, vide);
                if (vide || !estRenseigne(valeur) || "—".equals(valeur)) {
                    setText(vide ? null : valeur);
                    setTooltip(null);
                } else {
                    setText(valeur);
                    setTooltip(new Tooltip(valeur));
                }
            }
        };
    }

    /// Cellule de la colonne « 💬 » : affiche l'icône quand un commentaire est présent, et met le **texte
    /// complet** du commentaire (lu sur la ligne) en infobulle ; vide sinon.
    static TableCell<LigneObservationAudio, String> commentaire() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String icone, boolean vide) {
                super.updateItem(icone, vide);
                LigneObservationAudio ligne =
                        getTableRow() == null ? null : getTableRow().getItem();
                if (vide || ligne == null || !estRenseigne(ligne.commentaire())) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(icone);
                    setTooltip(new Tooltip(ligne.commentaire()));
                }
            }
        };
    }
}

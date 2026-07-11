package fr.univ_amu.iut.lot.viewmodel;

import fr.univ_amu.iut.commun.viewmodel.EtatUnite;
import fr.univ_amu.iut.commun.viewmodel.LigneSuivi;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;

/// Ligne **observable** de la table de suivi du dépôt (#820) : une archive ZIP `<préfixe>-N.zip` (le
/// numéro [LigneSuivi#numero()] la nomme) et son avancement. Spécialise le socle [LigneSuivi] avec les
/// colonnes propres au dépôt : nombre de fichiers (fixé dès la planification) et taille (estimée puis
/// réelle, avec un `~` tant que l'état n'est pas [EtatUnite#TERMINEE]).
///
/// Les mutateurs sont réservés au pilote ([SuiviLignesArchives]), à appeler sur le **fil JavaFX**.
public final class LigneArchive extends LigneSuivi {

    private final int nombreFichiers;
    private final ReadOnlyLongWrapper tailleOctets;

    /// Crée une ligne « en attente » : `tailleEstimeeOctets` est l'estimation compressée affichée avant que
    /// la taille réelle ne soit connue ([#terminer(long)]).
    LigneArchive(int numero, int nombreFichiers, long tailleEstimeeOctets) {
        super(numero);
        this.nombreFichiers = nombreFichiers;
        this.tailleOctets = new ReadOnlyLongWrapper(this, "tailleOctets", tailleEstimeeOctets);
    }

    /// Nombre de séquences que l'archive contient.
    public int nombreFichiers() {
        return nombreFichiers;
    }

    public ReadOnlyLongProperty tailleOctetsProperty() {
        return tailleOctets.getReadOnlyProperty();
    }

    /// `true` tant que la taille affichée est une **estimation** (état autre que [EtatUnite#TERMINEE]) :
    /// l'IHM préfixe alors la taille d'un `~`.
    public boolean tailleEstimee() {
        return etatProperty().get() != EtatUnite.TERMINEE;
    }

    /// L'archive est écrite : passe « terminée », fraction à 1, `tailleReelleOctets` remplace l'estimation.
    void terminer(long tailleReelleOctets) {
        terminer();
        tailleOctets.set(tailleReelleOctets);
    }
}

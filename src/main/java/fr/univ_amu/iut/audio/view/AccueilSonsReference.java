package fr.univ_amu.iut.audio.view;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import fr.univ_amu.iut.commun.view.ActiviteAccueil;
import fr.univ_amu.iut.commun.view.OuvrirAudio;
import fr.univ_amu.iut.commun.view.Prisme;
import fr.univ_amu.iut.commun.viewmodel.SourceObservations;
import java.util.Objects;

/// Carte d'accueil des **sons de référence** (#audio) : ouvre la **vue audio unifiée**
/// (« Sons & validation ») sur la source `References` du corpus de l'utilisateur courant.
///
/// Reprend l'identité de l'ancienne carte « Bibliothèque de sons » (prisme **Espèces & biodiversité**,
/// rang 20, icône volume, violet) mais vit désormais dans la feature `audio` : l'utilisateur y écoute,
/// valide / corrige et exporte ses sons de référence dans le même espace de travail audio que la
/// validation. Enregistrée dans le `Multibinder<ActiviteAccueil>` par
/// [fr.univ_amu.iut.audio.di.AudioModule] ; ouvre la vue via le contrat socle [OuvrirAudio].
public final class AccueilSonsReference implements ActiviteAccueil {

    private final OuvrirAudio ouvrirAudio;
    private final Provider<String> idUtilisateur;

    @Inject
    public AccueilSonsReference(
            OuvrirAudio ouvrirAudio, @Named("idUtilisateurCourant") Provider<String> idUtilisateur) {
        this.ouvrirAudio = Objects.requireNonNull(ouvrirAudio, "ouvrirAudio");
        this.idUtilisateur = Objects.requireNonNull(idUtilisateur, "idUtilisateur");
    }

    @Override
    public Prisme prisme() {
        return Prisme.ESPECES_BIODIVERSITE;
    }

    @Override
    public int ordre() {
        return 20;
    }

    @Override
    public String iconeLiteral() {
        return "fas-volume-up";
    }

    @Override
    public String couleur() {
        return "#8e44ad";
    }

    @Override
    public String titre() {
        return "Sons de référence";
    }

    @Override
    public String description() {
        return "Vos sons de référence à écouter, valider et exporter.";
    }

    @Override
    public void ouvrir() {
        // Identifiant résolu paresseusement (requête en base via SitesModule) : seulement à l'ouverture,
        // pas à la construction de l'accueil - sinon le rendu des cartes exigerait un schéma migré.
        ouvrirAudio.ouvrir(new SourceObservations.References(idUtilisateur.get()));
    }
}

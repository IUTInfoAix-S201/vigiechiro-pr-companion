package fr.univ_amu.iut.audio.di;

import com.google.inject.Provides;
import com.google.inject.multibindings.OptionalBinder;
import fr.univ_amu.iut.audio.view.AccueilSonsReference;
import fr.univ_amu.iut.audio.view.NavigationAudio;
import fr.univ_amu.iut.audio.viewmodel.AudioViewModel;
import fr.univ_amu.iut.audio.viewmodel.ImportVigieChiroViewModel;
import fr.univ_amu.iut.audio.viewmodel.OngletReglagesAudio;
import fr.univ_amu.iut.bibliotheque.model.ServiceBibliotheque;
import fr.univ_amu.iut.commun.di.ModuleDeFeature;
import fr.univ_amu.iut.commun.view.OuvrirAudio;
import fr.univ_amu.iut.validation.model.ImportVigieChiro;
import fr.univ_amu.iut.validation.model.MarquageDouteux;
import fr.univ_amu.iut.validation.model.RevueEnLot;
import fr.univ_amu.iut.validation.model.ServiceValidation;
import fr.univ_amu.iut.validation.model.ValidationManuelle;
import java.util.Optional;

/// Module Guice de la feature `audio` (vue audio unifiée « Sons & validation »).
///
/// Lie le contrat socle [OuvrirAudio] à son implémentation [NavigationAudio] (que les features
/// alimentant la vue injectent sans dépendre de `audio.view`) et fournit le [AudioViewModel], assemblé
/// sur les **services** de `validation` ([ServiceValidation]) et `bibliotheque` ([ServiceBibliotheque]).
/// La feature `audio` est un **puits** (aucun retour vers elle) : le graphe de slices reste acyclique
/// (cf. `ArchitectureTest`).
///
/// **Intégration** : installé dans `RacineInjecteur` après `ValidationModule` et `BibliothequeModule`
/// (qui fournissent ses services). Enregistre la carte d'accueil [AccueilSonsReference] (« Sons de
/// référence ») dans le `Multibinder<ActiviteAccueil>` du socle : elle ouvre la vue audio sur la source
/// `References` (elle remplace l'ancienne carte « Bibliothèque de sons »).
public class AudioModule extends ModuleDeFeature {

    @Override
    protected void configure() {
        bind(OuvrirAudio.class).to(NavigationAudio.class);
        activite(AccueilSonsReference.class);
        // Onglet « Audio » de l'écran Réglages (#1006) : préférences de lecture (auto-lecture, boucle),
        // partagées avec les options du menu ☰ de la vue audio.
        ongletReglages(OngletReglagesAudio.class);
        // Import VigieChiro (axe 4.2) en liaison **optionnelle** : déclaré à vide ici pour que les injecteurs
        // partiels de capture (sans `connexion`, donc sans client HTTP) résolvent `Optional<ImportVigieChiro>`
        // à vide. La liaison réelle est posée par `ImportVigieChiroModule` (injecteur applicatif complet).
        OptionalBinder.newOptionalBinder(binder(), ImportVigieChiro.class);
    }

    // ViewModel non-singleton (cf. analyse / multisite) : un VM frais par chargement d'écran, pour éviter
    // que des listeners de vues fermées restent accrochés.
    @Provides
    AudioViewModel fournirAudioViewModel(
            ServiceValidation validation,
            ValidationManuelle validationManuelle,
            MarquageDouteux marquageDouteux,
            RevueEnLot revueEnLot,
            ServiceBibliotheque bibliotheque) {
        return new AudioViewModel(validation, validationManuelle, marquageDouteux, revueEnLot, bibliotheque);
    }

    /// ViewModel dédié de l'**import VigieChiro** (axe 4.2), séparé de [AudioViewModel] (concern distinct, et
    /// pour ne pas alourdir ce VM déjà volumineux). `importVigieChiro` est vide dans les injecteurs partiels
    /// de capture, présent dans l'application complète (cf. `ImportVigieChiroModule`).
    @Provides
    ImportVigieChiroViewModel fournirImportVigieChiroViewModel(Optional<ImportVigieChiro> importVigieChiro) {
        return new ImportVigieChiroViewModel(importVigieChiro);
    }
}

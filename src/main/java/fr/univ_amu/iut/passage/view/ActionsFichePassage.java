package fr.univ_amu.iut.passage.view;

import fr.univ_amu.iut.commun.model.CompteurValidations;
import fr.univ_amu.iut.commun.model.RegleMetierException;
import fr.univ_amu.iut.commun.view.ConfirmateurModifiable;
import fr.univ_amu.iut.commun.view.NiveauNotification;
import fr.univ_amu.iut.commun.view.NotificateurModifiable;
import fr.univ_amu.iut.passage.viewmodel.PassageViewModel;
import java.util.Objects;
import java.util.function.LongSupplier;

/// Actions **destructives ou correctives** de la fiche d'un passage : supprimer, annuler le dépôt,
/// purger les originaux. Chacune confirme, délègue au [PassageViewModel], puis recharge l'écran (ou
/// revient à l'accueil pour la suppression) ; un refus métier est présenté sans quitter l'écran.
///
/// Extraites de [PassageController] pour qu'il reste du **pur câblage** (PMD GodClass), au même
/// titre que [ActionArchivage] (#1300) et [ActionReactivation] (#1302).
///
/// Le refus passe par le port `Notificateur` (#1405) et non plus par un `Alert` en dur : c'est ce qui
/// rend ces trois gestes **cliquables dans un test**. Jusqu'ici on ne vérifiait que la **présence** de
/// leurs boutons, jamais leur effet - alors que la suppression emporte la nuit entière et, avec elle,
/// les validations de l'observateur.
final class ActionsFichePassage {

    private final PassageViewModel viewModel;
    private final ConfirmateurModifiable confirmateur;
    private final NotificateurModifiable notificateur;
    private final CompteurValidations compteurValidations;
    private final LongSupplier idPassage;
    private final Runnable recharger;
    private final Runnable retourAccueil;

    /// @param viewModel ViewModel de M-Passage
    /// @param confirmateur porteur de confirmation partagé de l'écran (stub déterministe en test, #1013)
    /// @param notificateur porteur de compte rendu partagé de l'écran (double capturant en test, #1405)
    /// @param compteurValidations validations menacées par une suppression (avertissement, #786)
    /// @param idPassage identifiant du passage affiché (évalué à l'action : l'écran peut avoir changé)
    /// @param recharger rejeu de l'ouverture de l'écran après une action non destructive
    /// @param retourAccueil navigation après suppression (le passage n'existe plus)
    ActionsFichePassage(
            PassageViewModel viewModel,
            ConfirmateurModifiable confirmateur,
            NotificateurModifiable notificateur,
            CompteurValidations compteurValidations,
            LongSupplier idPassage,
            Runnable recharger,
            Runnable retourAccueil) {
        this.viewModel = Objects.requireNonNull(viewModel, "viewModel");
        this.confirmateur = Objects.requireNonNull(confirmateur, "confirmateur");
        this.notificateur = Objects.requireNonNull(notificateur, "notificateur");
        this.compteurValidations = Objects.requireNonNull(compteurValidations, "compteurValidations");
        this.idPassage = Objects.requireNonNull(idPassage, "idPassage");
        this.recharger = Objects.requireNonNull(recharger, "recharger");
        this.retourAccueil = Objects.requireNonNull(retourAccueil, "retourAccueil");
    }

    /// « Supprimer » : après confirmation (avec l'avertissement des validations menacées), supprime le
    /// passage et toute sa nuit, puis revient à l'accueil.
    void supprimer() {
        if (!confirmateur.confirmer("Supprimer définitivement ce passage et toute sa nuit (séquences, relevés) ?"
                + alerteValidationsMenacees())) {
            return;
        }
        try {
            viewModel.supprimer();
            retourAccueil.run();
        } catch (RegleMetierException refus) {
            signalerRefus("Suppression impossible", refus.getMessage());
        }
    }

    /// « Annuler le dépôt » : ramène le passage de « Déposé » à « Prêt à déposer » (les validations
    /// Tadarida déjà saisies sont **conservées**) puis recharge l'écran. Un passage non déposé est
    /// refusé par le service.
    void annulerDepot() {
        if (!confirmateur.confirmer("Annuler le dépôt de ce passage et le ramener à « Prêt à déposer » ? "
                + "Les validations Tadarida déjà saisies sont conservées.")) {
            return;
        }
        try {
            viewModel.annulerDepot();
            recharger.run();
        } catch (RegleMetierException refus) {
            signalerRefus("Annulation impossible", refus.getMessage());
        }
    }

    /// « Purger les originaux » : supprime les `bruts/` de cette nuit pour récupérer l'espace disque,
    /// puis recharge l'écran (le volume bruts tombe à 0). Les séquences transformées, la validation
    /// Tadarida et le dépôt sont **conservés** (ils n'utilisent pas les originaux).
    void purgerOriginaux() {
        if (!confirmateur.confirmer("Supprimer les enregistrements originaux (bruts) de cette nuit pour libérer de "
                + "l'espace disque ? Les séquences d'écoute, la validation et le dépôt sont conservés ; "
                + "cette suppression est définitive.")) {
            return;
        }
        try {
            viewModel.purgerOriginaux();
            recharger.run();
        } catch (RuntimeException echec) {
            signalerRefus("Purge impossible", echec.getMessage());
        }
    }

    /// Complément d'avertissement pour la confirmation de suppression : si le passage porte des
    /// validations observateur (taxon corrigé, référence, commentaire), elles seront **définitivement
    /// perdues** avec la cascade. Chaîne vide sinon. Contrairement à une ré-importation de CSV, la
    /// suppression ne permet aucune préservation.
    private String alerteValidationsMenacees() {
        int menacees = compteurValidations.menaceesPourPassage(idPassage.getAsLong());
        if (menacees == 0) {
            return "";
        }
        return "\n\n⚠ " + menacees
                + " validation(s) Tadarida (correction, référence, commentaire) seront définitivement perdues.";
    }

    /// L'action n'a pas eu lieu : l'écran ne bouge pas, et l'utilisateur sait pourquoi.
    private void signalerRefus(String entete, String message) {
        notificateur.notifier(NiveauNotification.AVERTISSEMENT, entete, message);
    }
}

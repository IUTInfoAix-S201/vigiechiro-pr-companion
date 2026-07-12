package fr.univ_amu.iut.lot.model;

import fr.univ_amu.iut.commun.api.ClientVigieChiro;
import fr.univ_amu.iut.commun.api.DonneeVigieChiro;
import fr.univ_amu.iut.commun.model.RegleMetierException;
import fr.univ_amu.iut.lot.model.dao.DepotUniteDao;
import fr.univ_amu.iut.passage.model.SynchronisationParticipation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/// **Vérification a posteriori d’un dépôt** (#1132) : confronte le plan local (`depot_unite`) à ce
/// que la plateforme a **réellement traité**, via deux sources en lecture seule :
///
///  - le **journal de traitement** de la participation ([ClientVigieChiro#journalTraitement]) — il
///    nomme chaque archive extraite (avec inventaire) et chaque WAV passé à Tadarida ; c’est la
///    seule source capable de vérifier un dépôt en **ZIP** ;
///  - les **titres des `donnees`** Tadarida (nom du WAV **sans extension**), en recoupement.
///
/// Limite (comme la réconciliation #1046) : ces traces n’existent qu’**après** le traitement
/// serveur — un fichier téléversé mais pas encore traité est « manquant » jusqu’au prochain passage
/// du pipeline. La vérification ne modifie **rien** (ni localement, ni côté plateforme).
public final class VerificationDepot {

    /// Noms de fichiers cités dans le journal (`…wav` de TadaridaD, `…zip` des extractions).
    private static final Pattern NOM_DE_FICHIER = Pattern.compile("[\\w.-]+\\.(?:wav|zip)");

    private final SynchronisationParticipation participations;
    private final ClientVigieChiro client;
    private final DepotUniteDao depotUnites;

    public VerificationDepot(
            SynchronisationParticipation participations, ClientVigieChiro client, DepotUniteDao depotUnites) {
        this.participations = Objects.requireNonNull(participations, "participations");
        this.client = Objects.requireNonNull(client, "client");
        this.depotUnites = Objects.requireNonNull(depotUnites, "depotUnites");
    }

    /// Vérifie le dépôt du passage `idPassage` et renvoie le bilan (rien n’est modifié).
    ///
    /// @throws RegleMetierException si le passage n’est pas lié à une participation, ou si aucun
    ///     plan de dépôt local n’existe (dépôt fait hors de l’application)
    public BilanVerification verifier(Long idPassage) {
        Objects.requireNonNull(idPassage, "idPassage");
        String participationId = participations
                .participationDe(idPassage)
                .orElseThrow(() -> new RegleMetierException(
                        "Ce passage n’est lié à aucune participation VigieChiro : rien à vérifier"
                                + " (déposez-le d’abord)."));
        List<DepotUnite> unites = depotUnites.parPassage(idPassage);
        if (unites.isEmpty()) {
            throw new RegleMetierException("Aucun plan de dépôt local pour ce passage (dépôt fait hors de"
                    + " l’application ?) : rien à comparer.");
        }

        Optional<String> journal = client.journalTraitement(participationId);
        Set<String> nomsDuJournal =
                journal.map(VerificationDepot::nomsDeFichiers).orElseGet(Set::of);
        Set<String> titresDonnees = new HashSet<>();
        for (DonneeVigieChiro donnee : client.donnees(participationId)) {
            titresDonnees.add(donnee.titre());
        }

        List<String> retrouvees = new ArrayList<>();
        List<String> manquantes = new ArrayList<>();
        for (DepotUnite unite : unites) {
            String nom = unite.identifiantUnite();
            boolean traitee = nomsDuJournal.contains(nom) || titresDonnees.contains(sansExtension(nom));
            (traitee ? retrouvees : manquantes).add(nom);
        }
        return new BilanVerification(
                participationId,
                journal.isPresent(),
                titresDonnees.size(),
                List.copyOf(retrouvees),
                List.copyOf(manquantes));
    }

    private static Set<String> nomsDeFichiers(String journal) {
        Set<String> noms = new HashSet<>();
        Matcher nom = NOM_DE_FICHIER.matcher(journal);
        while (nom.find()) {
            noms.add(nom.group());
        }
        return noms;
    }

    private static String sansExtension(String nom) {
        int point = nom.lastIndexOf('.');
        return point <= 0 ? nom : nom.substring(0, point);
    }
}

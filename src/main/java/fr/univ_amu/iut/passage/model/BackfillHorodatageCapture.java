package fr.univ_amu.iut.passage.model;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.model.Prefixe;
import fr.univ_amu.iut.commun.persistence.UniteDeTravail;
import fr.univ_amu.iut.passage.model.dao.SequenceDao;
import java.time.LocalDateTime;
import java.util.Optional;

/// Backfill **applicatif** de l'horodatage de capture (#530) : renseigne `recorded_at` des séquences déjà
/// importées avant la colonne (migration V09) en **re-parsant leur nom de fichier** (`_AAAAMMJJ_HHMMSS`),
/// plus fiable qu'un `substr` SQLite. Déclenché au démarrage après la migration.
///
/// **Idempotent** : ne cible que les séquences **sans** horodatage ([SequenceDao#sansHorodatage()]) et ne
/// remplit que celles dont le nom est effectivement horodaté (les noms non standard / de test restent à
/// `null`, sans re-traitement coûteux au-delà d'une requête).
///
/// **En une transaction.** Le remplissage est pur (parsing de nom + `UPDATE`), sans lecture de disque :
/// rien ne justifie d'auto-commiter ligne par ligne, c'est-à-dire de payer un `fsync` par séquence
/// pendant que l'utilisateur attend sa fenêtre. L'idempotence rend l'annulation sans conséquence : un
/// démarrage interrompu ne laisse rien à moitié, et le suivant recommence.
///
/// C'est l'inverse du choix fait pour [BackfillEmpreintes], et pour une bonne raison : celui-là **lit le
/// disque** pour chaque séquence, et sa reprise ligne par ligne vaut mieux qu'une transaction tenue
/// pendant des minutes de hachage.
public final class BackfillHorodatageCapture {

    private final SequenceDao sequenceDao;
    private final UniteDeTravail uniteDeTravail;

    @Inject
    public BackfillHorodatageCapture(SequenceDao sequenceDao, UniteDeTravail uniteDeTravail) {
        this.sequenceDao = sequenceDao;
        this.uniteDeTravail = uniteDeTravail;
    }

    /// Remplit l'horodatage des séquences qui n'en ont pas et dont le nom est horodaté. Retourne le nombre
    /// de séquences effectivement renseignées.
    public int remplir() {
        int[] remplis = {0};
        uniteDeTravail.executer(connexion -> {
            for (SequenceDEcoute sequence : sequenceDao.sansHorodatage()) {
                Optional<LocalDateTime> horodatage = Prefixe.horodatageDe(sequence.nomFichier());
                if (horodatage.isPresent()) {
                    sequenceDao.majHorodatage(connexion, sequence.id(), horodatage.orElseThrow());
                    remplis[0]++;
                }
            }
        });
        return remplis[0];
    }
}

package fr.univ_amu.iut.passage.model;

import fr.univ_amu.iut.commun.model.PresenceFichiers;
import fr.univ_amu.iut.commun.model.PresenceFichiers.Presence;
import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.passage.model.dao.SequenceDao;
import fr.univ_amu.iut.passage.model.dao.SessionDao;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/// Disponibilité de l'audio local d'un passage (#1298, EPIC #1297) : calcule le [DecompteAudio]
/// des séquences présentes sur disque, en balayage **groupé** ([PresenceFichiers] : un accès
/// disque par dossier, pas par séquence) et **mis en cache** par passage. C'est le point de calcul
/// unique de la [DisponibiliteAudio], consommé par l'écoute, l'audit et l'archivage.
///
/// **Invalidation.** Le jeu de séquences d'un passage est immuable en base après l'import :
/// l'import crée toujours un passage à identifiant **neuf** (l'écrasement supprime l'ancien
/// passage et en insère un nouveau), donc aucune entrée de cache ne peut se périmer côté base.
/// Seul le **disque** fait varier le décompte : les écrivains connus (archivage #1300,
/// réactivation #1302) doivent appeler [#invalider] après avoir touché les fichiers d'un passage ;
/// [#invaliderTout] resynchronise le cache entier (par exemple avant un audit complet, ou après
/// une intervention manuelle de l'utilisateur dans le workspace).
public class ServiceDisponibiliteAudio {

    private final SessionDao sessionDao;
    private final SequenceDao sequenceDao;
    private final PresenceFichiers presenceFichiers;
    private final Map<Long, DecompteAudio> cache = new ConcurrentHashMap<>();

    public ServiceDisponibiliteAudio(SessionDao sessionDao, SequenceDao sequenceDao, Workspace workspace) {
        this(sessionDao, sequenceDao, new PresenceFichiers(workspace));
    }

    /// Variante à [PresenceFichiers] injecté (tests : balayeur à compteur d'accès disque).
    ServiceDisponibiliteAudio(SessionDao sessionDao, SequenceDao sequenceDao, PresenceFichiers presenceFichiers) {
        this.sessionDao = Objects.requireNonNull(sessionDao, "sessionDao");
        this.sequenceDao = Objects.requireNonNull(sequenceDao, "sequenceDao");
        this.presenceFichiers = Objects.requireNonNull(presenceFichiers, "presenceFichiers");
    }

    /// Décompte présentes / total des séquences du passage, servi depuis le cache s'il y est.
    public DecompteAudio decompte(Long idPassage) {
        Objects.requireNonNull(idPassage, "idPassage");
        return cache.computeIfAbsent(idPassage, this::calculer);
    }

    /// Disponibilité observée de l'audio du passage (raccourci sur [#decompte]).
    public DisponibiliteAudio disponibilite(Long idPassage) {
        return decompte(idPassage).disponibilite();
    }

    /// Oublie le décompte d'un passage : à appeler après toute opération qui change ses fichiers
    /// sur disque (archivage, réactivation). Le prochain accès recalcule.
    public void invalider(Long idPassage) {
        cache.remove(idPassage);
    }

    /// Vide le cache entier : le prochain accès recalcule passage par passage.
    public void invaliderTout() {
        cache.clear();
    }

    private DecompteAudio calculer(Long idPassage) {
        Optional<SessionDEnregistrement> session = sessionDao.trouverParPassage(idPassage);
        if (session.isEmpty()) {
            return new DecompteAudio(0, 0);
        }
        List<SequenceDEcoute> sequences =
                sequenceDao.findBySession(session.get().id());
        List<String> chemins =
                sequences.stream().map(SequenceDEcoute::cheminFichier).toList();
        Map<String, Presence> presences = presenceFichiers.evaluer(chemins);
        int presentes = (int) chemins.stream()
                .filter(chemin -> presences.get(chemin) == Presence.PRESENTE)
                .count();
        return new DecompteAudio(presentes, sequences.size());
    }
}

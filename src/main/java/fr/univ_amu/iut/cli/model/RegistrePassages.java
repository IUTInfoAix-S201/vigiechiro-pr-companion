package fr.univ_amu.iut.cli.model;

import fr.univ_amu.iut.commun.model.StatutWorkflow;
import fr.univ_amu.iut.commun.model.Verdict;
import fr.univ_amu.iut.passage.model.Passage;
import fr.univ_amu.iut.passage.model.dao.PassageDao;
import fr.univ_amu.iut.sites.model.PointDEcoute;
import fr.univ_amu.iut.sites.model.Site;
import fr.univ_amu.iut.sites.model.dao.PointDao;
import fr.univ_amu.iut.sites.model.dao.SiteDao;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/// Lecture transverse pour la commande `lister-passages` (parcours P5, navigation
/// multi-sites).
///
/// Service de lecture pure (aucune écriture, aucun JavaFX). Il **orchestre des DAO de plusieurs
/// features** (`passage` et `sites`) pour reconstituer, pour chaque passage, le
/// contexte « carré / point » sans charger de vue : exactement le type de dépendance
/// inter-feature `cli → <feature>.model` autorisé par la règle ArchUnit assouplie (jamais vers un
/// `view`/`viewmodel`). Le graphe reste acyclique : `cli` est un puits, aucune feature ne
/// dépend de lui.
///
/// Pour éviter un effet N+1, les points et les sites sont chargés une seule fois et indexés par
/// identifiant ; les passages orphelins (point/site introuvable) restent listés avec un libellé
/// `"?"` plutôt que d'être masqués.
public final class RegistrePassages {

  private final PassageDao passageDao;
  private final PointDao pointDao;
  private final SiteDao siteDao;

  public RegistrePassages(PassageDao passageDao, PointDao pointDao, SiteDao siteDao) {
    this.passageDao = Objects.requireNonNull(passageDao, "passageDao");
    this.pointDao = Objects.requireNonNull(pointDao, "pointDao");
    this.siteDao = Objects.requireNonNull(siteDao, "siteDao");
  }

  /// Tous les passages enregistrés, enrichis du contexte site/point, triés pour un affichage
  /// stable.
  public List<LignePassage> lister() {
    Map<Long, PointDEcoute> points =
        pointDao.findAll().stream()
            .collect(Collectors.toMap(PointDEcoute::id, Function.identity()));
    Map<Long, Site> sites =
        siteDao.findAll().stream().collect(Collectors.toMap(Site::id, Function.identity()));

    return passageDao.findAll().stream()
        .map(passage -> versLigne(passage, points, sites))
        .sorted(
            Comparator.comparing(LignePassage::carre)
                .thenComparing(LignePassage::codePoint)
                .thenComparingInt(LignePassage::annee)
                .thenComparingInt(LignePassage::numeroPassage))
        .toList();
  }

  private static LignePassage versLigne(
      Passage passage, Map<Long, PointDEcoute> points, Map<Long, Site> sites) {
    PointDEcoute point = points.get(passage.idPoint());
    Site site = point == null ? null : sites.get(point.idSite());
    return new LignePassage(
        passage.id(),
        site == null ? "?" : site.numeroCarre(),
        point == null ? "?" : point.code(),
        passage.annee(),
        passage.numeroPassage(),
        passage.statutWorkflow(),
        passage.verdictVerification());
  }

  /// Ligne d'affichage d'un passage (objet de présentation, pas une entité persistée).
  ///
  /// @param idPassage identifiant technique du passage
  /// @param carre numéro de carré du site (ou `"?"` si introuvable)
  /// @param codePoint code du point d'écoute (ou `"?"` si introuvable)
  /// @param annee année du passage
  /// @param numeroPassage numéro de passage dans l'année
  /// @param statut statut du workflow
  /// @param verdict verdict de vérification (`null` tant que non vérifié)
  public record LignePassage(
      Long idPassage,
      String carre,
      String codePoint,
      int annee,
      int numeroPassage,
      StatutWorkflow statut,
      Verdict verdict) {}
}

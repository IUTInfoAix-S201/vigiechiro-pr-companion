package fr.univ_amu.iut.commun.api;

import java.util.List;

/// Une *donnée* VigieChiro = un **fichier** audio et ses observations Tadarida, renvoyé par
/// `GET /participations/#id/donnees` (#719, axe 4.2). Le `titre` est le nom de fichier (sans extension,
/// ex. `"Car130711-2026-Pass1-Z41-PaRec..._20260703_220529_000"`), qui sert de clé de rattachement à la
/// séquence d'écoute locale de même nom.
///
/// L'`id` (`_id` Eve) rend la donnée **adressable** : c'est l'ancrage serveur d'une correction
/// d'observation (`PATCH /donnees/#id/observations/#index`, spike #1203). Les observations, elles,
/// n'ont pas d'identifiant propre : ce sont des sous-documents positionnels du tableau `observations`.
///
/// @param id identifiant Eve de la donnée (`_id`), `null` si absent de la réponse
/// @param titre nom de fichier de la donnée (clé de rattachement à la séquence locale)
/// @param observations détections Tadarida du fichier (éventuellement vide)
public record DonneeVigieChiro(String id, String titre, List<ObservationVigieChiro> observations) {}

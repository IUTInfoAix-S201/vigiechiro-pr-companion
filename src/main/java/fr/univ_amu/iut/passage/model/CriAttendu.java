package fr.univ_amu.iut.passage.model;

/// Un cri **attendu** dans une séquence d'écoute : la trace acoustique d'une observation (instants
/// et fréquence médiane, tels que Tadarida les a mesurés). Sert de preuve d'identité à la
/// vérification acoustique (#1309) : si les cris attendus ne sont pas dans le fichier candidat, ce
/// n'est pas le bon fichier.
///
/// Porté en **données brutes** plutôt qu'en entité `Observation` (feature validation) pour que le
/// socle de vérification reste dans `passage` sans dépendre d'une feature voisine : l'appelant
/// (réactivation, #1302) fait la projection.
///
/// @param debutSecondes début du cri, en secondes **réelles** dans la séquence (`start_time_s`)
/// @param finSecondes fin du cri, en secondes réelles (`end_time_s`)
/// @param frequenceMedianeHz fréquence médiane du cri en Hz **réels** (`median_freq_khz` × 1000)
public record CriAttendu(double debutSecondes, double finSecondes, double frequenceMedianeHz) {}

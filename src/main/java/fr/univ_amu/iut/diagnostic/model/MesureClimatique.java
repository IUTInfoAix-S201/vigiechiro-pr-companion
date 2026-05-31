package fr.univ_amu.iut.diagnostic.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Une mesure ponctuelle du relevé climatique (C10, R20) : un point de la série temporelle
 * T°/hygrométrie de la sonde embarquée, prêt à alimenter un graphe (P6-CA1).
 *
 * <p>Le firmware Teensy écrit ~1 mesure / 600 s dans le fichier {@code PaRecPR<sn>_THLog.csv}
 * (séparateur tabulation). Le modèle transporte des valeurs <b>typées</b> (et non du texte brut)
 * pour être directement traçables : {@code date}/{@code heure} pour l'axe des abscisses, {@code
 * temperatureCelsius}/{@code humiditePourcent} pour les ordonnées.
 *
 * @param date jour de la mesure
 * @param heure heure de la mesure
 * @param temperatureCelsius température en degrés Celsius
 * @param humiditePourcent humidité relative en pourcentage
 */
public record MesureClimatique(
    LocalDate date, LocalTime heure, double temperatureCelsius, int humiditePourcent) {}

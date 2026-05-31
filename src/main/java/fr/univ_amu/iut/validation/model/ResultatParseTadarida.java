package fr.univ_amu.iut.validation.model;

import java.util.List;

/**
 * Résultat du parsing d'un fichier de résultats Tadarida : le {@link FormatTadarida} détecté et la
 * liste des {@link LigneObservation} (dans l'ordre du fichier, donc déterministe).
 *
 * @param format format détecté ({@link FormatTadarida#BRUT} ou {@link FormatTadarida#VU})
 * @param lignes lignes d'observation parsées, dans l'ordre du fichier
 */
public record ResultatParseTadarida(FormatTadarida format, List<LigneObservation> lignes) {

  /** Nombre d'observations parsées. */
  public int taille() {
    return lignes.size();
  }
}

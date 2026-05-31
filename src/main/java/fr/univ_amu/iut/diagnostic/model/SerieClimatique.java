package fr.univ_amu.iut.diagnostic.model;

import java.util.List;
import java.util.Objects;

/**
 * Série climatique d'une nuit, prête pour un graphe (P6-CA1), avec gestion explicite de l'absence
 * de relevé (R20).
 *
 * <p><b>R20 (relevé optionnel)</b> : la sonde T°/hygrométrie peut être absente ou défaillante.
 * L'onglet diagnostic doit <b>signaler</b> cette absence plutôt que la masquer. Le drapeau {@link
 * #present()} distingue donc deux cas :
 *
 * <ul>
 *   <li>{@link #absente()} : aucun relevé climatique rattaché à la session ({@code present ==
 *       false} ⇒ l'IHM affiche « relevé climatique absent ») ;
 *   <li>{@link #presente(List)} : un relevé existe ({@code present == true}) ; la liste de mesures
 *       peut malgré tout être vide si le fichier a été perdu (journal tronqué, R19).
 * </ul>
 */
public final class SerieClimatique {

  private final boolean present;
  private final List<MesureClimatique> mesures;

  private SerieClimatique(boolean present, List<MesureClimatique> mesures) {
    this.present = present;
    this.mesures = List.copyOf(Objects.requireNonNull(mesures, "mesures"));
  }

  /** Aucun relevé climatique pour la session (R20, absence à signaler). */
  public static SerieClimatique absente() {
    return new SerieClimatique(false, List.of());
  }

  /** Un relevé existe ; {@code mesures} porte la série lue (éventuellement vide). */
  public static SerieClimatique presente(List<MesureClimatique> mesures) {
    return new SerieClimatique(true, mesures);
  }

  /** {@code true} si un relevé climatique est rattaché à la session (R20). */
  public boolean present() {
    return present;
  }

  /** La série de mesures (immuable, dans l'ordre chronologique du fichier). */
  public List<MesureClimatique> mesures() {
    return mesures;
  }

  /** Nombre de mesures de la série. */
  public int nombreMesures() {
    return mesures.size();
  }
}

package fr.univ_amu.iut.lot.model;

import fr.univ_amu.iut.passage.model.SequenceDEcoute;
import java.util.List;
import java.util.Objects;

/// Récapitulatif d'un lot prêt à déposer sur Vigie-Chiro (parcours P4, maquette M-Lot,
/// story E4.S2).
///
/// Produit par [ServiceLot#preparerLot(Long)] une fois les
/// [vérifications de cohérence][VerificationCoherence] passées. C'est un
/// **objet de présentation** (pas une entité persistée) : il transporte vers l'IHM ce qui
/// sera téléversé manuellement (séquences d'écoute, volume, chemin du dossier sur le
/// disque). L'application ne dialogue jamais avec le portail (R8 implicite) : le dépôt
/// reste manuel.
///
/// @param idPassage identifiant du passage dont le lot est préparé
/// @param cheminDossier chemin sur disque du sous-dossier de session prêt à téléverser (R22)
/// @param sequences séquences d'écoute transformées à déposer (copie défensive immuable)
/// @param volumeSequencesOctets volume total des séquences en octets (`null` si non calculé)
public record Lot(
    Long idPassage,
    String cheminDossier,
    List<SequenceDEcoute> sequences,
    Long volumeSequencesOctets) {

  public Lot {
    Objects.requireNonNull(idPassage, "idPassage");
    sequences = List.copyOf(sequences);
  }

  /// Nombre de séquences d'écoute à déposer (affiché dans le récapitulatif).
  public int nombreSequences() {
    return sequences.size();
  }
}

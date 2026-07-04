package fr.univ_amu.iut.importation.model;

/// Aperçu de ce qu'un **écrasement** de passage (#214) détruirait, pour rendre la confirmation tangible :
/// le nombre de **séquences** et le nombre de **validations observateur** (taxon corrigé, référence,
/// commentaire) du passage existant au quadruplet visé. Les deux valent `0` si le n° est libre.
///
/// @param sequences séquences d'écoute du passage existant (régénérées à l'identique au réimport)
/// @param validations validations observateur qui seront **définitivement perdues** (aucune préservation
///     possible à l'écrasement, contrairement à une ré-importation de CSV)
public record ApercuEcrasement(int sequences, int validations) {

    /// Aperçu vide (n° libre / rattachement incomplet) : rien à écraser.
    public static final ApercuEcrasement VIDE = new ApercuEcrasement(0, 0);
}

package fr.univ_amu.iut.importation.model;

import java.util.Objects;

/// Avancement **déterminé** d'un import en cours (story #33) : un libellé lisible
/// (« Transformation 45/191 ») et une fraction globale dans `[0, 1]`.
///
/// Émis par [ServiceImport] au fil de la copie protégée (R9) puis de la transformation (R10/R11) des
/// originaux ; la couche IHM le relaie vers une barre de progression déterminée (remplace l'ancien
/// indicateur indéterminé). Objet de transport pur, sans dépendance JavaFX.
///
/// @param libelle texte d'étape affichable (jamais `null`)
/// @param fraction avancement global, de `0.0` (rien fait) à `1.0` (terminé)
public record Progression(String libelle, double fraction) {

    public Progression {
        Objects.requireNonNull(libelle, "libelle");
    }
}

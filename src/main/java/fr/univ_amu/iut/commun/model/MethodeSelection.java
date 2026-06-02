package fr.univ_amu.iut.commun.model;

/// Méthode de constitution d'une sélection d'écoute (C11, R12).
///
/// [#REPARTITION_TEMPORELLE] est la méthode par défaut à l'ouverture de la vue de
/// vérification.
public enum MethodeSelection {
    REPARTITION_TEMPORELLE("RéparTemporel"),
    ALEATOIRE("Aléatoire"),
    MANUEL("Manuel");

    private final String libelle;

    MethodeSelection(String libelle) {
        this.libelle = libelle;
    }

    public String libelle() {
        return libelle;
    }

    public static MethodeSelection parLibelle(String libelle) {
        for (MethodeSelection methode : values()) {
            if (methode.libelle.equals(libelle)) {
                return methode;
            }
        }
        throw new IllegalArgumentException("Méthode de sélection inconnue : " + libelle);
    }
}

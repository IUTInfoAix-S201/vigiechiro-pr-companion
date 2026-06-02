package fr.univ_amu.iut.commun.model;

/// Statut d'avancement d'un `Passage` dans le workflow d'import → dépôt (C5).
///
/// Progression attendue : [#IMPORTE] → [#TRANSFORME] → [#VERIFIE] → [#PRET_A_DEPOSER] → [#DEPOSE].
/// Le `libelle` (avec accents) est la valeur persistée.
public enum StatutWorkflow {
    IMPORTE("Importé"),
    TRANSFORME("Transformé"),
    VERIFIE("Vérifié"),
    PRET_A_DEPOSER("Prêt à déposer"),
    DEPOSE("Déposé");

    private final String libelle;

    StatutWorkflow(String libelle) {
        this.libelle = libelle;
    }

    public String libelle() {
        return libelle;
    }

    public static StatutWorkflow parLibelle(String libelle) {
        for (StatutWorkflow statut : values()) {
            if (statut.libelle.equals(libelle)) {
                return statut;
            }
        }
        throw new IllegalArgumentException("Statut workflow inconnu : " + libelle);
    }
}

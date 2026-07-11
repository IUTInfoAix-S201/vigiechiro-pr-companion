package fr.univ_amu.iut.commun.viewmodel;

/// État d'une **unité de travail** dans une table de suivi (archive de dépôt #820, fichier d'import,
/// unité de dépôt…), qui pilote son rendu (couleur + icône + libellé) et sa barre de progression :
/// gris « en attente » → bleu « en cours » → vert « terminée » → ou rouge « échec ».
///
/// Les libellés qualifient la **ligne** suivie (féminin : une archive, une copie, une transformation).
public enum EtatUnite {

    /// Planifiée mais pas encore traitée (gris) : le plan est connu, le travail n'a pas commencé.
    EN_ATTENTE("En attente"),

    /// En cours de traitement (bleu) : la barre de la ligne progresse de 0 à 1.
    EN_COURS("En cours"),

    /// Traitée avec succès (vert).
    TERMINEE("Terminée"),

    /// Traitement échoué (rouge).
    ECHEC("Échec");

    private final String libelle;

    EtatUnite(String libelle) {
        this.libelle = libelle;
    }

    /// Libellé lisible de l'état (affichage + accessibilité).
    public String libelle() {
        return libelle;
    }
}

package fr.univ_amu.iut.commun.model;

/// **Port** : importer les observations d'une nuit depuis Vigie-Chiro, depuis n'importe quel écran (#1264).
///
/// L'import lui-même vit dans la feature `validation` (il écrit les observations). Mais c'est depuis
/// **M-Passage** qu'on veut souvent le déclencher — juste après avoir constaté que l'analyse est terminée.
/// Or `passage` ne peut pas dépendre de `validation` : le graphe des features doit rester acyclique
/// (ArchUnit y veille). D'où ce contrat dans `commun`, que `validation` implémente et que `passage`
/// consomme — le même patron que [ReferentielPoint] et [CoordonneesPoint].
///
/// Le compte rendu est rendu sous forme de **texte prêt à afficher** : le bilan détaillé
/// (`BilanImport`) appartient à `validation`, et n'a pas à traverser le socle pour être relu par une
/// modale qui ne fait que l'annoncer.
public interface ImportObservations {

    /// La nuit est-elle rattachée à une participation Vigie-Chiro ? Sinon, il n'y a rien à importer (et le
    /// rattachement se fait depuis « Sons & validation »).
    boolean estRattache(Long idPassage);

    /// Importe les observations de la nuit. **Bloquant** (réseau) : à appeler hors du fil JavaFX.
    ///
    /// Lève une [RegleMetierException] quand il n'y a rien à importer — avec la **raison** (analyse jamais
    /// lancée, en cours, en échec…) plutôt qu'un refus muet (#1264).
    ///
    /// @param remplacer remplace le jeu existant en préservant les validations de l'observateur
    /// @return un compte rendu prêt à afficher
    String importer(Long idPassage, boolean remplacer);
}

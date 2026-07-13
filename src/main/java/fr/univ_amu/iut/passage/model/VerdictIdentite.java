package fr.univ_amu.iut.passage.model;

/// Verdict de la vérification d'identité d'un fichier candidat (#1309) : accepté avec un **niveau
/// de confiance**, ou refusé avec un **motif**. Jamais un simple booléen : l'utilisateur qui
/// réactive un passage archivé (#1302) voit quelle preuve a joué, et pourquoi un jeu de fichiers
/// étranger est écarté.
public sealed interface VerdictIdentite {

    /// Niveau de confiance d'une identité **acceptée**.
    enum NiveauConfiance {
        /// Identité prouvée : empreinte de contenu identique (#1299), ou deux preuves
        /// **indépendantes** concordantes (structurelle + acoustique) : on ne peut pas mieux faire
        /// sans empreinte, et c'est dit tel quel à l'utilisateur.
        CERTITUDE,

        /// Identité très probable : preuve structurelle seule (nom, durée réelle confrontée à
        /// l'en-tête WAV, taille si connue), sans cri à confronter. C'est le niveau des séquences
        /// **sans observation** : elles n'ont rien à corrompre, la couverture épouse la valeur de
        /// ce qu'elle protège.
        FORTE
    }

    /// Le fichier candidat est bien celui que la base décrit.
    ///
    /// @param niveau niveau de confiance atteint
    /// @param preuves ce qui a été vérifié, en clair (affiché à l'utilisateur au moment de réactiver)
    record Acceptee(NiveauConfiance niveau, String preuves) implements VerdictIdentite {}

    /// Le fichier candidat n'est **pas** celui que la base décrit (ou n'est pas vérifiable).
    ///
    /// @param motif ce qui a divergé, en clair : rebrancher quand même produirait des observations
    ///     pointant sur le mauvais audio, un résultat scientifiquement faux et silencieux
    record Refusee(String motif) implements VerdictIdentite {}
}

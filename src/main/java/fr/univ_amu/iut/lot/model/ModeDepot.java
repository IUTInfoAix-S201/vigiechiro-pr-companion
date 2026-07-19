package fr.univ_amu.iut.lot.model;

import java.util.Arrays;

/// **Sous quelle forme une nuit part sur la plateforme** (#1997) : en archives ZIP, ou sequence par
/// sequence.
///
/// ## Pourquoi ce choix devient explicite
///
/// Il ne l'etait pas. Le mode se decidait tout seul, d'apres la **place disque** : les archives etaient
/// privilegiees, et le repli WAV n'intervenait que si le disque ne permettait pas de les creer. Tant
/// que le depot generait tout le lot d'un coup, ce repli se declenchait pour de vrai sur les grosses
/// nuits.
///
/// Le pipeline (#1995) a supprime cette contrainte : deux archives suffisent desormais, donc le ZIP est
/// pratiquement toujours possible et le repli ne se declencherait plus jamais. Sans rien faire, on
/// aurait donc **supprime en silence** la seule route par laquelle l'IHM produisait un depot WAV.
///
/// ## Ce qui est en jeu, et qui n'est pas une question de place
///
/// En **ZIP**, la plateforme extrait l'archive puis la **detruit**, et ne remonte pas les WAV extraits
/// sur S3 (#1244). L'audio n'est donc pas recuperable cote serveur, et relancer le calcul de la
/// participation effacerait ses observations **sans pouvoir les recalculer** : c'est la raison d'etre du
/// verrou `relanceBloquee`.
///
/// En **WAV**, chaque sequence deposee garde son `s3_id` et survit au traitement. L'audio reste
/// telechargeable, et la participation reste relancable.
///
/// Le defaut reste [#ARCHIVES_ZIP] : c'est le comportement etabli, et le plus rapide. Mais c'en est
/// maintenant un que l'on choisit.
public enum ModeDepot {

    /// Archives ZIP (defaut) : rapide, peu de requetes. L'audio n'est **pas** recuperable cote serveur
    /// apres traitement, et la participation ne pourra pas etre relancee (#1244).
    ARCHIVES_ZIP("zip", "Archives ZIP (rapide)"),

    /// Sequences WAV une a une : plus lent (une requete par sequence), mais l'audio est **conserve** cote
    /// serveur et la participation reste relancable.
    SEQUENCES_WAV("wav", "Séquences WAV (audio conservé en ligne)");

    private final String valeur;
    private final String libelle;

    ModeDepot(String valeur, String libelle) {
        this.valeur = valeur;
        this.libelle = libelle;
    }

    /// Valeur persistee dans les reglages (stable : ne pas renommer, des bases la portent).
    public String valeur() {
        return valeur;
    }

    /// Libelle affiche dans la liste deroulante des reglages.
    public String libelle() {
        return libelle;
    }

    /// Le mode designe par sa valeur persistee, ou [#ARCHIVES_ZIP] si elle est absente ou inconnue :
    /// un reglage corrompu ne doit pas empecher de deposer.
    public static ModeDepot parValeur(String valeur) {
        return Arrays.stream(values())
                .filter(mode -> mode.valeur.equals(valeur))
                .findFirst()
                .orElse(ARCHIVES_ZIP);
    }
}

package fr.univ_amu.iut.commun.viewmodel;

import fr.univ_amu.iut.commun.model.PreferenceSourceEspece;
import fr.univ_amu.iut.commun.view.DescripteurReglage;
import fr.univ_amu.iut.commun.view.OngletReglages;
import java.util.List;

/// Onglet « Général » de l'écran Réglages (#928) : réglages transverses de l'application, contribués
/// par le socle (`CommunModule`). Pour l'instant la **source des fiches espèces** ; emplacement futur
/// du thème clair/sombre et d'un mode daltonien audio.
///
/// Le descripteur pointe la **même clé** que l'item ☰ « Fiches espèces sur Wikipédia »
/// ([PreferenceSourceEspece#CLE]) : liés à la même Property réactive, l'item et l'onglet restent
/// synchronisés en direct.
public final class OngletReglagesGeneral implements OngletReglages {

    @Override
    public String idFeature() {
        return "general";
    }

    @Override
    public int ordre() {
        return 10;
    }

    @Override
    public String titre() {
        return "Général";
    }

    @Override
    public String iconeLiteral() {
        return "fas-sliders-h";
    }

    @Override
    public List<DescripteurReglage> reglages() {
        return List.of(new DescripteurReglage.Booleen(
                PreferenceSourceEspece.CLE,
                "Fiches espèces sur Wikipédia (sinon GBIF)",
                "Source des fiches pour les espèces hors chiroptères (le PNA reste prioritaire). Effet immédiat.",
                false));
    }
}

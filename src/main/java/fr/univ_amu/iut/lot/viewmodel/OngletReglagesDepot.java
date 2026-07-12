package fr.univ_amu.iut.lot.viewmodel;

import fr.univ_amu.iut.commun.view.DescripteurReglage;
import fr.univ_amu.iut.commun.view.OngletReglages;
import java.util.List;

/// Onglet « Dépôt » de l’écran Réglages (#1047) : plafond de taille des archives ZIP générées pour
/// le dépôt, contribué par le module `lot`.
///
/// Le réglage pilote le [fr.univ_amu.iut.lot.model.CompacteurDepot] via `LotModule` : relu à chaque
/// **génération d’archives** (pas de redémarrage nécessaire). La propriété système
/// `vigiechiro.depot.taille-max-mo` reste prioritaire (tests/outils). Le défaut et la borne haute
/// sont la contrainte de la plateforme (700 Mo, base 1000).
public final class OngletReglagesDepot implements OngletReglages {

    /// Clé du réglage persisté (table `app_setting`).
    public static final String CLE_TAILLE_MAX = "depot.taille-max-mo";

    /// Défaut (et borne haute) : contrainte Tadarida côté plateforme.
    public static final int DEFAUT_TAILLE_MAX_MO = 700;

    static final int MIN_TAILLE_MAX_MO = 50;
    static final int MAX_TAILLE_MAX_MO = DEFAUT_TAILLE_MAX_MO;

    @Override
    public String idFeature() {
        return "lot";
    }

    @Override
    public int ordre() {
        return 40;
    }

    @Override
    public String titre() {
        return "Dépôt";
    }

    @Override
    public String iconeLiteral() {
        return "fas-cloud-upload-alt";
    }

    @Override
    public List<DescripteurReglage> reglages() {
        return List.of(new DescripteurReglage.Entier(
                CLE_TAILLE_MAX,
                "Taille maximale d’une archive (Mo)",
                "Plafond de découpe des archives ZIP générées pour le dépôt (base 1000 : 700 Mo"
                        + " = 700 000 000 octets, la limite acceptée par la plateforme). Appliqué à la"
                        + " prochaine génération d’archives.",
                DEFAUT_TAILLE_MAX_MO,
                MIN_TAILLE_MAX_MO,
                MAX_TAILLE_MAX_MO));
    }
}

package fr.univ_amu.iut.cli.commande;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.model.VersionApplication;
import java.util.Objects;
import picocli.CommandLine.IVersionProvider;

/// Alimente `vigiechiro --version` avec la version réellement empaquetée (#2108).
///
/// L'option existait déjà (`mixinStandardHelpOptions`), mais l'attribut `version` de la commande
/// racine portait une chaîne **figée sans numéro** : elle répondait sans rien apprendre. Un
/// fournisseur est nécessaire parce que la version n'est connue qu'à l'exécution - elle vient du
/// manifeste, pas du code source.
///
/// Deux lignes plutôt qu'une : la seconde nomme le **dossier de travail** attendu dans un
/// signalement d'anomalie. C'est l'usage réel de cette commande - on la lance pour renseigner un
/// rapport, rarement par curiosité.
public final class FournisseurVersion implements IVersionProvider {

    private final VersionApplication version;

    @Inject
    FournisseurVersion(VersionApplication version) {
        this.version = Objects.requireNonNull(version, "version");
    }

    @Override
    public String[] getVersion() {
        return new String[] {
            "VigieChiro - compagnon PR (CLI) " + version.libelle(),
            "Java " + System.getProperty("java.version") + " sur " + System.getProperty("os.name")
        };
    }
}

package fr.univ_amu.iut.cli.commande;

import com.google.inject.Inject;
import com.google.inject.Provider;
import fr.univ_amu.iut.commun.persistence.ServiceSauvegarde;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

/// Commande `restaurer` (#1346) : remet en place une sauvegarde produite par `sauvegarder`.
///
/// **Destructif** : l'état local est écrasé. `--confirmer` est donc obligatoire — en script, rien ne doit
/// pouvoir remplacer une base par inadvertance. L'état courant de la base est mis de côté
/// (`vigiechiro.db.avant-restauration`) ; l'**audio**, lui, ne l'est pas.
///
/// Avec `--complet`, l'argument est le **dossier** d'une sauvegarde complète (base + dossiers de session) ;
/// sinon, c'est le **fichier** `.db` d'une sauvegarde de base.
@Command(
        name = "restaurer",
        description = "Restaure une sauvegarde (base seule, ou base + audio avec --complet). Destructif.")
public final class Restaurer implements Callable<Integer> {

    @Parameters(
            index = "0",
            paramLabel = "<source>",
            description = "Fichier .db de la sauvegarde, ou dossier de sauvegarde complète avec --complet.")
    private Path source;

    @Option(
            names = "--complet",
            description = "La source est un DOSSIER de sauvegarde complète : restaure la base ET l'audio.")
    private boolean complet;

    @Option(
            names = "--confirmer",
            description = "Obligatoire : confirme l'écrasement de l'état local (opération destructive).")
    private boolean confirmer;

    @Spec
    private CommandSpec spec;

    // Provider, non instance directe : picocli instancie les sous-commandes AVANT la migration du schéma.
    private final Provider<ServiceSauvegarde> service;

    @Inject
    public Restaurer(Provider<ServiceSauvegarde> service) {
        this.service = Objects.requireNonNull(service, "service");
    }

    @Override
    public Integer call() {
        PrintWriter sortie = spec.commandLine().getOut();
        if (!confirmer) {
            sortie.println("Restauration refusée : elle écrase l'état local. Relancez avec --confirmer.");
            return 1;
        }
        ServiceSauvegarde sauvegarde = service.get();
        if (complet) {
            sauvegarde.restaurerComplet(source);
            sortie.println("Base et dossiers de session restaurés depuis : " + source);
        } else {
            sauvegarde.restaurer(source);
            sortie.println("Base restaurée depuis : " + source);
            sortie.println("(L'audio n'est pas concerné : seule une sauvegarde --complet le contient.)");
        }
        return 0;
    }
}

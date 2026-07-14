package fr.univ_amu.iut.documentation;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import fr.univ_amu.iut.cli.commande.CommandeRacine;
import fr.univ_amu.iut.commun.di.RacineInjecteur;
import fr.univ_amu.iut.commun.view.ActiviteAccueil;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

/// Garde-fou de **documentation** (#1458) : une commande CLI sans ligne de doc, ou un écran sans fiche,
/// font **rougir la CI**.
///
/// ## Pourquoi ce test existe
///
/// La clôture de l'EPIC #1154 a trouvé deux dérives que **rien** n'aurait signalées :
/// `dev-docs/cli.md` documentait **22 commandes sur 29** (dont quatre livrées par le chantier même), et
/// l'écran « Audit de cohérence » n'avait **aucune fiche** depuis sa livraison (#1133). Une relecture à
/// la main les a vues. C'est précisément ce qu'on ne peut pas garantir.
///
/// Le dépôt défend déjà ses **captures** par quatre garde-fous (`check-doc-images.sh`,
/// `captures.manifest`, `check-captures.sh`, `check-capture-mains.sh`), au nom d'un principe qu'il a
/// tranché : *« une fonctionnalité visible sans capture est une fonctionnalité à moitié livrée »*
/// (`dev-docs/captures.md`). Les **commandes** et les **écrans**, eux, n'avaient rien. Ce test comble
/// l'asymétrie : le même raisonnement vaut pour une commande sans ligne et pour un écran sans page.
///
/// ## Ce qu'il confronte
///
/// Le point de comparaison n'est **jamais une liste tenue à la main** (c'est exactement ce qui dérive),
/// mais la **vérité du câblage** :
///
/// - les sous-commandes déclarées dans l'annotation `@Command` de [CommandeRacine] — lues par
///   **réflexion sur l'annotation**, sans jamais instancier une commande (leurs constructeurs tirent des
///   `Provider` qui ouvrent la base : les instancier ici ferait de l'E/S pour rien) ;
/// - les [ActiviteAccueil] réellement liées dans le `Multibinder` de l'**injecteur**, pas les classes qui
///   ressemblent à des activités.
///
/// Une doc qui ment est **pire** qu'une doc absente : on la croit.
class DocumentationAJourTest {

    /// Surefire s'exécute depuis la racine du projet (`${basedir}`) : les chemins sont relatifs à elle.
    private static final Path DOC_CLI = Path.of("dev-docs", "cli.md");

    private static final Path FICHES = Path.of("docs", "ecrans");

    private static final Path INDEX_FICHES = FICHES.resolve("index.md");

    private static final Path NAV = Path.of("mkdocs.yml");

    @AfterEach
    void nettoyer() {
        System.clearProperty("vigiechiro.workspace");
    }

    @Test
    @DisplayName("Chaque sous-commande câblée dans la CLI figure dans le tableau de dev-docs/cli.md")
    void chaque_commande_cli_est_documentee() {
        String doc = lire(DOC_CLI);

        List<String> absentes = sousCommandesCablees().stream()
                .filter(nom -> !doc.contains("| `" + nom + "`"))
                .sorted()
                .toList();

        assertThat(absentes)
                .as("Ces commandes sont câblées dans CommandeRacine mais ne figurent dans aucune ligne du "
                        + "tableau de dev-docs/cli.md. Une commande qu'on ne documente pas est une commande "
                        + "que personne ne trouvera : ajoutez-lui sa ligne.")
                .isEmpty();
    }

    @Test
    @DisplayName("Chaque activité d'accueil déclare une fiche d'écran qui existe")
    void chaque_activite_a_sa_fiche(@TempDir Path espaceDeTravail) {
        Set<ActiviteAccueil> activites = activitesCablees(espaceDeTravail);

        assertThat(activites)
                .as("aucune activité d'accueil câblée : le test ne prouverait rien")
                .isNotEmpty();

        List<String> sansFiche = activites.stream()
                .filter(activite -> !Files.isRegularFile(FICHES.resolve(activite.pageDoc() + ".md")))
                .map(activite -> activite.titre() + " -> docs/ecrans/" + activite.pageDoc() + ".md")
                .sorted()
                .toList();

        assertThat(sansFiche)
                .as("Ces activités sont offertes sur l'écran d'accueil, mais la fiche qu'elles déclarent "
                        + "n'existe pas. Un écran livré sans page est un écran à moitié livré.")
                .isEmpty();
    }

    @Test
    @DisplayName("Chaque fiche d'écran est atteignable : dans la nav MkDocs et dans l'index de la section")
    void chaque_fiche_est_atteignable() {
        String nav = lire(NAV);
        String index = lire(INDEX_FICHES);
        List<String> fiches = fichesEcrites();

        assertThat(fiches)
                .as("aucune fiche d'écran trouvée : le test ne prouverait rien")
                .isNotEmpty();

        List<String> horsNav = fiches.stream()
                .filter(fiche -> !nav.contains("ecrans/" + fiche + ".md"))
                .toList();
        assertThat(horsNav)
                .as("Ces fiches existent mais n'apparaissent dans aucune entrée `nav` de mkdocs.yml : le site "
                        + "produit ne les publie pas. Une page que le site ne sert pas n'existe pas.")
                .isEmpty();

        List<String> horsIndex = fiches.stream()
                .filter(fiche -> !index.contains("(" + fiche + ".md)"))
                .toList();
        assertThat(horsIndex)
                .as("Ces fiches ne sont liées par aucune ligne du tableau de docs/ecrans/index.md : depuis "
                        + "l'index de leur propre section, l'écran est invisible.")
                .isEmpty();
    }

    /// Noms des sous-commandes tels que picocli les expose à l'utilisateur, lus **sur l'annotation** de
    /// [CommandeRacine] : aucune commande n'est instanciée, donc aucun `Provider` n'ouvre la base.
    private static List<String> sousCommandesCablees() {
        CommandLine.Command racine = CommandeRacine.class.getAnnotation(CommandLine.Command.class);
        return Stream.of(racine.subcommands())
                .map(classe -> classe.getAnnotation(CommandLine.Command.class))
                .filter(Objects::nonNull)
                .map(CommandLine.Command::name)
                .toList();
    }

    /// Les activités **réellement liées** dans le `Multibinder<ActiviteAccueil>` : la vérité du câblage,
    /// pas les classes qui en ont l'air.
    private static Set<ActiviteAccueil> activitesCablees(Path espaceDeTravail) {
        System.setProperty("vigiechiro.workspace", espaceDeTravail.toString());
        Injector injecteur = RacineInjecteur.creer();
        return injecteur.getInstance(Key.get(new TypeLiteral<Set<ActiviteAccueil>>() {}));
    }

    /// Noms courts des fiches présentes sur le disque, `index.md` exclue (elle est le sommaire, pas un écran).
    private static List<String> fichesEcrites() {
        try (Stream<Path> pages = Files.list(FICHES)) {
            return pages.map(page -> page.getFileName().toString())
                    .filter(nom -> nom.endsWith(".md"))
                    .filter(nom -> !"index.md".equals(nom))
                    .map(nom -> nom.substring(0, nom.length() - ".md".length()))
                    .sorted(Comparator.naturalOrder())
                    .toList();
        } catch (IOException echec) {
            throw new UncheckedIOException("lecture de " + FICHES, echec);
        }
    }

    private static String lire(Path fichier) {
        try {
            return Files.readString(fichier);
        } catch (IOException echec) {
            throw new UncheckedIOException("lecture de " + fichier, echec);
        }
    }
}

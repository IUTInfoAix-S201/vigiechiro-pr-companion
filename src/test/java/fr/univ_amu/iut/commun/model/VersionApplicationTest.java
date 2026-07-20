package fr.univ_amu.iut.commun.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/// La version vient du manifeste, donc de l'**empaquetage** et non du code : ces tests exercent les
/// deux situations réelles, en choisissant la classe de référence.
class VersionApplicationTest {

    @Test
    @DisplayName("hors d'un jar, la version empaquetée est absente")
    void horsJarLaVersionEstAbsente() {
        // La suite tourne sur les classes Maven : aucune classe du projet n'a de manifeste. C'est le
        // quotidien du développement (javafx:run, tests, outils de capture), pas une anomalie.
        VersionApplication version = new VersionApplication(VersionApplication.class);

        assertThat(version.versionEmpaquetee())
                .as("aucune version ne doit être inventée quand il n'y a pas de manifeste à lire")
                .isEmpty();
    }

    @Test
    @DisplayName("hors d'un jar, le libellé reste lisible plutôt que vide")
    void horsJarLeLibelleResteLisible() {
        VersionApplication version = new VersionApplication(VersionApplication.class);

        // Une chaîne vide dans un « À propos » ou un --version ne dit pas si l'information manque ou
        // si le champ est cassé : le repli doit se lire.
        assertThat(version.libelle())
                .as("le libellé doit toujours dire quelque chose")
                .isEqualTo(VersionApplication.INCONNUE)
                .isNotBlank();
    }

    @Test
    @DisplayName("depuis un jar, la version du manifeste est rendue telle quelle")
    void depuisUnJarLaVersionEstLue(@TempDir Path dossier) throws Exception {
        // On EMPAQUETTE réellement, plutôt que de chercher une classe tierce dont le manifeste
        // porterait une version : le mécanisme testé est justement la lecture d'un manifeste de jar,
        // et s'appuyer sur l'empaquetage d'autrui rendrait ce test dépendant de ses choix. Une
        // première version interrogeait `String.class` en supposant que le JDK porte cette entrée -
        // il ne la porte pas, et le test a rougi.
        Path jar = jarPortantLaVersion(dossier, "9.9.9-essai");

        try (URLClassLoader chargeur = new URLClassLoader(new URL[] {jar.toUri().toURL()}, null)) {
            Class<?> classeEmpaquetee = Class.forName(CLASSE, false, chargeur);
            VersionApplication version = new VersionApplication(classeEmpaquetee);

            assertThat(version.versionEmpaquetee())
                    .as("une version présente au manifeste doit être rendue, non remplacée par le repli")
                    .contains("9.9.9-essai");
            assertThat(version.libelle()).isEqualTo("9.9.9-essai");
        }
    }

    /// Fabrique un jar portant `Implementation-Version` et une classe du projet, pour exercer la
    /// lecture de manifeste telle qu'elle se produit sur l'artefact distribué.
    private static Path jarPortantLaVersion(Path dossier, String version) throws Exception {
        Manifest manifeste = new Manifest();
        manifeste.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifeste.getMainAttributes().put(Attributes.Name.IMPLEMENTATION_VERSION, version);

        String chemin = CLASSE.replace('.', '/') + ".class";
        Path jar = dossier.resolve("essai.jar");
        try (InputStream classe = VersionApplication.class.getResourceAsStream("/" + chemin);
                JarOutputStream sortie = new JarOutputStream(Files.newOutputStream(jar), manifeste)) {
            sortie.putNextEntry(new JarEntry(chemin));
            sortie.write(Objects.requireNonNull(classe, chemin).readAllBytes());
            sortie.closeEntry();
        }
        return jar;
    }

    private static final String CLASSE = "fr.univ_amu.iut.commun.model.VersionApplication";

    @Test
    @DisplayName("une version vide au manifeste vaut une version absente")
    void versionVideVautAbsente() {
        // Un manifeste peut porter l'entrée avec une valeur blanche (substitution Maven ratée). La
        // traiter comme présente ferait afficher « Version :  » et comparer une chaîne vide.
        VersionApplication version = new VersionApplication(ClasseSansPaquetNomme.class);

        assertThat(version.versionEmpaquetee()).isEmpty();
        assertThat(version.libelle()).isEqualTo(VersionApplication.INCONNUE);
    }

    /// Classe locale sans manifeste, pour exercer le repli sans dépendre du JDK.
    private static final class ClasseSansPaquetNomme {}
}

package fr.univ_amu.iut.commun.view;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import fr.univ_amu.iut.commun.di.RacineInjecteur;
import fr.univ_amu.iut.commun.model.PreferenceSourceEspece;
import java.nio.file.Path;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/// Câblage Guice du point d'extension « onglets de réglages » (#927/#928) : le socle agrège, via le
/// `Multibinder<OngletReglages>`, les onglets contribués par le socle lui-même (« Général ») et par
/// les features (« Import »), sans dépendre d'aucune feature. On vérifie aussi la **clé partagée** de
/// l'onglet « Général » avec l'item ☰ (garde-fou de la synchro live).
class OngletReglagesWiringTest {

    @AfterEach
    void nettoyer() {
        System.clearProperty("vigiechiro.workspace");
    }

    @Test
    @DisplayName("Le socle agrège les onglets de réglages (Général, Import) et l'entrée ☰ est active")
    void agrege_les_onglets(@TempDir Path tmp) {
        System.setProperty("vigiechiro.workspace", tmp.toString());
        Injector injector = RacineInjecteur.creer();

        Set<OngletReglages> onglets = injector.getInstance(Key.get(new TypeLiteral<Set<OngletReglages>>() {}));

        assertThat(onglets)
                .extracting(onglet -> onglet.getClass().getSimpleName())
                .contains("OngletReglagesGeneral", "OngletReglagesImport");
        assertThat(injector.getInstance(NavigationReglages.class).aDesReglages())
                .isTrue();
    }

    @Test
    @DisplayName("L'onglet « Général » pointe la clé de la préférence source espèce (synchro avec le ☰)")
    void onglet_general_partage_la_cle_du_menu(@TempDir Path tmp) {
        System.setProperty("vigiechiro.workspace", tmp.toString());
        Injector injector = RacineInjecteur.creer();

        Set<OngletReglages> onglets = injector.getInstance(Key.get(new TypeLiteral<Set<OngletReglages>>() {}));
        OngletReglages general = onglets.stream()
                .filter(onglet -> "general".equals(onglet.idFeature()))
                .findFirst()
                .orElseThrow();

        assertThat(general.reglages())
                .singleElement()
                .isInstanceOfSatisfying(DescripteurReglage.Booleen.class, booleen -> {
                    assertThat(booleen.cle()).isEqualTo(PreferenceSourceEspece.CLE);
                    assertThat(booleen.defaut()).isFalse();
                });
    }
}

package fr.univ_amu.iut.commun.view;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.commun.viewmodel.CompteRendu;
import fr.univ_amu.iut.commun.viewmodel.CompteRendu.Constat;
import fr.univ_amu.iut.commun.viewmodel.CompteRendu.Detail;
import fr.univ_amu.iut.commun.viewmodel.RetourOperation.Severite;
import java.util.List;
import java.util.stream.IntStream;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

/// Le rendu d'un [CompteRendu] (ADR 0031) : ce qui le rend **reconnaissable** et ce que la structure
/// débloque, par opposition au `Label` unique qu'il remplace.
@ExtendWith(ApplicationExtension.class)
class VueCompteRenduTest {

    private static final CompteRendu REACTIVATION = new CompteRendu(
            "Réactivation partielle",
            "Ce dossier ne contenait que vos enregistrements bruts.",
            List.of(
                    Constat.de("4229 séquence(s) réactivée(s).", Severite.SUCCES),
                    new Constat(
                            "7 séquence(s) restent introuvables dans ce dossier.",
                            Severite.ERREUR,
                            List.of(
                                    new Detail("PaRec_223507.wav", "enregistrement absent du dossier"),
                                    new Detail("PaRec_012327_001.wav", "tranche non régénérée")))),
            "L'audio reste incomplet : 4229 séquence(s) sur 4236.");

    @Test
    @DisplayName("Un compte rendu se reconnaît à sa classe racine, quel que soit l'écran")
    void classe_racine_identifiable() {
        VBox rendu = VueCompteRendu.rendre(REACTIVATION, 5);

        assertThat(rendu.getStyleClass())
                .as("c'est elle qui fait qu'on reconnaît la structure avant de lire")
                .contains(VueCompteRendu.CLASSE_RACINE);
    }

    @Test
    @DisplayName("Chaque détail est un nœud distinct, avec son propre retrait")
    void chaque_detail_est_un_noeud() {
        VBox rendu = VueCompteRendu.rendre(REACTIVATION, 5);

        List<Label> details = rendu.getChildren().stream()
                .filter(Label.class::isInstance)
                .map(Label.class::cast)
                .filter(l -> l.getStyleClass().contains("compte-rendu-detail"))
                .toList();

        assertThat(details)
                .as("dans un Label unique, une puce trop longue repartait à la marge (#1987)")
                .hasSize(2);
        assertThat(details.getFirst().isWrapText())
                .as("elle doit pouvoir revenir à la ligne, mais sous son propre texte")
                .isTrue();
    }

    @Test
    @DisplayName("La sévérité teinte le fait concerné, pas l'ensemble du compte rendu")
    void severite_par_fait() {
        VBox rendu = VueCompteRendu.rendre(REACTIVATION, 5);

        List<String> classes = rendu.getChildren().stream()
                .filter(Label.class::isInstance)
                .map(Label.class::cast)
                .flatMap(l -> l.getStyleClass().stream())
                .toList();

        assertThat(classes)
                .as("un même compte rendu porte un succès ET un échec : une seule couleur mentirait")
                .contains("compte-rendu-succes", "compte-rendu-erreur");
    }

    @Test
    @DisplayName("Le plafond appartient à la surface : la modale résume, la commande montre tout")
    void plafond_decide_par_la_surface() {
        List<Detail> cent = IntStream.range(0, 100)
                .mapToObj(i -> Detail.de("fichier-" + i + ".wav"))
                .toList();
        CompteRendu volumineux =
                CompteRendu.de("Beaucoup", List.of(new Constat("100 introuvables.", Severite.ERREUR, cent)));

        long plafonne = compter(VueCompteRendu.rendre(volumineux, 5), "compte-rendu-detail");
        long complet = compter(VueCompteRendu.rendre(volumineux, VueCompteRendu.SANS_PLAFOND), "compte-rendu-detail");

        assertThat(plafonne).as("cinq détails, plus la ligne « et N autre(s) »").isEqualTo(6);
        assertThat(complet)
                .as("la sortie d'une commande se filtre : rien n'y est masqué")
                .isEqualTo(100);
    }

    @Test
    @DisplayName("Un compte rendu vide ne rend rien : mieux vaut ça qu'un cadre vide")
    void compte_rendu_vide_ne_rend_rien() {
        assertThat(VueCompteRendu.rendre(CompteRendu.de("", List.of()), 5).getChildren())
                .isEmpty();
    }

    private static long compter(VBox rendu, String classe) {
        return rendu.getChildren().stream()
                .filter(Label.class::isInstance)
                .map(Label.class::cast)
                .filter(l -> l.getStyleClass().contains(classe))
                .count();
    }
}

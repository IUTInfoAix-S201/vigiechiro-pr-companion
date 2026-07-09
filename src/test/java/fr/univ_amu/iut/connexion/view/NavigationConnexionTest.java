package fr.univ_amu.iut.connexion.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.google.inject.Injector;
import fr.univ_amu.iut.commun.api.ProfilVigieChiro;
import fr.univ_amu.iut.commun.model.Horloge;
import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.connexion.model.StockageConnexion;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/// Libellé de l'entrée de menu ☰ « Connexion VigieChiro » (#741) : reflète l'état de connexion **stocké
/// localement**, sans réseau (invite si déconnecté, identité si connecté). `ouvrir()` (chargement FXML)
/// n'est pas testé ici : c'est du câblage IHM couvert par le test de vue de la modale.
class NavigationConnexionTest {

    @TempDir
    Path workspace;

    private StockageConnexion stockage;
    private NavigationConnexion navigation;

    @BeforeEach
    void preparer() {
        stockage = new StockageConnexion(new Workspace(workspace), Horloge.figeeAu(LocalDate.of(2026, 1, 1)));
        navigation = new NavigationConnexion(mock(Injector.class), stockage);
    }

    @Test
    @DisplayName("déconnecté : l'entrée invite à se connecter")
    void libelle_deconnecte() {
        assertThat(navigation.libelleMenu()).contains("Se connecter");
    }

    @Test
    @DisplayName("connecté : l'entrée affiche l'identité (pseudo + rôle)")
    void libelle_connecte() {
        stockage.enregistrer("TOK", new ProfilVigieChiro("6a1b", "Sébastien", "Observateur"));

        assertThat(navigation.libelleMenu()).contains("Sébastien").contains("Observateur");
    }
}

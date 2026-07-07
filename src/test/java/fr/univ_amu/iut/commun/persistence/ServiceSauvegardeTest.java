package fr.univ_amu.iut.commun.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.univ_amu.iut.commun.model.HorlogeFigee;
import fr.univ_amu.iut.commun.model.Utilisateur;
import fr.univ_amu.iut.commun.model.Workspace;
import fr.univ_amu.iut.commun.model.dao.UtilisateurDao;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/// Sauvegarde et restauration de la base (#148).
class ServiceSauvegardeTest {

    @TempDir
    Path workspaceDir;

    private SourceDeDonnees source;
    private UtilisateurDao utilisateurDao;
    private ServiceSauvegarde service;

    @BeforeEach
    void preparer() {
        source = new SourceDeDonnees(new Workspace(workspaceDir));
        new MigrationSchema(source).migrer();
        utilisateurDao = new UtilisateurDao(source);
        // Horloge figée → horodatage déterministe dans le nom de fichier.
        service = new ServiceSauvegarde(source, new HorlogeFigee(LocalDateTime.of(2026, 7, 7, 14, 30, 15)));
    }

    @Test
    @DisplayName("La sauvegarde crée un fichier horodaté dans le dossier choisi")
    void sauvegarde_cree_un_fichier_horodate() {
        utilisateurDao.insert(new Utilisateur("u1", "Alice"));
        Path dossier = workspaceDir.resolve("mes-sauvegardes");

        Path fichier = service.sauvegarder(dossier);

        assertThat(fichier).exists().hasParent(dossier).hasFileName("vigiechiro-sauvegarde-20260707-143015.db");
    }

    @Test
    @DisplayName("Restaurer revient à l'état de la sauvegarde et met de côté la base courante")
    void restauration_revient_a_l_etat_sauvegarde() {
        utilisateurDao.insert(new Utilisateur("u1", "Alice"));
        Path sauvegarde = service.sauvegarder(workspaceDir.resolve("mes-sauvegardes"));

        // Modification APRÈS la sauvegarde : un second utilisateur.
        utilisateurDao.insert(new Utilisateur("u2", "Bob"));
        assertThat(utilisateurDao.findAll()).hasSize(2);

        service.restaurer(sauvegarde);

        // Retour à l'état sauvegardé : seul u1 subsiste (u2 ajouté après est perdu).
        assertThat(utilisateurDao.findAll()).extracting(Utilisateur::localId).containsExactly("u1");
        // Filet de sécurité : l'état courant a été mis de côté avant écrasement.
        assertThat(workspaceDir.resolve(Workspace.FICHIER_BASE + ".avant-restauration"))
                .exists();
    }

    @Test
    @DisplayName("Restaurer depuis un fichier qui n'est pas une base est refusé")
    void restauration_fichier_invalide_rejetee() throws IOException {
        Path faux = Files.writeString(workspaceDir.resolve("faux.db"), "ceci n'est pas une base SQLite");

        assertThatExceptionOfType(DataAccessException.class).isThrownBy(() -> service.restaurer(faux));
    }

    @Test
    @DisplayName("Restaurer depuis un fichier absent est refusé")
    void restauration_fichier_absent_rejetee() {
        assertThatThrownBy(() -> service.restaurer(workspaceDir.resolve("absent.db")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Le dossier de sauvegarde par défaut est <workspace>/sauvegardes")
    void dossier_par_defaut() {
        assertThat(service.dossierParDefaut()).isEqualTo(workspaceDir.resolve("sauvegardes"));
    }
}

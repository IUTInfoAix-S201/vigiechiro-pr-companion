package fr.univ_amu.iut.importation.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.importation.model.NuitDetectee;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/// Badge de complétude d'une nuit (#2036) : libellé et **classe de pastille**, dérivés de l'état,
/// jamais stockés.
///
/// La table écrivait auparavant sa sévérité dans le texte de la cellule (« ✓ complète », « ⚠
/// incomplète (motif) »). La cellule passe par `ColonneBadge`, qui la rend en pastille colorée - la
/// couleur vient donc de [NuitVM#classeBadge()], et il faut que ses deux branches soient justes.
///
/// Ce test compte double parce qu'**aucune capture ne montre une nuit incomplète** : le jeu d'essai
/// des aperçus n'a que des nuits complètes. La revue visuelle ne peut donc rien dire de la branche
/// « incomplète », et c'est ici qu'elle se vérifie.
class NuitVMBadgeTest {

    @Test
    @DisplayName("Une nuit complète : libellé « complète » et pastille de succès")
    void nuit_complete() {
        NuitVM nuit = new NuitVM(nuitDetectee(true, null));

        assertThat(nuit.badge()).isEqualTo("complète");
        assertThat(nuit.classeBadge()).isEqualTo("badge-succes");
    }

    @Test
    @DisplayName("Une nuit tronquée : libellé « incomplète » et pastille d'avertissement, pas de danger")
    void nuit_incomplete() {
        NuitVM nuit = new NuitVM(nuitDetectee(false, "carte SD pleine"));

        assertThat(nuit.badge()).isEqualTo("incomplète");
        assertThat(nuit.classeBadge())
                .as("une nuit tronquée s'importe et se dépose normalement : c'est un avertissement, "
                        + "pas une erreur")
                .isEqualTo("badge-avertissement");
        assertThat(nuit.motifIncompletude())
                .as("le motif alimente l'infobulle de la pastille : il ne doit pas se perdre en chemin")
                .isEqualTo("carte SD pleine");
    }

    private static NuitDetectee nuitDetectee(boolean complete, String motif) {
        LocalDate date = LocalDate.of(2026, 7, 3);
        return new NuitDetectee(date, date.atTime(21, 0), date.plusDays(1).atTime(6, 0), List.of(), complete, motif);
    }
}

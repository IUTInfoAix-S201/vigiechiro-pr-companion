package fr.univ_amu.iut.importation.model;

import com.google.inject.Inject;
import fr.univ_amu.iut.commun.model.Prefixe;
import fr.univ_amu.iut.passage.model.RegenerationSequences;
import fr.univ_amu.iut.passage.model.SequencesRegenerees;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/// Implémentation du port [RegenerationSequences] (#1406) : elle rejoue **exactement** la chaîne de
/// l'import ([TransformationAudio]), c'est-à-dire la seule chose qui garantisse que les tranches
/// régénérées soient identiques à celles d'origine - mêmes noms, et mêmes octets tant que le code de
/// transformation n'a pas changé.
///
/// Rien n'est réinventé ici : c'est un **adaptateur**. Toute divergence future de la transformation se
/// répercutera donc automatiquement, et sera détectée par la cascade de vérification plutôt que passée
/// sous silence.
public class RegenerationParTransformationAudio implements RegenerationSequences {

    private final TransformationAudio transformation;

    @Inject
    public RegenerationParTransformationAudio(TransformationAudio transformation) {
        this.transformation = Objects.requireNonNull(transformation, "transformation");
    }

    @Override
    public SequencesRegenerees regenerer(
            Path brut,
            String nomOriginal,
            Prefixe prefixe,
            int frequenceAcquisitionHz,
            Path dossierSortie,
            List<String> nomsArbitres) {
        TransformationOriginal resultat =
                transformation.transformer(brut, nomOriginal, dossierSortie, prefixe, frequenceAcquisitionHz);
        List<Path> tranches =
                resultat.sequences().stream().map(SequenceProduite::chemin).toList();
        return new SequencesRegenerees(appliquerArbitrage(tranches, nomsArbitres), resultat.sha256());
    }

    /// Donne à chaque tranche le nom que l'arbitrage lui a attribué sur la nuit entière.
    ///
    /// C'est l'équivalent, pour la régénération, de ce que [ReconciliationNoms] fait à l'import : là-bas
    /// tous les originaux sont présents et l'arbitrage se déduit ; ici un seul brut est régénéré à la fois,
    /// donc l'arbitrage est **fourni** par l'appelant, qui a la nuit sous les yeux. Sans cette étape, une
    /// tranche ayant perdu une collision serait régénérée sous un nom que la base ne connaît pas.
    ///
    /// Le renommage est **positionnel** : la tranche d'index k prend le k-ième nom arbitré, les deux listes
    /// étant construites dans l'ordre des index. Rien n'est cru sur parole pour autant : le fichier passe
    /// ensuite la même cascade de vérification que n'importe quel candidat, qui refuserait un appariement
    /// erroné.
    private static List<Path> appliquerArbitrage(List<Path> tranches, List<String> nomsArbitres) {
        if (nomsArbitres.isEmpty()) {
            return tranches;
        }
        List<Path> renommees = new ArrayList<>(tranches.size());
        for (int index = 0; index < tranches.size(); index++) {
            Path tranche = tranches.get(index);
            if (index >= nomsArbitres.size() || tranche.getFileName().toString().equals(nomsArbitres.get(index))) {
                renommees.add(tranche);
                continue;
            }
            renommees.add(deplacer(tranche, tranche.resolveSibling(nomsArbitres.get(index))));
        }
        return List.copyOf(renommees);
    }

    private static Path deplacer(Path source, Path cible) {
        try {
            return Files.move(source, cible, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Renommage de tranche régénérée impossible : " + source, e);
        }
    }
}

package fr.univ_amu.iut.audit.model;

import fr.univ_amu.iut.commun.model.Severite;
import java.util.List;
import java.util.Objects;

/// Résultat d'un audit de cohérence : la liste des écarts relevés (vide si tout est sain).
///
/// @param constats les écarts, dans l'ordre de détection (liste immuable)
public record RapportAudit(List<ConstatAudit> constats) {

    public RapportAudit {
        constats = List.copyOf(Objects.requireNonNull(constats, "constats"));
    }

    /// Nombre de constats d'une gravité donnée.
    public long nombre(Severite severite) {
        return constats.stream().filter(c -> c.severite() == severite).count();
    }

    /// `true` si l'audit n'a relevé aucun écart.
    public boolean sain() {
        return constats.isEmpty();
    }

    /// `true` si au moins un constat est une [Severite#ERREUR] (pilote le code de sortie CLI).
    public boolean aDesErreurs() {
        return nombre(Severite.ERREUR) > 0;
    }
}

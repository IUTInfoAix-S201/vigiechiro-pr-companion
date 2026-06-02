package fr.univ_amu.iut.commun.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/// Abstraction du temps : permet aux services de lire « la date du jour » sans dépendre directement
/// de [LocalDate#now()].
///
/// Pourquoi cette indirection ? Les règles métier de dates (R3 : fenêtres de passage, R4 :
/// intervalle entre deux passages) et l'horodatage des entités (date de création d'un site, date de
/// dépôt d'un lot) dépendent de « maintenant ». Si un service appelait `LocalDate.now()`
/// directement, ses tests seraient **non déterministes** (le résultat changerait chaque jour). En
/// injectant une `Horloge`, le test fournit une [HorlogeFigee] sur une date connue et
/// peut alors asserter exactement la date écrite en base.
///
/// En production, le socle (`CommunModule`) binde [#systeme()] (l'horloge réelle). En
/// test, on construit directement une [HorlogeFigee] et on la passe au constructeur du
/// service.
public interface Horloge {

    /// Date locale courante (ISO `AAAA-MM-JJ` via [LocalDate#toString()]).
    LocalDate aujourdhui();

    /// Instant local courant (utile pour un horodatage complet, ex. date/heure de dépôt).
    LocalDateTime maintenant();

    /// Horloge réelle, adossée à l'horloge système. C'est l'implémentation de production.
    static Horloge systeme() {
        return new HorlogeSysteme();
    }

    /// Horloge figée sur une date donnée (minuit) : réservée aux tests déterministes.
    static Horloge figeeAu(LocalDate jour) {
        return new HorlogeFigee(jour);
    }
}

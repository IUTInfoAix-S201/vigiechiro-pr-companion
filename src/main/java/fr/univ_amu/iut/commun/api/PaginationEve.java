package fr.univ_amu.iut.commun.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;

/// Parcours d'une collection **paginée Eve** (`_items` + `?max_results=&page=`) : accumule **toutes**
/// les pages jusqu'à la première page vide, avec un plafond de pages en garde-fou anti-boucle.
///
/// Extrait de [ClientVigieChiro] : la boucle de pagination est une préoccupation à part entière,
/// partagée par `donnees`, `mesSites` et `mesParticipations`. L'y factoriser évite d'alourdir le client
/// (God Class) et supprime la duplication de la boucle.
///
/// **Tout-ou-rien** (#1284) : un échec à la page N rend l'issue de cette page pour la collection
/// entière. Avant, l'échec « terminait » le parcours : une panne à la page 3 rendait les pages 1-2
/// comme si la collection était complète — un préfixe silencieux, la variante pire-que-vide de #1277.
final class PaginationEve {

    /// Taille de page demandée à Eve. **100 est le maximum accepté** : au-delà, le serveur ne tronque pas,
    /// il **rejette la requête** (`422`). Quand le transport dégradait tout échec HTTP en
    /// `Optional.empty()`, un dépassement ne se voyait pas : la collection entière revenait **vide, en
    /// silence** — import des observations, participations et sites (#1277). Le plafond est celui du
    /// `Paginator` du backend (`vigiechiro/xin/snippets.py`) ; le contrat live verrouille désormais
    /// `422 → Refuse`.
    static final int TAILLE_PAGE = 100;

    private PaginationEve() {}

    /// Suffixe de requête d'une page (`?max_results=…&page=…`). Le nombre de **pages** est libre, la
    /// **taille** de page ne l'est pas : cf. [#TAILLE_PAGE].
    static String requete(int page) {
        return "?max_results=" + TAILLE_PAGE + "&page=" + page;
    }

    /// @param pagesMax  plafond de pages (garde-fou anti-boucle)
    /// @param corpsPage renvoie le corps JSON de la page `n`, trié ([ReponseApi])
    /// @param parPage   parse une page en éléments ; une page **sans élément** marque la fin
    /// @return la collection **complète** en cas de succès ; sinon l'issue de la page fautive, sans
    ///     jamais rendre un préfixe des pages déjà lues
    static <T> ReponseApi<List<T>> parcourir(
            int pagesMax, IntFunction<ReponseApi<String>> corpsPage, Function<String, List<T>> parPage) {
        List<T> tout = new ArrayList<>();
        for (int page = 1; page <= pagesMax; page++) {
            ReponseApi<List<T>> lot = corpsPage.apply(page).transformer(parPage);
            if (!(lot instanceof ReponseApi.Succes<List<T>>(List<T> elements))) {
                return lot;
            }
            if (elements.isEmpty()) {
                break;
            }
            tout.addAll(elements);
        }
        return ReponseApi.succes(List.copyOf(tout));
    }
}

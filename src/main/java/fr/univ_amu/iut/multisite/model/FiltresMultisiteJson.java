package fr.univ_amu.iut.multisite.model;

import fr.univ_amu.iut.commun.model.JsonSimple;
import fr.univ_amu.iut.commun.model.StatutWorkflow;
import fr.univ_amu.iut.commun.model.Verdict;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Sérialisation aller-retour d'un {@link FiltresMultisite} vers la colonne {@code
 * saved_view.filters_json} (story E5.S3, « ⭐ Mes vues »).
 *
 * <p><b>Écriture</b> : déléguée à l'utilitaire partagé {@link JsonSimple} (format JSON
 * déterministe, sans dépendance externe — {@code pom.xml} et {@code module-info.java} sont gelés).
 * Seuls les critères <b>renseignés</b> sont émis (un critère {@code null} est absent du JSON), ce
 * qui produit un objet plat de paires {@code "clé":"valeur"} : {@code
 * {"site":"640380","annee":"2026"}}.
 *
 * <p><b>Lecture</b> : {@link JsonSimple} n'expose pas de parseur, on relit donc ici l'objet plat
 * produit à l'écriture (parseur minimal, symétrique de {@link JsonSimple#echapper(String)}). Les
 * énumérations sont relues par leur {@code libelle()} (cohérent avec le mapping des colonnes énum
 * du schéma), jamais par {@code name()}.
 *
 * <p>Les clés JSON ({@code site}, {@code statut}, {@code verdict}, {@code annee}) sont
 * <b>internes</b> à cette feature : elles décrivent le contenu de {@code filters_json}, indépendant
 * des noms de colonnes SQL.
 */
public final class FiltresMultisiteJson {

  private static final String CLE_SITE = "site";
  private static final String CLE_STATUT = "statut";
  private static final String CLE_VERDICT = "verdict";
  private static final String CLE_ANNEE = "annee";

  private FiltresMultisiteJson() {}

  /**
   * Sérialise les critères en JSON (déterministe : clés dans l'ordre site, statut, verdict, année).
   */
  public static String serialiser(FiltresMultisite filtres) {
    Objects.requireNonNull(filtres, "filtres");
    Map<String, String> champs = new LinkedHashMap<>();
    if (filtres.numeroCarre() != null) {
      champs.put(CLE_SITE, filtres.numeroCarre());
    }
    if (filtres.statut() != null) {
      champs.put(CLE_STATUT, filtres.statut().libelle());
    }
    if (filtres.verdict() != null) {
      champs.put(CLE_VERDICT, filtres.verdict().libelle());
    }
    if (filtres.annee() != null) {
      champs.put(CLE_ANNEE, String.valueOf(filtres.annee()));
    }
    return JsonSimple.objet(champs);
  }

  /** Reconstruit les critères depuis le JSON produit par {@link #serialiser(FiltresMultisite)}. */
  public static FiltresMultisite interpreter(String json) {
    Objects.requireNonNull(json, "json");
    Map<String, String> champs = parserObjetPlat(json);
    String site = champs.get(CLE_SITE);
    StatutWorkflow statut =
        champs.containsKey(CLE_STATUT) ? StatutWorkflow.parLibelle(champs.get(CLE_STATUT)) : null;
    Verdict verdict =
        champs.containsKey(CLE_VERDICT) ? Verdict.parLibelle(champs.get(CLE_VERDICT)) : null;
    Integer annee = champs.containsKey(CLE_ANNEE) ? Integer.valueOf(champs.get(CLE_ANNEE)) : null;
    return new FiltresMultisite(site, statut, verdict, annee);
  }

  /**
   * Parseur minimal d'un objet JSON <b>plat</b> à valeurs textuelles ({@code {"k":"v","k2":"v2"}}),
   * symétrique de {@link JsonSimple#objet(Map)}. Ne gère pas les objets/ tableaux imbriqués : c'est
   * suffisant pour {@code filters_json}, dont cette feature maîtrise le format des deux côtés.
   */
  private static Map<String, String> parserObjetPlat(String json) {
    Map<String, String> resultat = new LinkedHashMap<>();
    String s = json.strip();
    if (s.length() < 2 || s.charAt(0) != '{' || s.charAt(s.length() - 1) != '}') {
      throw new IllegalArgumentException("Objet JSON plat attendu : " + json);
    }
    int[] curseur = {1};
    int fin = s.length() - 1;
    while (true) {
      sauterEspacesEtVirgules(s, curseur, fin);
      if (curseur[0] >= fin) {
        return resultat;
      }
      String cle = lireChaine(s, curseur);
      sauterEspaces(s, curseur, fin);
      if (curseur[0] >= fin || s.charAt(curseur[0]) != ':') {
        throw new IllegalArgumentException("« : » attendu après une clé : " + json);
      }
      curseur[0]++;
      sauterEspaces(s, curseur, fin);
      if (curseur[0] < fin && s.charAt(curseur[0]) == '"') {
        resultat.put(cle, lireChaine(s, curseur));
      } else if (s.startsWith("null", curseur[0])) {
        curseur[0] += 4;
        resultat.put(cle, null);
      } else {
        throw new IllegalArgumentException("Valeur chaîne ou null attendue : " + json);
      }
    }
  }

  private static void sauterEspaces(String s, int[] curseur, int fin) {
    while (curseur[0] < fin && Character.isWhitespace(s.charAt(curseur[0]))) {
      curseur[0]++;
    }
  }

  private static void sauterEspacesEtVirgules(String s, int[] curseur, int fin) {
    while (curseur[0] < fin
        && (Character.isWhitespace(s.charAt(curseur[0])) || s.charAt(curseur[0]) == ',')) {
      curseur[0]++;
    }
  }

  /**
   * Lit une chaîne JSON à partir du guillemet ouvrant pointé par {@code curseur}, en dés-échappant
   * les séquences produites par {@link JsonSimple#echapper(String)} ({@code \" \\ \n \r \t} et
   * {@code \\uXXXX}). À la sortie, {@code curseur} pointe juste après le guillemet fermant.
   */
  private static String lireChaine(String s, int[] curseur) {
    int i = curseur[0];
    if (i >= s.length() || s.charAt(i) != '"') {
      throw new IllegalArgumentException("Guillemet ouvrant attendu dans : " + s);
    }
    i++;
    StringBuilder sb = new StringBuilder();
    while (i < s.length()) {
      char c = s.charAt(i++);
      if (c == '"') {
        curseur[0] = i;
        return sb.toString();
      }
      if (c == '\\') {
        char echappe = s.charAt(i++);
        switch (echappe) {
          case '"' -> sb.append('"');
          case '\\' -> sb.append('\\');
          case '/' -> sb.append('/');
          case 'n' -> sb.append('\n');
          case 'r' -> sb.append('\r');
          case 't' -> sb.append('\t');
          case 'b' -> sb.append('\b');
          case 'f' -> sb.append('\f');
          case 'u' -> {
            sb.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
            i += 4;
          }
          default -> throw new IllegalArgumentException("Échappement JSON invalide : \\" + echappe);
        }
      } else {
        sb.append(c);
      }
    }
    throw new IllegalArgumentException("Chaîne JSON non terminée : " + s);
  }
}

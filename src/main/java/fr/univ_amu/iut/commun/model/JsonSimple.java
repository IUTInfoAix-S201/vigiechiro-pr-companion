package fr.univ_amu.iut.commun.model;

import java.util.List;
import java.util.Map;

/// Sérialisation JSON minimale, sans dépendance externe (le module ne `requires` aucune
/// bibliothèque JSON, et `pom.xml`/`module-info.java` sont gelés).
///
/// Utilitaire **partagé** (paquet `commun.model`) : l'import l'utilise pour alimenter
/// les colonnes `TEXT` JSON du schéma (`passage.acquisition_params`, `sensor_log.parsed_events`,
/// `sensor_log.detected_anomalies`) et le diagnostic doit pouvoir
/// relire ces colonnes **avec le même format**. Les méthodes préservent un ordre d'insertion
/// stable ([java.util.LinkedHashMap]) pour rester **déterministes** (cohérent avec R11).
public final class JsonSimple {

  private JsonSimple() {}

  /// Échappe une chaîne pour l'insérer entre guillemets dans du JSON.
  public static String echapper(String valeur) {
    StringBuilder sb = new StringBuilder(valeur.length() + 2);
    for (int i = 0; i < valeur.length(); i++) {
      char c = valeur.charAt(i);
      switch (c) {
        case '"' -> sb.append("\\\"");
        case '\\' -> sb.append("\\\\");
        case '\n' -> sb.append("\\n");
        case '\r' -> sb.append("\\r");
        case '\t' -> sb.append("\\t");
        default -> {
          if (c < 0x20) {
            sb.append(String.format("\\u%04x", (int) c));
          } else {
            sb.append(c);
          }
        }
      }
    }
    return sb.toString();
  }

  /// Tableau JSON de chaînes : `["a","b"]`.
  public static String tableau(List<String> valeurs) {
    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < valeurs.size(); i++) {
      if (i > 0) {
        sb.append(',');
      }
      sb.append('"').append(echapper(valeurs.get(i))).append('"');
    }
    return sb.append(']').toString();
  }

  /// Objet JSON à valeurs textuelles (`null` → `null` JSON, sinon chaîne échappée). Les
  /// clés sont émises dans l'ordre d'itération de la `Map` fournie (utiliser une
  /// [java.util.LinkedHashMap] pour un rendu déterministe).
  public static String objet(Map<String, String> champs) {
    StringBuilder sb = new StringBuilder("{");
    boolean premier = true;
    for (Map.Entry<String, String> e : champs.entrySet()) {
      if (!premier) {
        sb.append(',');
      }
      premier = false;
      sb.append('"').append(echapper(e.getKey())).append("\":");
      if (e.getValue() == null) {
        sb.append("null");
      } else {
        sb.append('"').append(echapper(e.getValue())).append('"');
      }
    }
    return sb.append('}').toString();
  }
}

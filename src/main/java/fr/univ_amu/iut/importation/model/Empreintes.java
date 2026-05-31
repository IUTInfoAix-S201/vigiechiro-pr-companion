package fr.univ_amu.iut.importation.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/// Calcul d'empreintes **SHA-256** (hexadécimal minuscule) sur des fichiers ou des tableaux
/// d'octets.
///
/// Deux usages dans la feature import :
///
/// - **R9 (copie protégée)** : on hash chaque fichier de la carte SD avant et après la copie pour
/// prouver que la source n'a pas été modifiée (et que la copie est fidèle).
/// - **Intégrité bit-à-bit** : on stocke le SHA-256 de chaque enregistrement original
/// (`original_recording.sha256`) comme référence ultime.
///
/// Implémenté avec [MessageDigest] de `java.base` (aucune dépendance externe ni `java.desktop`).
public final class Empreintes {

  private Empreintes() {}

  /// SHA-256 hexadécimal d'un fichier, lu par blocs (n'occupe pas toute la RAM).
  public static String sha256Hex(Path fichier) {
    MessageDigest digest = nouveauSha256();
    byte[] tampon = new byte[1 << 16];
    try (InputStream flux = Files.newInputStream(fichier)) {
      int lus;
      while ((lus = flux.read(tampon)) != -1) {
        digest.update(tampon, 0, lus);
      }
    } catch (IOException e) {
      throw new IllegalStateException("Lecture impossible pour le SHA-256 : " + fichier, e);
    }
    return HexFormat.of().formatHex(digest.digest());
  }

  /// SHA-256 hexadécimal d'un tableau d'octets.
  public static String sha256Hex(byte[] octets) {
    return HexFormat.of().formatHex(nouveauSha256().digest(octets));
  }

  private static MessageDigest nouveauSha256() {
    try {
      return MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 indisponible sur cette JVM", e);
    }
  }
}

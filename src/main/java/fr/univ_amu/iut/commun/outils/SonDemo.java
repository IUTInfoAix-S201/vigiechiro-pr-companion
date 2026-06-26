package fr.univ_amu.iut.commun.outils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/// Générateur de WAV de démonstration pour les **outils de capture** d'écran.
///
/// Les `AudioView` des écrans d'écoute (qualification, validation, bibliothèque) lisent un WAV pour
/// en afficher un **sonogramme** et un **spectrogramme**. Pour que les aperçus PNG versionnés
/// montrent un signal réel et lisible (et non un cadre noir vide), on écrit un petit WAV **de
/// synthèse** : aucun fichier audio réel n'est embarqué, le signal est calculé.
///
/// Le signal imite une suite de **cris FM descendants** (≈14 kHz → 7 kHz) type Pipistrelle après
/// expansion temporelle : sonogramme pulsé et spectrogramme à strates obliques bien contrastées.
/// Déterministe (mêmes échantillons à chaque exécution), donc les PNG régénérés restent stables.
public final class SonDemo {

    private static final int FREQUENCE_ECHANTILLONNAGE = 44_100;
    private static final double DUREE_S = 3.0;
    private static final int NB_CRIS = 8;

    private SonDemo() {}

    /// Écrit à `cible` un WAV PCM 16 bits mono, 44,1 kHz, ~3 s : huit cris FM descendants
    /// (≈14 kHz → 7 kHz, enveloppe de Hann). Crée les dossiers parents au besoin.
    public static void ecrireCrisDemo(Path cible) throws IOException {
        final int nbEchantillons = (int) (DUREE_S * FREQUENCE_ECHANTILLONNAGE);
        final int dureeCri = (int) (0.06 * FREQUENCE_ECHANTILLONNAGE);
        short[] echantillons = new short[nbEchantillons];
        for (int c = 0; c < NB_CRIS; c++) {
            int debut = (int) ((0.12 + c * 0.34) * FREQUENCE_ECHANTILLONNAGE);
            double dureeS = dureeCri / (double) FREQUENCE_ECHANTILLONNAGE;
            for (int i = 0; i < dureeCri && debut + i < nbEchantillons; i++) {
                double tSec = i / (double) FREQUENCE_ECHANTILLONNAGE;
                // FM linéaire descendante : phase = 2π (f0·t + (f1-f0)/(2T)·t²).
                double phase = 2 * Math.PI * (14_000 * tSec + (7_000 - 14_000) / (2 * dureeS) * tSec * tSec);
                double enveloppe = 0.5 - 0.5 * Math.cos(2 * Math.PI * i / dureeCri); // Hann
                echantillons[debut + i] = (short) (enveloppe * Math.sin(phase) * 28_000);
            }
        }
        Files.createDirectories(cible.getParent());
        try (OutputStream sortie = Files.newOutputStream(cible)) {
            ecrireWav(sortie, echantillons, FREQUENCE_ECHANTILLONNAGE);
        }
    }

    /// Sérialise `echantillons` (PCM 16 bits mono) dans un conteneur WAV/RIFF minimal (little-endian).
    private static void ecrireWav(OutputStream sortie, short[] echantillons, int frequence) throws IOException {
        int tailleData = echantillons.length * 2;
        ByteBuffer buffer = ByteBuffer.allocate(44 + tailleData).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put("RIFF".getBytes(StandardCharsets.US_ASCII));
        buffer.putInt(36 + tailleData);
        buffer.put("WAVE".getBytes(StandardCharsets.US_ASCII));
        buffer.put("fmt ".getBytes(StandardCharsets.US_ASCII));
        buffer.putInt(16); // taille du sous-bloc fmt
        buffer.putShort((short) 1); // PCM
        buffer.putShort((short) 1); // mono
        buffer.putInt(frequence);
        buffer.putInt(frequence * 2); // débit octets/s
        buffer.putShort((short) 2); // alignement de bloc
        buffer.putShort((short) 16); // bits par échantillon
        buffer.put("data".getBytes(StandardCharsets.US_ASCII));
        buffer.putInt(tailleData);
        for (short echantillon : echantillons) {
            buffer.putShort(echantillon);
        }
        sortie.write(buffer.array());
    }
}

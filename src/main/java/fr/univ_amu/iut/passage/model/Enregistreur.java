package fr.univ_amu.iut.passage.model;

/**
 * Enregistreur de terrain : Passive Recorder Teensy déposé sur un point d'écoute (C4, table {@code
 * recorder}).
 *
 * <p>Son identité (le n° de série) est <b>lue depuis le journal du capteur</b> ({@code
 * LogPR<n>.txt} du firmware Teensy) au moment de l'import. C'est pourquoi sa clé est
 * <b>naturelle</b> ({@code serial_number}, en {@code TEXT}) et que {@link
 * fr.univ_amu.iut.passage.model.dao.EnregistreurDao#insert(Enregistreur)} fait un <b>upsert</b> :
 * si le même enregistreur est rencontré sur un nouveau passage, on rafraîchit ses métadonnées
 * plutôt que de planter sur un doublon de clé.
 *
 * @param numeroSerie n° de série, clé naturelle (ex. {@code 1925492})
 * @param versionModele modèle / version du firmware (optionnel, ex. {@code V1.01, T4.1})
 * @param commentaire commentaire libre (optionnel : anomalies, remises en état)
 */
public record Enregistreur(String numeroSerie, String versionModele, String commentaire) {}

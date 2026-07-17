# Diagnostic

Le **Diagnostic matériel** fait le point sur l'état du capteur pour une nuit donnée. C'est une
action transverse, accessible depuis l'écran [Passage](passage.md) quel que soit l'avancement.

![L'écran de diagnostic : courbe climatique, anomalies, évènements et cohérence horaire.](../assets/captures/apercu-diagnostic.png)

L'écran réunit :

- la **température en début de nuit** et une **courbe climatique** de la nuit (température et
  hygrométrie), dont l'axe est gradué en heures ;
- les **anomalies** détectées (par exemple réveil non programmé, batterie faible) ;
- les **évènements du journal** du capteur (démarrage, arrêt programmé...) ;
- la **cohérence horaire** : la fenêtre nocturne réelle au point d'écoute (heures de coucher et de
  lever du soleil), assortie d'une alerte « hors nuit » si l'enregistrement déborde de cette fenêtre
  (démarrage avant le coucher ou arrêt après le lever du soleil) ;
- l'état du **GPS du point d'écoute** : « disponible », ou « non renseigné » avec une invitation à
  compléter la fiche du site. Ce sont les coordonnées du point d'écoute (et non du capteur) ; le
  calcul de la fenêtre nocturne en dépend.

L'enregistreur diagnostiqué et le nombre de mesures climatiques figurent dans la **barre de statut**
en bas de la fenêtre.

## Sans relevé climatique

Si la nuit ne comporte pas de relevé climatique, l'absence est signalée et seules les anomalies et
les évènements du journal restent affichés.

![L'écran de diagnostic sans relevé climatique : l'absence est signalée.](../assets/captures/apercu-diagnostic-sans-releve.png)

## Sans coordonnées GPS

Si le point d'écoute n'a pas de coordonnées, le repère GPS passe à « non renseigné (compléter la
fiche site) » et l'encart cohérence horaire disparaît : la fenêtre nocturne ne peut pas être calculée
sans coordonnées. Le reste du diagnostic (courbe, anomalies, évènements) demeure exploitable.

![L'écran de diagnostic sans coordonnées GPS : repère « non renseigné », cohérence horaire absente.](../assets/captures/apercu-diagnostic-sans-gps.png)

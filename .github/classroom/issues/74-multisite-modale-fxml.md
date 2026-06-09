# 🗂 [multisite] 4/5 — Construire la modale ModaleVues.fxml

Quatrième sous-tâche : la **modale des vues sauvegardées** (placeholder → vraie vue).

> 💡 **Un écran complet pour vous inspirer.** L'écran **Passage** vous est fourni en correction (PR « 🧭 Aide : écran Passage ») : c'est un exemple **abouti** de cette même structure MVVM. Ouvrez `passage/view/RattachementModale.fxml` à côté de votre fichier et comparez. Le patron `sites` reste aussi une référence.

## Fichier à modifier (un seul)

`src/main/java/fr/univ_amu/iut/multisite/view/ModaleVues.fxml`

## Ce qu'il faut faire

1. Construisez la modale avec les `fx:id` attendus par `ModaleVuesController` : `racine` (l'élément
   racine, utilisé pour fermer la fenêtre), `listeVues` (ListView), `boutonAppliquer`,
   `boutonMettreAJour`, `boutonSupprimer`, `champNom`, `boutonEnregistrer`, `lblMessage`.
2. Reliez les boutons à leurs `onAction` (noms dans le controleur : `appliquer`, `mettreAJour`,
   `supprimer`, `enregistrer`, et un bouton « Fermer » → `fermer`). Gardez `fx:controller` vers
   `ModaleVuesController`.

## Les `fx:id` à déclarer (et leur type)

Votre FXML doit déclarer **exactement ces `fx:id`** (mêmes noms et mêmes types que ce qu'attend le controleur) :

| `fx:id` | Type | Rôle à l'écran |
|---|---|---|
| `racine` | `VBox` | racine du composant (`fx:root`) |
| `listeVues` | `ListView` | liste des vues enregistrées |
| `champNom` | `TextField` | nom de la vue |
| `boutonAppliquer` | `Button` | appliquer la vue sélectionnée |
| `boutonMettreAJour` | `Button` | mettre à jour la vue |
| `boutonSupprimer` | `Button` | supprimer la vue |
| `lblMessage` | `Label` | message |


## Pièges courants

- L'**élément racine** doit porter le bon `fx:controller` : sans lui, les `@FXML` du controleur ne
  seront pas injectés.
- Chaque `fx:id` doit être **exactement** celui du tableau (la casse compte) : un `fx:id` mal
  orthographié = un `@FXML` qui reste `null` au chargement.
- Le **type de balise doit correspondre** à la colonne « Type » et la balise doit être **importée**
  en tête du FXML (`<?import javafx.scene.control.ListView?>`, etc.).
- Pas de logique dans le FXML : seulement la **structure** (la donnée arrive par le câblage du controleur).

## Critères d'acceptation

- [ ] Le FXML se charge sans erreur ; tous les `fx:id` présents et du bon type.

## Definition of Done

- [ ] **Un seul fichier modifié** : `ModaleVues.fxml`.
- [ ] Câblage controleur en 5/5 ; un test rouge à cause du controleur est normal ici.
- [ ] Livré via **branche + PR relue**.

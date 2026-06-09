# 🔊 [bibliotheque] 2/3 — Construire la vue Bibliotheque.fxml

Deuxième sous-tâche : la mise en page (placeholder → vraie vue), avec le composant audio fourni.

> 💡 **Un écran complet pour vous inspirer.** L'écran **Passage** vous est fourni en correction (PR « 🧭 Aide : écran Passage ») : c'est un exemple **abouti** de cette même structure MVVM. Ouvrez `passage/view/Passage.fxml` à côté de votre fichier et comparez. Le patron `sites` reste aussi une référence.

## Fichier à modifier (un seul)

`src/main/java/fr/univ_amu/iut/bibliotheque/view/Bibliotheque.fxml`

## Ce qu'il faut faire

1. Étudiez `sites/view/MesSites.fxml` et `sites/view/SiteDetail.fxml`.
2. Construisez la vraie vue avec les `fx:id` attendus : `lblResume`, `tableEntrees` (TableView) et ses
   colonnes `colTaxon`, `colSequence`, `colFrequence`, `lblDetail`, `audioView`, `btnExporter`,
   `lblMessage`.
3. `audioView` est le **composant fourni** `AudioView` (réutilisé de M-Vision-Tadarida) : déclarez-le
   comme un nœud du même type que dans la vue d'origine. Reliez le bouton d'export à son `onAction`.
4. Gardez `fx:controller` vers `BibliothequeController`.

## Les `fx:id` à déclarer (et leur type)

Votre FXML doit déclarer **exactement ces `fx:id`** (mêmes noms et mêmes types que ce qu'attend le controleur) :

| `fx:id` | Type | Rôle à l'écran |
|---|---|---|
| `lblResume` | `Label` | résumé |
| `tableEntrees` | `TableView` | tableau des entrées (`setItems`) |
| `colTaxon, colSequence, colFrequence` | `TableColumn` | colonnes du tableau |
| `lblDetail` | `Label` | détail de l'entrée sélectionnée |
| `btnExporter` | `Button` | activé si la bibliothèque n'est pas vide |
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

- [ ] Le FXML se charge sans erreur ; tous les `fx:id` présents et du bon type (TableView, colonnes,
      AudioView, Button).

## Definition of Done

- [ ] **Un seul fichier modifié** : `Bibliotheque.fxml`.
- [ ] Câblage controleur en 3/3 ; un test rouge à cause du controleur est normal ici.
- [ ] Livré via **branche + PR relue**.

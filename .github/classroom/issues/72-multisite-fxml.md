# 🗂 [multisite] 2/5 — Construire la vue Multisite.fxml

Deuxième sous-tâche : la mise en page de l'écran principal (placeholder → vraie vue).

> 💡 **Un écran complet pour vous inspirer.** L'écran **Passage** vous est fourni en correction (PR « 🧭 Aide : écran Passage ») : c'est un exemple **abouti** de cette même structure MVVM. Ouvrez `passage/view/Passage.fxml` à côté de votre fichier et comparez. Le patron `sites` reste aussi une référence.

## Fichier à modifier (un seul)

`src/main/java/fr/univ_amu/iut/multisite/view/Multisite.fxml`

## Ce qu'il faut faire

1. Étudiez `sites/view/SiteDetail.fxml` (table + barre d'outils).
2. Construisez la vue avec les `fx:id` attendus : `lblResume` ; barre de filtres `champCarre`,
   `choixStatut`, `choixVerdict`, `champAnnee`, `boutonReinitialiser`, `boutonGererVues`, `choixTri`,
   `boutonExporter` ; le tableau `tableLignes` + colonnes `colCarre`, `colPoint`, `colAnnee`,
   `colNumero`, `colDate`, `colStatut`, `colVerdict` ; `lblMessage`.
3. Reliez les boutons à leurs `onAction` (noms dans le controleur : `reinitialiser`, `gererVues`,
   `exporter`). Gardez `fx:controller` vers `MultisiteController`.

## Les `fx:id` à déclarer (et leur type)

Votre FXML doit déclarer **exactement ces `fx:id`** (mêmes noms et mêmes types que ce qu'attend le controleur) :

| `fx:id` | Type | Rôle à l'écran |
|---|---|---|
| `lblResume` | `Label` | résumé |
| `champCarre, champAnnee` | `TextField` | filtres carré / année |
| `choixStatut` | `ComboBox` | filtre statut |
| `choixVerdict` | `ComboBox` | filtre verdict |
| `choixTri` | `ComboBox` | tri |
| `boutonExporter` | `Button` | activé si la table n'est pas vide |
| `boutonGererVues` | `Button` | ouvre la modale des vues |
| `tableLignes` | `TableView` | tableau des passages (`setItems`) |
| `colCarre, colPoint, colAnnee, colNumero, colDate, colStatut, colVerdict` | `TableColumn` | colonnes du tableau |
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

- [ ] **Un seul fichier modifié** : `Multisite.fxml`.
- [ ] Câblage controleur en 3/5 ; un test rouge à cause du controleur est normal ici.
- [ ] Livré via **branche + PR relue**.

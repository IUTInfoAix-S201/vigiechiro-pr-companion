# ✅ [validation] 2/3 — Construire la vue Validation.fxml

Deuxième sous-tâche : la mise en page (placeholder → vraie vue), avec table, filtres et composant audio.

> 💡 **Un écran complet pour vous inspirer.** L'écran **Passage** vous est fourni en correction (PR « 🧭 Aide : écran Passage ») : c'est un exemple **abouti** de cette même structure MVVM. Ouvrez `passage/view/Passage.fxml` à côté de votre fichier et comparez. Le patron `sites` reste aussi une référence.

## Fichier à modifier (un seul)

`src/main/java/fr/univ_amu/iut/validation/view/Validation.fxml`

## Ce qu'il faut faire

1. Étudiez `sites/view/SiteDetail.fxml` (table + colonnes) ; pour l'`AudioView`, inspirez-vous de la
   feature `bibliotheque` que vous venez de construire.
2. Construisez la vraie vue avec les `fx:id` attendus : `lblProgression`, `btnImporter`, `choixFiltre`
   (ComboBox), `tableObservations` (TableView) + colonnes `colEspece`, `colStatut`, `lblDetail`,
   `audioView`, `choixMode` (ComboBox), `btnValider`, `choixTaxon` (ComboBox), `btnCorriger`,
   `chkInclureMode` (CheckBox), `btnExporter`, `lblMessage`.
3. Reliez les boutons à leurs `onAction` (noms définis dans le controleur : `importer`, `valider`,
   `corriger`, `exporter`). Gardez `fx:controller` vers `ValidationController`.

## Les `fx:id` à déclarer (et leur type)

Votre FXML doit déclarer **exactement ces `fx:id`** (mêmes noms et mêmes types que ce qu'attend le controleur) :

| `fx:id` | Type | Rôle à l'écran |
|---|---|---|
| `lblProgression` | `Label` | progression |
| `btnImporter` | `Button` | activé si résultats disponibles |
| `choixFiltre` | `ComboBox` | filtre par statut |
| `tableObservations` | `TableView` | tableau des observations (`setItems`) |
| `colEspece, colStatut` | `TableColumn` | colonnes du tableau |
| `lblDetail` | `Label` | détail de l'observation |
| `choixMode` | `ComboBox` | mode de revue |
| `choixTaxon` | `ComboBox` | liste des taxons |
| `chkInclureMode` | `CheckBox` | inclure le mode à l'export |
| `btnValider` | `Button` | activé si une ligne est sélectionnée |
| `btnCorriger` | `Button` | action « corriger » |
| `btnExporter` | `Button` | action « exporter » |
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

- [ ] **Un seul fichier modifié** : `Validation.fxml`.
- [ ] Câblage controleur en 3/3 ; un test rouge à cause du controleur est normal ici.
- [ ] Livré via **branche + PR relue**.

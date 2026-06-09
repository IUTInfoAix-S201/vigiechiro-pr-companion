# 🔊 [bibliotheque] 3/3 — Câbler BibliothequeController

Dernière sous-tâche : relier la vue au ViewModel. Cet écran n'a pas de méthode `ouvrirSur` : il se
charge **tout seul** à l'ouverture (dans la méthode d'initialisation FXML).

> 💡 **Un écran complet pour vous inspirer.** L'écran **Passage** vous est fourni en correction (PR « 🧭 Aide : écran Passage ») : c'est un exemple **abouti** de cette même structure MVVM. Ouvrez `passage/view/PassageController.java` à côté de votre fichier et comparez. Le patron `sites` reste aussi une référence.

## Fichier à modifier (un seul)

`src/main/java/fr/univ_amu/iut/bibliotheque/view/BibliothequeController.java`

## Démarche (TDD)

1. **Activez** `BibliothequeViewTest`, **constatez le rouge**.
2. Patron : `SiteDetailController` (table + cellules) et la façon dont `sites` reconstruit ses cartes.
3. Dans le controleur : déclarez les champs `@FXML` (mêmes noms que les `fx:id`, dont l'`AudioView`),
   câblez la table et ses colonnes, la sélection, le détail, l'écoute (lier la source audio au son
   sélectionné), l'état du bouton d'export, et **déclenchez le chargement initial** (appel à `charger`
   du ViewModel) en fin d'initialisation. Ajoutez le **handler** `@FXML` d'export.
4. **Aucune logique métier**. Relancez jusqu'au **vert**.

## Le câblage attendu (`fx:id` → propriété du ViewModel)

Chaque champ `@FXML` porte le **même nom que son `fx:id`** et se relie à **une** propriété (les getters sont déjà fournis) :

| Champ `@FXML` (= `fx:id`) | Type | À relier à | Effet attendu |
|---|---|---|---|
| `lblResume` | `Label` | viewModel.resumeProperty() | résumé |
| `tableEntrees` | `TableView` | viewModel.entrees() | tableau des entrées (`setItems`) |
| `colTaxon, colSequence, colFrequence` | `TableColumn` | `setCellValueFactory` | colonnes du tableau |
| `lblDetail` | `Label` | — | détail de l'entrée sélectionnée |
| `btnExporter` | `Button` | viewModel.biblioNonVideProperty() | activé si la bibliothèque n'est pas vide |
| `lblMessage` | `Label` | viewModel.messageProperty() | message |


## Pièges courants

- Le **nom du champ `@FXML` doit être identique au `fx:id`** du FXML (sinon l'injection échoue →
  `NullPointerException` au chargement).
- **Aucune logique métier ni accès base** ici (règle ArchUnit `view_sans_jdbc`) : le controleur ne
  fait que **relier** propriétés et contrôles.
- On lie **dans le bon sens** : le **contrôle** se branche sur la propriété du ViewModel
  (`controle…Property().bind(viewModel…)`), jamais l'inverse.
- Pour une **liste / table**, on utilise `setItems(viewModel.xxx())` (pas un `bind`). Une `ComboBox`
  éditable se lie souvent en **bidirectionnel** (`bindBidirectional`).

## Critères d'acceptation

- [ ] **`BibliothequeViewTest` réactivé et vert** — critère d'acceptation de la feature.
- [ ] Dans l'appli, l'écran liste les sons, lit l'audio sélectionné et propose l'export.

## Definition of Done

- [ ] **Un seul fichier modifié** : `BibliothequeController.java` (+ retrait du `@Disabled`).
- [ ] Controleur sans logique métier (ArchUnit `view_sans_jdbc` vert) ; feature **verte** sans
      régression ; `spotless:check` OK.
- [ ] Livré via **branche + PR relue** ; la PR clôt l'épico.

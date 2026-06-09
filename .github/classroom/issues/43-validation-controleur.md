# ✅ [validation] 3/3 — Câbler ValidationController

Dernière sous-tâche : relier la vue au ViewModel et brancher les quatre actions.

> 💡 **Un écran complet pour vous inspirer.** L'écran **Passage** vous est fourni en correction (PR « 🧭 Aide : écran Passage ») : c'est un exemple **abouti** de cette même structure MVVM. Ouvrez `passage/view/PassageController.java` à côté de votre fichier et comparez. Le patron `sites` reste aussi une référence.

## Fichier à modifier (un seul)

`src/main/java/fr/univ_amu/iut/validation/view/ValidationController.java`

## Démarche (TDD)

1. **Activez** `ValidationViewTest`, **constatez le rouge**.
2. Patron : `SiteDetailController`. Dans le controleur : déclarez les champs `@FXML`, câblez la table
   et ses colonnes (libellés via les utilitaires fournis), le filtre, la sélection, le détail, l'écoute
   audio, les combos (mode, taxon), l'activation des boutons selon la sélection / la disponibilité des
   résultats, la progression et le message. Ajoutez les **handlers** `@FXML` des quatre boutons
   (import / valider / corriger / export) qui délèguent au ViewModel. Les sélecteurs de fichiers natifs
   (import / export) vivent dans la vue.
3. **Aucune logique métier**. Relancez jusqu'au **vert**.

## Le câblage attendu (`fx:id` → propriété du ViewModel)

Chaque champ `@FXML` porte le **même nom que son `fx:id`** et se relie à **une** propriété (les getters sont déjà fournis) :

| Champ `@FXML` (= `fx:id`) | Type | À relier à | Effet attendu |
|---|---|---|---|
| `lblProgression` | `Label` | viewModel.progressionProperty() | progression |
| `btnImporter` | `Button` | viewModel.resultatsDisponiblesProperty() | activé si résultats disponibles |
| `choixFiltre` | `ComboBox` | viewModel.filtreStatutProperty() (bidir.) | filtre par statut |
| `tableObservations` | `TableView` | viewModel.observationsFiltrees() | tableau des observations (`setItems`) |
| `colEspece, colStatut` | `TableColumn` | `setCellValueFactory` | colonnes du tableau |
| `lblDetail` | `Label` | viewModel.detailProperty() | détail de l'observation |
| `choixMode` | `ComboBox` | viewModel.modeRevueProperty() (bidir.) | mode de revue |
| `choixTaxon` | `ComboBox` | viewModel.taxons() (`setItems`) | liste des taxons |
| `chkInclureMode` | `CheckBox` | viewModel.inclureModeProperty() (bidir.) | inclure le mode à l'export |
| `btnValider` | `Button` | viewModel.selectionPresenteProperty() | activé si une ligne est sélectionnée |
| `btnCorriger` | `Button` | — | action « corriger » |
| `btnExporter` | `Button` | — | action « exporter » |
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

- [ ] **`ValidationViewTest` réactivé et vert** — critère d'acceptation de la feature.

## Definition of Done

- [ ] **Un seul fichier modifié** : `ValidationController.java` (+ retrait du `@Disabled`).
- [ ] Controleur sans logique métier (ArchUnit `view_sans_jdbc` vert) ; feature **verte** sans
      régression ; `spotless:check` OK.
- [ ] Livré via **branche + PR relue** ; la PR clôt l'épico.

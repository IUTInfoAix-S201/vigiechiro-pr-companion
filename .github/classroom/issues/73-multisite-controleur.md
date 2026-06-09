# 🗂 [multisite] 3/5 — Câbler MultisiteController

Troisième sous-tâche : relier la vue au ViewModel, brancher filtres/tri/export et le **double-clic**
d'ouverture d'un passage.

> 💡 **Un écran complet pour vous inspirer.** L'écran **Passage** vous est fourni en correction (PR « 🧭 Aide : écran Passage ») : c'est un exemple **abouti** de cette même structure MVVM. Ouvrez `passage/view/PassageController.java` à côté de votre fichier et comparez. Le patron `sites` reste aussi une référence.

## Fichier à modifier (un seul)

`src/main/java/fr/univ_amu/iut/multisite/view/MultisiteController.java`

## Contexte

Le controleur reçoit le ViewModel, le contrat d'ouverture de passage, et la navigation (pour ouvrir la
modale). Pas de `ouvrirSur` : il **charge le tableau tout seul** en fin d'initialisation FXML.

## Démarche (TDD)

1. **Activez** `MultisiteViewTest`, **constatez le rouge**.
2. Patron : `SiteDetailController` (en particulier le **double-clic** sur une ligne de table qui ouvre
   un passage via le contrat socle `OuvrirPassage` — sans dépendre de la feature `passage`).
3. Câblez le tableau et ses colonnes, les combos/champs de filtres (en liaison bidirectionnelle avec
   le ViewModel), le tri, le résumé, le message. Ajoutez les **handlers** `@FXML` : réinitialiser,
   exporter (sélecteur de fichier natif), et « Vues… » (qui demande à la navigation d'ouvrir la modale
   branchée sur **ce même** ViewModel). Le double-clic sur une ligne ouvre le passage correspondant.
4. **Aucune logique métier**. Relancez jusqu'au **vert**.

## Le câblage attendu (`fx:id` → propriété du ViewModel)

Chaque champ `@FXML` porte le **même nom que son `fx:id`** et se relie à **une** propriété (les getters sont déjà fournis) :

| Champ `@FXML` (= `fx:id`) | Type | À relier à | Effet attendu |
|---|---|---|---|
| `lblResume` | `Label` | viewModel.resumeProperty() | résumé |
| `champCarre, champAnnee` | `TextField` | — | filtres carré / année |
| `choixStatut` | `ComboBox` | viewModel.filtreStatutProperty() (bidir.) | filtre statut |
| `choixVerdict` | `ComboBox` | viewModel.filtreVerdictProperty() (bidir.) | filtre verdict |
| `choixTri` | `ComboBox` | viewModel.triProperty() (bidir.) | tri |
| `boutonExporter` | `Button` | viewModel.nonVideProperty() | activé si la table n'est pas vide |
| `boutonGererVues` | `Button` | — | ouvre la modale des vues |
| `tableLignes` | `TableView` | viewModel.lignes() | tableau des passages (`setItems`) |
| `colCarre, colPoint, colAnnee, colNumero, colDate, colStatut, colVerdict` | `TableColumn` | `setCellValueFactory` | colonnes du tableau |
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

- [ ] **`MultisiteViewTest` réactivé et vert**.
- [ ] Le double-clic sur une ligne ouvre l'écran du passage.

## Definition of Done

- [ ] **Un seul fichier modifié** : `MultisiteController.java` (+ retrait du `@Disabled`).
- [ ] Controleur sans logique métier (ArchUnit `view_sans_jdbc` vert) ; pas de régression ;
      `spotless:check` OK.
- [ ] Livré via **branche + PR relue**.

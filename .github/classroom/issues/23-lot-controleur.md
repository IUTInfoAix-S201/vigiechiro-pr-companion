# 📦 [lot] 3/3 — Câbler LotController

Dernière sous-tâche de **M-Lot** : relier la vue au ViewModel et brancher les deux boutons.

> 💡 **Un écran complet pour vous inspirer.** L'écran **Passage** vous est fourni en correction (PR « 🧭 Aide : écran Passage ») : c'est un exemple **abouti** de cette même structure MVVM. Ouvrez `passage/view/PassageController.java` à côté de votre fichier et comparez. Le patron `sites` reste aussi une référence.

## Fichier à modifier (un seul)

`src/main/java/fr/univ_amu/iut/lot/view/LotController.java`

## Démarche (TDD)

1. **Activez** `LotViewTest` (retirez `@Disabled`), **constatez le rouge**.
2. Patron : `SiteDetailController`.
3. Dans `LotController` : déclarez les champs `@FXML` (mêmes noms que les `fx:id`), liez-les aux
   propriétés du ViewModel dans la méthode d'initialisation FXML (statut, récap, dossier, liste
   d'alertes, état activé/désactivé des boutons selon les disponibilités, message), et écrivez les
   **handlers** `@FXML` des deux boutons qui délèguent à `preparer` / `deposer` du ViewModel. La zone
   d'alertes ne doit apparaître qu'en présence d'alertes.
4. **Aucune logique métier** ici. Relancez jusqu'au **vert**.

## Le câblage attendu (`fx:id` → propriété du ViewModel)

Chaque champ `@FXML` porte le **même nom que son `fx:id`** et se relie à **une** propriété (les getters sont déjà fournis) :

| Champ `@FXML` (= `fx:id`) | Type | À relier à | Effet attendu |
|---|---|---|---|
| `lblStatut` | `Label` | viewModel.statutProperty() | statut du lot |
| `lblRecap` | `Label` | viewModel.recapProperty() | récapitulatif |
| `lblCheminDossier` | `Label` | viewModel.cheminDossierProperty() | chemin du dossier |
| `zoneAlertes` | `VBox` | — | conteneur des alertes |
| `listeAlertes` | `ListView` | viewModel.alertes() | liste des alertes |
| `btnPreparer` | `Button` | viewModel.peutPreparerProperty() | activé/désactivé selon l'état |
| `btnDeposer` | `Button` | viewModel.peutDeposerProperty() | activé/désactivé selon l'état |
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

- [ ] **`LotViewTest` réactivé et vert** — critère d'acceptation de la feature.
- [ ] Dans l'appli, les boutons s'activent/désactivent selon le statut et font progresser le dépôt.

## Definition of Done

- [ ] **Un seul fichier modifié** : `LotController.java` (+ retrait du `@Disabled` du test).
- [ ] Controleur sans logique métier ni accès base (ArchUnit `view_sans_jdbc` vert).
- [ ] Toute la feature lot **verte**, sans régression ; `spotless:check` OK.
- [ ] Livré via **branche + PR relue** ; la PR clôt l'épico (`Closes #…`).

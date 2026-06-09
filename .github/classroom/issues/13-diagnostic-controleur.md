# 🔧 [diagnostic] 3/3 — Câbler DiagnosticController

Dernière sous-tâche de **M-Diagnostic** : relier la vue (les `@FXML`) au ViewModel. C'est ce câblage
qui rend l'écran vivant et fait passer le test d'acceptation au vert.

> 💡 **Un écran complet pour vous inspirer.** L'écran **Passage** vous est fourni en correction (PR « 🧭 Aide : écran Passage ») : c'est un exemple **abouti** de cette même structure MVVM. Ouvrez `passage/view/PassageController.java` à côté de votre fichier et comparez. Le patron `sites` reste aussi une référence.

## Fichier à modifier (un seul)

`src/main/java/fr/univ_amu/iut/diagnostic/view/DiagnosticController.java`

## Contexte

Le controleur est une **coquille** : la classe, le constructeur `@Inject` (qui reçoit le
`DiagnosticViewModel`) et la méthode `ouvrirSur` (appelée par la navigation) sont fournis. Un
commentaire `// TODO (M-Diagnostic) …` indique ce qui reste à écrire.

## Démarche (TDD)

1. **Activez le test d'acceptation** : retirez le `@Disabled` de
   `src/test/java/.../diagnostic/view/DiagnosticViewTest.java`, lancez-le et **constatez le rouge**.
2. **Étudiez le patron** : ouvrez le controleur de référence `SiteDetailController` (feature `sites`).
   Il illustre le câblage « pur » : déclarer des champs `@FXML`, et tout lier dans une méthode
   `initialize()`.
3. Dans `DiagnosticController` :
   - déclarez les **champs `@FXML`** correspondant aux `fx:id` de votre `Diagnostic.fxml` (mêmes
     noms) ;
   - écrivez la méthode d'initialisation FXML qui **lie** chaque contrôle à la propriété
     correspondante du ViewModel : le texte des labels aux propriétés texte, les listes aux listes
     observables, et la **reconstruction du graphe** à partir de la série de mesures du ViewModel ;
   - **aucune logique métier ni accès base** : uniquement du câblage propriété ↔ contrôle.
4. Relancez `DiagnosticViewTest` jusqu'au **vert**.

## Le câblage attendu (`fx:id` → propriété du ViewModel)

Chaque champ `@FXML` porte le **même nom que son `fx:id`** et se relie à **une** propriété (les getters sont déjà fournis) :

| Champ `@FXML` (= `fx:id`) | Type | À relier à | Effet attendu |
|---|---|---|---|
| `lblEnregistreur` | `Label` | viewModel.enregistreurProperty() | enregistreur de la nuit |
| `lblResumeClimat` | `Label` | viewModel.resumeClimatProperty() | résumé de la série climatique |
| `lblReleveAbsent` | `Label` | viewModel.releveClimatiqueAbsentProperty() | alerte « relevé absent » (R20), affichée/masquée |
| `grapheClimat` | `LineChart` | viewModel.mesures() | courbe T°/hygrométrie (reconstruite quand la liste change) |
| `listeAnomalies` | `ListView` | viewModel.anomalies() | anomalies (R19) |
| `listeEvenements` | `ListView` | viewModel.evenements() | évènements du journal |
| `lblGps` | `Label` | viewModel.gpsDisponibleProperty() | disponibilité GPS |
| `lblMessage` | `Label` | viewModel.messageProperty() | message d'erreur |


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

- [ ] **`DiagnosticViewTest` est réactivé et vert** (graphe à deux séries, listes peuplées,
      enregistreur affiché) — c'est le critère d'acceptation **de toute la feature**.
- [ ] Dans l'appli (`./mvnw javafx:run`), ouvrir un passage puis l'onglet « Diagnostic matériel »
      affiche le vrai écran (plus le placeholder).

## Definition of Done

- [ ] **Un seul fichier modifié** : `DiagnosticController.java` (+ retrait du `@Disabled` du test).
- [ ] Le controleur ne contient **aucune** logique métier ni accès base (règle ArchUnit
      `view_sans_jdbc` verte).
- [ ] `./mvnw -Dglass.platform=Headless -Dprism.order=sw test` : **toute la feature diagnostic est
      verte**, sans régression ailleurs ; `./mvnw spotless:check` OK.
- [ ] Modification livrée via une **branche + PR relue** ; la PR clôt l'issue chapeau (`Closes #…`).

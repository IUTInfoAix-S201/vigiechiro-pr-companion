# 🎧 [qualification] 4/4 — Câbler QualificationController

Dernière sous-tâche : relier la vue aux **deux** ViewModels et brancher les actions (verdict, écoute,
régénération, raccourcis clavier).

> 💡 **Un écran complet pour vous inspirer.** L'écran **Passage** vous est fourni en correction (PR « 🧭 Aide : écran Passage ») : c'est un exemple **abouti** de cette même structure MVVM. Ouvrez `passage/view/PassageController.java` à côté de votre fichier et comparez. Le patron `sites` reste aussi une référence.

## Fichier à modifier (un seul)

`src/main/java/fr/univ_amu/iut/qualification/view/QualificationController.java`

## Contexte

Le controleur reçoit les **deux** ViewModels + le contrat d'ouverture du passage. Sa méthode `ouvrirSur`
(fournie) synchronise déjà les deux ViewModels sur le même passage : ne la touchez pas.

## Démarche (TDD)

1. **Activez** `QualificationViewTest`, **constatez le rouge**.
2. Patron : `SiteDetailController`. Câblez le bandeau et les feux au ViewModel verdict ; la table, la
   progression, le détail et l'écoute au ViewModel sélection ; la surbrillance du verdict choisi,
   l'aperçu R14, l'avertissement et l'activation du bouton « Enregistrer » au ViewModel verdict.
   Ajoutez les **handlers** `@FXML` (poser O/D/J, enregistrer, régénérer, retour passage). Vous pouvez
   ajouter les raccourcis clavier (O/D/J, Entrée, Espace) si le test les couvre.
3. **Aucune logique métier**. Relancez jusqu'au **vert**.

## Le câblage attendu (`fx:id` → propriété du ViewModel)

Chaque champ `@FXML` porte le **même nom que son `fx:id`** et se relie à **une** propriété (les getters sont déjà fournis) :


> ℹ️ Cet écran s'appuie sur **deux** ViewModels : `selectionVm` (le contexte et la liste des séquences) et `verdictVm` (la saisie du verdict). La colonne « À relier à » indique lequel utiliser.

| Champ `@FXML` (= `fx:id`) | Type | À relier à | Effet attendu |
|---|---|---|---|
| `lienRetourPassage` | `Hyperlink` | selectionVm.filArianeProperty() (désactivé si vide) | retour vers Passage |
| `lblFilAriane` | `Label` | selectionVm.filArianeProperty() | fil d'Ariane |
| `lblTitreContexte` | `Label` | selectionVm.titreContexteProperty() | titre du contexte |
| `lblPlageHoraire` | `Label` | selectionVm.plageHoraireProperty() | plage horaire |
| `lblVolumetrie` | `Label` | selectionVm.volumetrieProperty() | volumétrie |
| `lblVerdictActuel, lblStatut` | `Label` | — | verdict actuel / statut |
| `feuCouverture, feuNombre, feuRenommage` | `Label` | — | voyants de pré-check |
| `lblAnomalie` | `Label` | verdictVm.preCheckAnomalieProperty() (visible/managed) | anomalie de pré-check |
| `barreProgression` | `ProgressBar` | selectionVm.progressionProperty() | progression |
| `lblProgression` | `Label` | selectionVm.progressionTexteProperty() | texte de progression |
| `tableSequences` | `TableView` | selectionVm.lignes() | tableau des séquences (`setItems`) |
| `colPosition, colFichier, colDuree, colEcoute` | `TableColumn` | `setCellValueFactory` | colonnes du tableau |
| `audioView` | `AudioView` | selectionVm.cheminSequenceCouranteProperty() | lecteur audio de la séquence |
| `lblSeqNumero, lblSeqMeta` | `Label` | — | n° et méta de la séquence |
| `boutonOk, boutonDouteux, boutonAJeter` | `Button` | — | verdict OK / douteux / à jeter |
| `champCommentaire` | `TextArea` | verdictVm.commentaireProperty() (bidir.) | commentaire |
| `lblAvertissement` | `Label` | verdictVm.avertissementAJeterProperty() | avertissement « à jeter » |
| `boutonEnregistrer` | `Button` | verdictVm.peutEnregistrer() | activé si enregistrable |
| `lblMessage` | `Label` | verdictVm.messageProperty() | message |


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

- [ ] **`QualificationViewTest` réactivé et vert** — critère d'acceptation de la feature.

## Definition of Done

- [ ] **Un seul fichier modifié** : `QualificationController.java` (+ retrait du `@Disabled`).
- [ ] Controleur sans logique métier (ArchUnit `view_sans_jdbc` vert) ; feature **verte** sans
      régression ; `spotless:check` OK.
- [ ] Livré via **branche + PR relue** ; la PR clôt l'épico.

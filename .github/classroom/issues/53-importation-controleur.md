# 📥 [importation] 3/3 — Câbler ImportationController (import en arrière-plan)

Dernière sous-tâche : relier la vue au ViewModel **et** lancer l'import lourd hors du fil JavaFX pour
ne pas figer l'IHM.

> 💡 **Un écran complet pour vous inspirer.** L'écran **Passage** vous est fourni en correction (PR « 🧭 Aide : écran Passage ») : c'est un exemple **abouti** de cette même structure MVVM. Ouvrez `passage/view/PassageController.java` à côté de votre fichier et comparez. Le patron `sites` reste aussi une référence.

## Fichier à modifier (un seul)

`src/main/java/fr/univ_amu/iut/importation/view/ImportationController.java`

## Démarche (TDD)

1. **Activez** `ImportationViewTest`, **constatez le rouge**.
2. Patron : `SiteDetailController`. Cet écran n'a pas de `ouvrirSur` : il **charge les sites tout
   seul** en fin d'initialisation FXML.
3. Dans le controleur : déclarez les champs `@FXML`, câblez les 4 sections (affichage du chemin,
   visibilité de l'inspection, libellés et avertissements, combos site/point, champs année/passage,
   aperçu du préfixe, activation du bouton importer, zone de progression). Handlers `@FXML` :
   - **parcourir** : ouvrir le sélecteur de dossier natif puis lancer l'inspection ;
   - **importer** : capturer la demande, passer en « en cours », puis exécuter l'import **sur un fil
     d'arrière-plan** (Java propose les *virtual threads*) en relayant la progression et le résultat
     **sur le fil JavaFX**. Étudiez bien la doc des méthodes `preparerImport` / `executerImport` /
     `marquer…` du ViewModel : elles sont conçues pour ce découpage thread-safe.
4. **Aucune logique métier**. Relancez jusqu'au **vert**.

## Le câblage attendu (`fx:id` → propriété du ViewModel)

Chaque champ `@FXML` porte le **même nom que son `fx:id`** et se relie à **une** propriété (les getters sont déjà fournis) :

| Champ `@FXML` (= `fx:id`) | Type | À relier à | Effet attendu |
|---|---|---|---|
| `champDossier` | `TextField` | — | chemin du dossier à importer |
| `boutonParcourir` | `Button` | — | ouvre le sélecteur de dossier |
| `sectionInspection` | `VBox` | viewModel.inspecteProperty() (visible/managed) | section affichée après inspection |
| `labelJournal, labelReleve, labelOriginaux, labelNommage` | `Label` | — | résultats d'inspection (présence/cohérence) |
| `labelMelange` | `Label` | viewModel.avertissementMelangeProperty() | avertissement « mélange » |
| `labelIncoherence` | `Label` | viewModel.avertissementIncoherenceProperty() | avertissement « incohérence » |
| `comboSites` | `ComboBox` | viewModel.sites() + siteSelectionneProperty() (bidir.) | site (liste + sélection) |
| `comboPoints` | `ComboBox` | viewModel.points() + pointSelectionneProperty() (bidir.) | point d'écoute |
| `champAnnee` | `TextField` | viewModel.anneeProperty() (bidir.) | année |
| `champPassage` | `TextField` | viewModel.numeroPassageProperty() (bidir.) | n° de passage |
| `labelApercu` | `Label` | viewModel.apercuPrefixeProperty() | aperçu du préfixe |
| `boutonImporter` | `Button` | viewModel.peutImporter() | activé si l'import est possible |
| `zoneProgression` | `VBox` | — | zone de progression |
| `barreProgression` | `ProgressBar` | viewModel.progressionProperty() | barre de progression |
| `labelProgression` | `Label` | viewModel.messageProgressionProperty() | message de progression |
| `labelMessage` | `Label` | viewModel.messageErreurProperty() | message d'erreur |
| `labelStatut` | `Label` | — | statut |


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

- [ ] **`ImportationViewTest` réactivé et vert** — critère d'acceptation de la feature.
- [ ] Pendant un import, l'IHM **ne gèle pas** ; la barre de progression avance ; le formulaire est gelé.

## Definition of Done

- [ ] **Un seul fichier modifié** : `ImportationController.java` (+ retrait du `@Disabled`).
- [ ] Controleur sans logique métier (ArchUnit `view_sans_jdbc` vert) ; feature **verte** sans
      régression ; `spotless:check` OK.
- [ ] Livré via **branche + PR relue** ; la PR clôt l'épico.

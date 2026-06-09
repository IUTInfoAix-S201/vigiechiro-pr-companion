# 📥 [importation] 2/3 — Construire la vue Importation.fxml

Deuxième sous-tâche : la mise en page de l'assistant (4 sections), placeholder → vraie vue.

> 💡 **Un écran complet pour vous inspirer.** L'écran **Passage** vous est fourni en correction (PR « 🧭 Aide : écran Passage ») : c'est un exemple **abouti** de cette même structure MVVM. Ouvrez `passage/view/Passage.fxml` à côté de votre fichier et comparez. Le patron `sites` reste aussi une référence.

## Fichier à modifier (un seul)

`src/main/java/fr/univ_amu/iut/importation/view/Importation.fxml`

## Ce qu'il faut faire

1. Étudiez `sites/view/SiteDetail.fxml` pour les conteneurs et les sections.
2. Construisez les 4 sections avec les `fx:id` attendus : `champDossier`, `boutonParcourir` ;
   `sectionInspection`, `labelJournal`, `labelReleve`, `labelOriginaux`, `labelNommage`,
   `labelMelange`, `labelIncoherence` ; `comboSites`, `comboPoints`, `champAnnee`, `champPassage`,
   `labelApercu`, `boutonImporter` ; `zoneProgression`, `barreProgression`, `labelProgression`,
   `labelStatut`, `labelMessage`.
3. Reliez les boutons à leurs `onAction` (`parcourir`, `importer`). Gardez `fx:controller` vers
   `ImportationController`.

## Les `fx:id` à déclarer (et leur type)

Votre FXML doit déclarer **exactement ces `fx:id`** (mêmes noms et mêmes types que ce qu'attend le controleur) :

| `fx:id` | Type | Rôle à l'écran |
|---|---|---|
| `champDossier` | `TextField` | chemin du dossier à importer |
| `boutonParcourir` | `Button` | ouvre le sélecteur de dossier |
| `sectionInspection` | `VBox` | section affichée après inspection |
| `labelJournal, labelReleve, labelOriginaux, labelNommage` | `Label` | résultats d'inspection (présence/cohérence) |
| `labelMelange` | `Label` | avertissement « mélange » |
| `labelIncoherence` | `Label` | avertissement « incohérence » |
| `comboSites` | `ComboBox` | site (liste + sélection) |
| `comboPoints` | `ComboBox` | point d'écoute |
| `champAnnee` | `TextField` | année |
| `champPassage` | `TextField` | n° de passage |
| `labelApercu` | `Label` | aperçu du préfixe |
| `boutonImporter` | `Button` | activé si l'import est possible |
| `zoneProgression` | `VBox` | zone de progression |
| `barreProgression` | `ProgressBar` | barre de progression |
| `labelProgression` | `Label` | message de progression |
| `labelMessage` | `Label` | message d'erreur |
| `labelStatut` | `Label` | statut |


## Pièges courants

- L'**élément racine** doit porter le bon `fx:controller` : sans lui, les `@FXML` du controleur ne
  seront pas injectés.
- Chaque `fx:id` doit être **exactement** celui du tableau (la casse compte) : un `fx:id` mal
  orthographié = un `@FXML` qui reste `null` au chargement.
- Le **type de balise doit correspondre** à la colonne « Type » et la balise doit être **importée**
  en tête du FXML (`<?import javafx.scene.control.ListView?>`, etc.).
- Pas de logique dans le FXML : seulement la **structure** (la donnée arrive par le câblage du controleur).

## Critères d'acceptation

- [ ] Le FXML se charge sans erreur ; tous les `fx:id` présents et du bon type (combos, champs, barre
      de progression…).

## Definition of Done

- [ ] **Un seul fichier modifié** : `Importation.fxml`.
- [ ] Câblage controleur en 3/3 ; un test rouge à cause du controleur est normal ici.
- [ ] Livré via **branche + PR relue**.

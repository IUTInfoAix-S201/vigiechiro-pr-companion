# 🎧 [qualification] 3/4 — Construire la vue Qualification.fxml

Troisième sous-tâche : la mise en page (placeholder → vraie vue) en deux colonnes (liste d'écoute à
gauche, détail + verdict à droite).

> 💡 **Un écran complet pour vous inspirer.** L'écran **Passage** vous est fourni en correction (PR « 🧭 Aide : écran Passage ») : c'est un exemple **abouti** de cette même structure MVVM. Ouvrez `passage/view/Passage.fxml` à côté de votre fichier et comparez. Le patron `sites` reste aussi une référence.

## Fichier à modifier (un seul)

`src/main/java/fr/univ_amu/iut/qualification/view/Qualification.fxml`

## Ce qu'il faut faire

1. Étudiez `sites/view/SiteDetail.fxml` (bandeau + table) ; pour l'`AudioView`, réutilisez ce que vous
   avez fait en `bibliotheque` / `validation`.
2. Construisez la vue avec les `fx:id` attendus (visibles dans `QualificationController`), notamment :
   `racine`, fil d'Ariane et bandeau (`lblFilAriane`, `lblTitreContexte`, `lblPlageHoraire`,
   `lblVolumetrie`, `lblVerdictActuel`, `lblStatut`), les 3 feux (`feuCouverture`, `feuNombre`,
   `feuRenommage`, `lblAnomalie`), la sélection (`tableSequences` + colonnes `colPosition`,
   `colFichier`, `colDuree`, `colEcoute`, `barreProgression`, `lblProgression`), le détail
   (`lblSeqNumero`, `lblSeqMeta`, `audioView`), le verdict (`boutonOk`, `boutonDouteux`, `boutonAJeter`,
   `champCommentaire`, `lblApercuR14`, `lblAvertissement`, `boutonEnregistrer`), `lblMessage`.
3. Reliez les boutons à leurs `onAction` (noms dans le controleur). Gardez `fx:controller` vers
   `QualificationController`.

## Les `fx:id` à déclarer (et leur type)

Votre FXML doit déclarer **exactement ces `fx:id`** (mêmes noms et mêmes types que ce qu'attend le controleur) :

| `fx:id` | Type | Rôle à l'écran |
|---|---|---|
| `lienRetourPassage` | `Hyperlink` | retour vers Passage |
| `lblFilAriane` | `Label` | fil d'Ariane |
| `lblTitreContexte` | `Label` | titre du contexte |
| `lblPlageHoraire` | `Label` | plage horaire |
| `lblVolumetrie` | `Label` | volumétrie |
| `lblVerdictActuel, lblStatut` | `Label` | verdict actuel / statut |
| `feuCouverture, feuNombre, feuRenommage` | `Label` | voyants de pré-check |
| `lblAnomalie` | `Label` | anomalie de pré-check |
| `barreProgression` | `ProgressBar` | progression |
| `lblProgression` | `Label` | texte de progression |
| `tableSequences` | `TableView` | tableau des séquences (`setItems`) |
| `colPosition, colFichier, colDuree, colEcoute` | `TableColumn` | colonnes du tableau |
| `audioView` | `AudioView` | lecteur audio de la séquence |
| `lblSeqNumero, lblSeqMeta` | `Label` | n° et méta de la séquence |
| `boutonOk, boutonDouteux, boutonAJeter` | `Button` | verdict OK / douteux / à jeter |
| `champCommentaire` | `TextArea` | commentaire |
| `lblAvertissement` | `Label` | avertissement « à jeter » |
| `boutonEnregistrer` | `Button` | activé si enregistrable |
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

- [ ] **Un seul fichier modifié** : `Qualification.fxml`.
- [ ] Câblage controleur en 4/4 ; un test rouge à cause du controleur est normal ici.
- [ ] Livré via **branche + PR relue**.

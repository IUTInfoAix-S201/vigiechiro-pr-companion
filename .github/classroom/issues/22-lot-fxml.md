# 📦 [lot] 2/3 — Construire la vue Lot.fxml

Deuxième sous-tâche de **M-Lot** : la mise en page (placeholder → vraie vue).

> 💡 **Un écran complet pour vous inspirer.** L'écran **Passage** vous est fourni en correction (PR « 🧭 Aide : écran Passage ») : c'est un exemple **abouti** de cette même structure MVVM. Ouvrez `passage/view/Passage.fxml` à côté de votre fichier et comparez. Le patron `sites` reste aussi une référence.

## Fichier à modifier (un seul)

`src/main/java/fr/univ_amu/iut/lot/view/Lot.fxml`

## Ce qu'il faut faire

1. Étudiez la vue de référence `sites/view/SiteDetail.fxml`.
2. Construisez la vraie mise en page de M-Lot, en déclarant les `fx:id` attendus par le controleur :
   `lblStatut`, `lblRecap`, `lblCheminDossier`, `zoneAlertes` (le conteneur des alertes),
   `listeAlertes` (ListView), `btnPreparer`, `btnDeposer`, `lblMessage`.
3. Reliez les deux boutons à leurs actions via `onAction` (les noms d'actions sont définis dans le
   controleur : `preparer`, `deposer`).
4. Gardez `fx:controller` vers `LotController` sur l'élément racine.

## Les `fx:id` à déclarer (et leur type)

Votre FXML doit déclarer **exactement ces `fx:id`** (mêmes noms et mêmes types que ce qu'attend le controleur) :

| `fx:id` | Type | Rôle à l'écran |
|---|---|---|
| `lblStatut` | `Label` | statut du lot |
| `lblRecap` | `Label` | récapitulatif |
| `lblCheminDossier` | `Label` | chemin du dossier |
| `zoneAlertes` | `VBox` | conteneur des alertes |
| `listeAlertes` | `ListView` | liste des alertes |
| `btnPreparer` | `Button` | activé/désactivé selon l'état |
| `btnDeposer` | `Button` | activé/désactivé selon l'état |
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

- [ ] Le FXML se charge sans erreur ; tous les `fx:id` ci-dessus sont présents et du bon type.
- [ ] Les boutons « Préparer » / « Marquer déposé » déclarent bien leur `onAction`.

## Definition of Done

- [ ] **Un seul fichier modifié** : `Lot.fxml`.
- [ ] Le câblage controleur arrive en 3/3 : un test encore rouge à cause du controleur est normal ici.
- [ ] Livré via **branche + PR relue** référençant l'issue.

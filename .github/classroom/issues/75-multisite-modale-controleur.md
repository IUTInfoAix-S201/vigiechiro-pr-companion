# 🗂 [multisite] 5/5 — Câbler ModaleVuesController

Dernière sous-tâche : relier la modale au ViewModel **partagé** avec l'écran principal (de sorte
qu'appliquer une vue met à jour les filtres et le tableau dessous).

> 💡 **Un écran complet pour vous inspirer.** L'écran **Passage** vous est fourni en correction (PR « 🧭 Aide : écran Passage ») : c'est un exemple **abouti** de cette même structure MVVM. Ouvrez `passage/view/RattachementModaleController.java` à côté de votre fichier et comparez. Le patron `sites` reste aussi une référence.

## Fichier à modifier (un seul)

`src/main/java/fr/univ_amu/iut/multisite/view/ModaleVuesController.java`

## Contexte

La méthode publique `demarrer` (fournie, appelée par la navigation) reçoit le `MultisiteViewModel` de
l'écran : son corps est à compléter (balisé) pour brancher la liste + charger les vues.

## Démarche (TDD)

1. **Activez** `ModaleVuesViewTest`, **constatez le rouge**.
2. Déclarez les champs `@FXML`, câblez la liste des vues (cellules par nom), l'activation des boutons
   selon la sélection, la pré-saisie du nom, et le message ; dans `demarrer`, branchez la liste sur le
   ViewModel partagé et chargez les vues. Ajoutez les **handlers** `@FXML` (enregistrer, appliquer,
   mettre à jour, supprimer, fermer).
3. **Aucune logique métier**. Relancez jusqu'au **vert**.

## Le câblage attendu (`fx:id` → propriété du ViewModel)

Chaque champ `@FXML` porte le **même nom que son `fx:id`** et se relie à **une** propriété (les getters sont déjà fournis) :

| Champ `@FXML` (= `fx:id`) | Type | À relier à | Effet attendu |
|---|---|---|---|
| `racine` | `VBox` | — | racine du composant (`fx:root`) |
| `listeVues` | `ListView` | viewModel.vues() (`setItems`) | liste des vues enregistrées |
| `champNom` | `TextField` | — | nom de la vue |
| `boutonAppliquer` | `Button` | — | appliquer la vue sélectionnée |
| `boutonMettreAJour` | `Button` | — | mettre à jour la vue |
| `boutonSupprimer` | `Button` | — | supprimer la vue |
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

- [ ] **`ModaleVuesViewTest` réactivé et vert** — avec `MultisiteViewTest`, c'est l'acceptation de la
      feature.
- [ ] Dans l'appli, « Vues… » ouvre la modale ; enregistrer puis appliquer une vue met à jour le
      tableau de l'écran principal.

## Definition of Done

- [ ] **Un seul fichier modifié** : `ModaleVuesController.java` (+ retrait du `@Disabled`).
- [ ] Controleur sans logique métier (ArchUnit vert) ; **toute la feature multisite verte** sans
      régression ; `spotless:check` OK.
- [ ] Livré via **branche + PR relue** ; la PR clôt l'épico.

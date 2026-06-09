# 🔧 [diagnostic] 2/3 — Construire la vue Diagnostic.fxml

Deuxième sous-tâche de **M-Diagnostic** : la **mise en page** de l'écran. Aujourd'hui le fichier ne
contient qu'un *placeholder* « à construire » ; vous écrivez la vraie vue.

> 💡 **Un écran complet pour vous inspirer.** L'écran **Passage** vous est fourni en correction (PR « 🧭 Aide : écran Passage ») : c'est un exemple **abouti** de cette même structure MVVM. Ouvrez `passage/view/Passage.fxml` à côté de votre fichier et comparez. Le patron `sites` reste aussi une référence.

## Fichier à modifier (un seul)

`src/main/java/fr/univ_amu/iut/diagnostic/view/Diagnostic.fxml`

## Ce qu'il faut faire

1. Ouvrez la vue de référence **`src/main/java/fr/univ_amu/iut/sites/view/SiteDetail.fxml`** pour voir
   comment on structure un écran (conteneurs `VBox`/`HBox`, `Label`, `styleClass`, `fx:id`).
2. Remplacez le contenu placeholder par la **vraie mise en page** de M-Diagnostic, qui doit déclarer
   les **`fx:id`** que le controleur attend (les noms sont visibles dans `DiagnosticController` et
   dans le test) :
   - `lblEnregistreur` (Label) — l'enregistreur de la nuit ;
   - `lblReleveAbsent` (Label) — alerte « relevé climatique absent » (R20) ;
   - `lblResumeClimat` (Label) — résumé de la série climatique ;
   - `grapheClimat` (**LineChart**) — la courbe T°/hygrométrie ;
   - `listeAnomalies`, `listeEvenements` (**ListView**) — anomalies (R19) et évènements du journal ;
   - `lblGps` (Label) et `lblMessage` (Label).
3. Gardez `fx:controller="fr.univ_amu.iut.diagnostic.view.DiagnosticController"` sur l'élément racine.
4. Conseil : vous pouvez prototyper la mise en page avec **SceneBuilder**, mais le rendu final doit
   être ce fichier FXML versionné.

## Les `fx:id` à déclarer (et leur type)

Votre FXML doit déclarer **exactement ces `fx:id`** (mêmes noms et mêmes types que ce qu'attend le controleur) :

| `fx:id` | Type | Rôle à l'écran |
|---|---|---|
| `lblEnregistreur` | `Label` | enregistreur de la nuit |
| `lblResumeClimat` | `Label` | résumé de la série climatique |
| `lblReleveAbsent` | `Label` | alerte « relevé absent » (R20), affichée/masquée |
| `grapheClimat` | `LineChart` | courbe T°/hygrométrie (reconstruite quand la liste change) |
| `listeAnomalies` | `ListView` | anomalies (R19) |
| `listeEvenements` | `ListView` | évènements du journal |
| `lblGps` | `Label` | disponibilité GPS |
| `lblMessage` | `Label` | message d'erreur |


## Pièges courants

- L'**élément racine** doit porter le bon `fx:controller` : sans lui, les `@FXML` du controleur ne
  seront pas injectés.
- Chaque `fx:id` doit être **exactement** celui du tableau (la casse compte) : un `fx:id` mal
  orthographié = un `@FXML` qui reste `null` au chargement.
- Le **type de balise doit correspondre** à la colonne « Type » et la balise doit être **importée**
  en tête du FXML (`<?import javafx.scene.control.ListView?>`, etc.).
- Pas de logique dans le FXML : seulement la **structure** (la donnée arrive par le câblage du controleur).

## Critères d'acceptation

- [ ] Le FXML se **charge sans erreur** (aucune exception `LoadException` au lancement de l'appli).
- [ ] Tous les `fx:id` ci-dessus sont présents et du **bon type** (un `LineChart` pour `grapheClimat`,
      des `ListView` pour les listes).
- [ ] L'élément racine porte bien `fx:controller` vers `DiagnosticController`.

## Definition of Done

- [ ] **Un seul fichier modifié** : `Diagnostic.fxml`.
- [ ] `./mvnw -Dglass.platform=Headless -Dprism.order=sw test` ne régresse pas (le placeholder
      n'existe plus, mais le câblage controleur arrive à la sous-tâche suivante : si un test reste
      rouge à cause du controleur, c'est normal à ce stade — il deviendra vert en 3/3).
- [ ] Modification livrée via une **branche + PR relue** ; la PR référence cette issue.

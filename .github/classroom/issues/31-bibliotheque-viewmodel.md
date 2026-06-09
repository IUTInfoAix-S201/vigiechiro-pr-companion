# 🔊 [bibliotheque] 1/3 — Implémenter BibliothequeViewModel

Première sous-tâche : le **ViewModel** (liste des sons, sélection courante, export).

> 💡 **Un écran complet pour vous inspirer.** L'écran **Passage** vous est fourni en correction (PR « 🧭 Aide : écran Passage ») : c'est un exemple **abouti** de cette même structure MVVM. Ouvrez `passage/viewmodel/PassageViewModel.java` à côté de votre fichier et comparez. Le patron `sites` reste aussi une référence.

## Fichier à modifier (un seul)

`src/main/java/fr/univ_amu/iut/bibliotheque/viewmodel/BibliothequeViewModel.java`

## Contexte

Propriétés et getters fournis. Vous écrivez le corps de `charger` (chargement initial) et `exporter`
(matérialisation disque), repérés par des `// TODO (M-Bibliotheque) …`. Le constructeur installe un
écouteur de sélection : sa partie à compléter est déjà balisée.

## Démarche (TDD)

1. **Activez** `BibliothequeViewModelTest`, **constatez le rouge**.
2. **`charger`** : interrogez `ServiceBibliotheque`, peuplez la liste des entrées, mettez à jour
   l'indicateur « non vide » et le résumé, réinitialisez la sélection.
3. **`exporter`** : matérialisez la bibliothèque vers le dossier choisi, publiez le bilan ou l'erreur
   dans le message, renvoyez le succès.
4. Patron : `SitesViewModel`. Relancez jusqu'au **vert**.

## Pièges courants

- Le ViewModel **n'importe jamais `javafx.scene`** (ni `Node`, ni contrôle) : uniquement
  `javafx.beans` / `javafx.collections`. Sinon le test **ArchUnit** passe au rouge.
- **Gérez le cas d'erreur** sans laisser remonter d'exception : en cas de souci, remplissez la
  propriété `message` et laissez les autres propriétés dans un état neutre.
- **Repartez d'un état propre** au début de la méthode d'ouverture : les valeurs d'un précédent
  affichage ne doivent pas subsister.

## Critères d'acceptation

- [ ] `BibliothequeViewModelTest` réactivé et **vert**.
- [ ] Une bibliothèque vide produit un résumé adapté (aucun crash, export inactif côté vue ensuite).

## Definition of Done

- [ ] **Un seul fichier modifié** : `BibliothequeViewModel.java` (+ retrait du `@Disabled`).
- [ ] ViewModel sans `javafx.scene` (ArchUnit vert) ; `spotless:check` OK ; pas de régression.
- [ ] Livré via **branche + PR relue**.

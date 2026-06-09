# 🎧 [qualification] 2/4 — Implémenter SelectionEcouteViewModel (liste + écoute)

Deuxième sous-tâche : le **ViewModel de la sélection d'écoute** (bandeau de contexte, liste
échantillonnée, progression d'écoute, régénération).

> 💡 **Un écran complet pour vous inspirer.** L'écran **Passage** vous est fourni en correction (PR « 🧭 Aide : écran Passage ») : c'est un exemple **abouti** de cette même structure MVVM. Ouvrez `passage/viewmodel/PassageViewModel.java` à côté de votre fichier et comparez. Le patron `sites` reste aussi une référence.

## Fichier à modifier (un seul)

`src/main/java/fr/univ_amu/iut/qualification/viewmodel/SelectionEcouteViewModel.java`

## Contexte

Propriétés et getters fournis (le constructeur garde un écouteur à compléter, balisé). Vous écrivez le
corps de `ouvrirSur`, `selectionner`, `marquerCouranteEcoutee` et `regenerer` (repérés par `// TODO …`).

## Démarche (TDD)

1. **Activez** `SelectionEcouteViewModelTest`, **constatez le rouge**.
2. Implémentez en **déléguant à `ServiceQualification`** : `ouvrirSur` charge le contexte (bandeau) et
   la sélection d'écoute (créée à la volée si absente, R12) ; `selectionner` mémorise la séquence
   courante ; `marquerCouranteEcoutee` marque la séquence écoutée (R10) et recalcule la progression ;
   `regenerer` reconstitue la sélection avec la méthode/taille choisies. Erreurs via le message.
3. Patron : `SiteDetailViewModel`. Relancez jusqu'au **vert**.

## Pièges courants

- Le ViewModel **n'importe jamais `javafx.scene`** (ni `Node`, ni contrôle) : uniquement
  `javafx.beans` / `javafx.collections`. Sinon le test **ArchUnit** passe au rouge.
- **Gérez le cas d'erreur** sans laisser remonter d'exception : en cas de souci, remplissez la
  propriété `message` et laissez les autres propriétés dans un état neutre.
- **Repartez d'un état propre** au début de la méthode d'ouverture : les valeurs d'un précédent
  affichage ne doivent pas subsister.

## Critères d'acceptation

- [ ] `SelectionEcouteViewModelTest` réactivé et **vert**.

## Definition of Done

- [ ] **Un seul fichier modifié** : `SelectionEcouteViewModel.java` (+ retrait du `@Disabled`).
- [ ] ViewModel sans `javafx.scene` (ArchUnit vert) ; `spotless:check` OK ; pas de régression.
- [ ] Livré via **branche + PR relue**.

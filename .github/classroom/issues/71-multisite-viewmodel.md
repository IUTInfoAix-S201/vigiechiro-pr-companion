# 🗂 [multisite] 1/5 — Implémenter MultisiteViewModel

Première sous-tâche : le **ViewModel** (tableau agrégé, filtres, tri, export, vues sauvegardées).

> 💡 **Un écran complet pour vous inspirer.** L'écran **Passage** vous est fourni en correction (PR « 🧭 Aide : écran Passage ») : c'est un exemple **abouti** de cette même structure MVVM. Ouvrez `passage/viewmodel/PassageViewModel.java` à côté de votre fichier et comparez. Le patron `sites` reste aussi une référence.

## Fichier à modifier (un seul)

`src/main/java/fr/univ_amu/iut/multisite/viewmodel/MultisiteViewModel.java`

## Contexte

Propriétés (filtres, tri, lignes, vues) et getters fournis ; le constructeur garde des écouteurs à
compléter (balisés). Vous écrivez le corps des méthodes publiques (repérées par `// TODO …`) :
`rafraichir`, `reinitialiserFiltres`, `exporter`, `chargerVues`, `enregistrerVue`, `appliquerVue`,
`mettreAJourVue`, `supprimerVue`. Plusieurs renvoient un booléen (amorce fournie).

## Démarche (TDD)

1. **Activez** `MultisiteViewModelTest`, **constatez le rouge** — il couvre filtres, tri, export et
   les opérations sur les vues sauvegardées.
2. Implémentez en **déléguant à `ServiceMultisite`** : `rafraichir` ré-interroge le service avec les
   filtres + le tri courants et met à jour le tableau + le résumé ; les opérations « vues » listent /
   enregistrent / appliquent / mettent à jour / suppriment, puis rechargent. Erreurs via le message.
3. Patron : `SitesViewModel`. Relancez jusqu'au **vert**.

## Pièges courants

- Le ViewModel **n'importe jamais `javafx.scene`** (ni `Node`, ni contrôle) : uniquement
  `javafx.beans` / `javafx.collections`. Sinon le test **ArchUnit** passe au rouge.
- **Gérez le cas d'erreur** sans laisser remonter d'exception : en cas de souci, remplissez la
  propriété `message` et laissez les autres propriétés dans un état neutre.
- **Repartez d'un état propre** au début de la méthode d'ouverture : les valeurs d'un précédent
  affichage ne doivent pas subsister.

## Critères d'acceptation

- [ ] `MultisiteViewModelTest` réactivé et **vert**.

## Definition of Done

- [ ] **Un seul fichier modifié** : `MultisiteViewModel.java` (+ retrait du `@Disabled`).
- [ ] ViewModel sans `javafx.scene` (ArchUnit vert) ; `spotless:check` OK ; pas de régression.
- [ ] Livré via **branche + PR relue**.

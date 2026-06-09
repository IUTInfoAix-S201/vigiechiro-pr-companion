# 🎧 [qualification] 1/4 — Implémenter QualificationViewModel (verdict)

Première sous-tâche : le **ViewModel du verdict** (pré-check + choix et enregistrement du verdict).

> 💡 **Un écran complet pour vous inspirer.** L'écran **Passage** vous est fourni en correction (PR « 🧭 Aide : écran Passage ») : c'est un exemple **abouti** de cette même structure MVVM. Ouvrez `passage/viewmodel/PassageViewModel.java` à côté de votre fichier et comparez. Le patron `sites` reste aussi une référence.

## Fichier à modifier (un seul)

`src/main/java/fr/univ_amu/iut/qualification/viewmodel/QualificationViewModel.java`

## Contexte

Propriétés, binding « peut enregistrer » et getters fournis (le constructeur est complet : n'y touchez
pas). Vous écrivez le corps de `ouvrirSur`, `choisirVerdict` et `enregistrer` (repérés par `// TODO …`).

## Démarche (TDD)

1. **Activez** `QualificationViewModelTest`, **constatez le rouge**.
2. Implémentez en **déléguant à `ServiceQualification`** : `ouvrirSur` exécute le pré-check (3 feux) et
   amorce le bandeau (statut + verdict actuels) ; `choisirVerdict` mémorise le choix ; `enregistrer`
   refuse sans verdict décisif, sinon persiste le verdict (statut → Vérifié), gère l'avertissement
   « à jeter » (R14) et l'état « enregistré ». Toute erreur passe par le message.
3. Patron : `SiteDetailViewModel`. Relancez jusqu'au **vert**.

## Pièges courants

- Le ViewModel **n'importe jamais `javafx.scene`** (ni `Node`, ni contrôle) : uniquement
  `javafx.beans` / `javafx.collections`. Sinon le test **ArchUnit** passe au rouge.
- **Gérez le cas d'erreur** sans laisser remonter d'exception : en cas de souci, remplissez la
  propriété `message` et laissez les autres propriétés dans un état neutre.
- **Repartez d'un état propre** au début de la méthode d'ouverture : les valeurs d'un précédent
  affichage ne doivent pas subsister.

## Critères d'acceptation

- [ ] `QualificationViewModelTest` réactivé et **vert**.

## Definition of Done

- [ ] **Un seul fichier modifié** : `QualificationViewModel.java` (+ retrait du `@Disabled`).
- [ ] ViewModel sans `javafx.scene` (ArchUnit vert) ; `spotless:check` OK ; pas de régression.
- [ ] Livré via **branche + PR relue**.

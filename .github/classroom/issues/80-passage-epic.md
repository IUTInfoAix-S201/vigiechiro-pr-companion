# 🗂️ [passage] Construire l'écran pivot M-Passage (+ modale rattachement)

**Issue chapeau**. Détail dans les 6 issues « fichier » qui suivent. C'est l'**écran central** : il
relie toutes les autres features. À faire **en dernier** — quand il est fini, le fil rouge complet
(import → vérification → dépôt → validation) passe au vert.

## L'écran à construire

**M-Passage** est la **fiche pivot d'un passage** : identité, **stepper** de statut workflow,
statistiques (volumes, durée, séquences), et des boutons qui ouvrent les autres écrans (Vérifier →
M-Qualification, Diagnostic → M-Diagnostic, Préparer le dépôt → M-Lot, Validation → M-Vision-Tadarida).
Une **modale « Modifier le rattachement »** corrige l'année / le n° de passage. On l'atteint en
double-cliquant un passage (depuis M-Sites ou M-Multisite).

## 👀 Voir à quoi l'écran doit ressembler

Pour **visualiser** l'IHM et suivre votre progression, générez les aperçus :

```bash
.github/assets/capture-screenshots.sh
```

Ce script rend tous les écrans en **PNG**, sans ouvrir de fenêtre (rendu hors-écran *Headless*), dans **`.github/assets/`**. Pour cette feature, ouvrez :

- `.github/assets/apercu-passage.png` — pivot, statut Vérifié : préparer le dépôt, validation verrouillée
- `.github/assets/apercu-passage-depose.png` — pivot, statut Déposé : dépôt fait, validation déverrouillée
- `.github/assets/apercu-passage-rattachement.png` — modale « Modifier le rattachement » : année + n° de passage

> Au départ ces images montrent le **placeholder** « à construire » ; elles se mettent à jour au fil de votre travail. La galerie est aussi **régénérée à chaque push sur `main`** (workflow `capture-vues.yml`) et consultable directement sur GitHub.

## Architecture

```
PassageController ─┬─lie──> PassageViewModel ──> ServicePassage (fourni)
  (Passage.fxml)   ├─ouvre─> les autres écrans via les contrats socle Ouvrir* (déjà fournis)
                   └─ouvre─> RattachementModaleController (RattachementModale.fxml) ──> RattachementViewModel
```

## Sous-tâches (dans l'ordre)

- [ ] **1/6** — Implémenter `PassageViewModel`.
- [ ] **2/6** — Construire `Passage.fxml`.
- [ ] **3/6** — Câbler `PassageController` (dont les boutons vers les autres écrans).
- [ ] **4/6** — Implémenter `RattachementViewModel`.
- [ ] **5/6** — Construire `RattachementModale.fxml`.
- [ ] **6/6** — Câbler `RattachementModaleController`.

## Tests d'acceptation de la feature

`PassageViewTest` (écran) **et** `RattachementModaleViewTest` (modale), livrés `@Disabled`. Feature
**terminée quand les deux sont verts**. Bonus : une fois passage fini, les **parcours E2E** redeviennent
réalisables (voir la passe finale dédiée).

## Critères d'acceptation (feature)

- [ ] Sous-tâches mergées ; `PassageViewModelTest`, `RattachementViewModelTest`, `PassageViewTest`,
      `RattachementModaleViewTest` verts.
- [ ] Dans l'appli, double-cliquer un passage ouvre la fiche ; les boutons ouvrent les bons écrans ;
      la modale de rattachement fonctionne.

## Definition of Done (feature)

- [ ] Suite verte sans régression ; ArchUnit, `spotless:check`, `-Pquality-gate verify` verts.
- [ ] Chaque sous-tâche passée par une **PR relue**.

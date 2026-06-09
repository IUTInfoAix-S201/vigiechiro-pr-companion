# 📥 [importation] Construire l'écran M-Import (assistant « Importer une nuit »)

**Issue chapeau**. Détail dans les 3 issues « fichier » qui suivent. C'est l'écran le plus riche : la
logique d'import tourne **en arrière-plan** pour ne pas figer l'IHM.

## L'écran à construire

**M-Import** (parcours P2) est un assistant en 4 sections : (1) choisir un dossier source ; (2)
l'**inspecter** en lecture seule ; (3) **rattacher** la nuit (site / point / année / n° de passage) et
prévisualiser le préfixe ; (4) **lancer l'import** avec une barre de progression. On l'atteint depuis
la carte « Importer une nuit » de l'accueil.

## 👀 Voir à quoi l'écran doit ressembler

Pour **visualiser** l'IHM et suivre votre progression, générez les aperçus :

```bash
.github/assets/capture-screenshots.sh
```

Ce script rend tous les écrans en **PNG**, sans ouvrir de fenêtre (rendu hors-écran *Headless*), dans **`.github/assets/`**. Pour cette feature, ouvrez :

- `.github/assets/apercu-import-assistant.png` — assistant « Importer une nuit », cas standard
- `.github/assets/apercu-import-en-cours.png` — import en cours : barre de progression, formulaire gelé
- `.github/assets/apercu-import-melange.png` — cas « mélange » : 2 enregistreurs détectés, avertissement
- `.github/assets/apercu-import-incoherence.png` — cas « incohérence » : journal/relevé en désaccord avec les WAV

> Au départ ces images montrent le **placeholder** « à construire » ; elles se mettent à jour au fil de votre travail. La galerie est aussi **régénérée à chaque push sur `main`** (workflow `capture-vues.yml`) et consultable directement sur GitHub.

## Architecture

```
ImportationController  ──lie──>  ImportationViewModel  ──délègue──>  ServiceImport (fourni)
  (Importation.fxml)            (propriétés + inspection + import)
```

## Sous-tâches (dans l'ordre)

- [ ] **1/3** — Implémenter `ImportationViewModel`.
- [ ] **2/3** — Construire `Importation.fxml`.
- [ ] **3/3** — Câbler `ImportationController` (dont l'import en arrière-plan).

## Test d'acceptation de la feature

`ImportationViewTest`, livré `@Disabled`. Feature **terminée quand il est vert**.

## Critères d'acceptation (feature)

- [ ] Sous-tâches mergées ; `ImportationViewModelTest` et `ImportationViewTest` verts.
- [ ] Dans l'appli, la carte « Importer une nuit » ouvre l'assistant ; choisir un dossier l'inspecte ;
      le rattachement complété active l'import ; l'import affiche une progression puis un résumé.

## Definition of Done (feature)

- [ ] Suite verte sans régression ; ArchUnit, `spotless:check`, `-Pquality-gate verify` verts.
- [ ] Chaque sous-tâche passée par une **PR relue**.

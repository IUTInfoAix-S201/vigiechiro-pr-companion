# ✅ [validation] Construire l'écran M-Vision-Tadarida (validation des observations)

**Issue chapeau**. Détail dans les 3 issues « fichier » qui suivent.

## L'écran à construire

**M-Vision-Tadarida** (parcours P7) sert à **valider taxonomiquement** les résultats Tadarida d'un
passage : importer un CSV de résultats, parcourir les observations, **valider** (R15) ou **corriger**
(R16) chacune, écouter le son associé (composant audio fourni), filtrer par statut, suivre la
progression, puis **exporter** un CSV `_Vu` réinjectable (R17). On l'atteint depuis l'onglet
« Validation Tadarida » de M-Passage (déverrouillé une fois le passage déposé).

## 👀 Voir à quoi l'écran doit ressembler

Pour **visualiser** l'IHM et suivre votre progression, générez les aperçus :

```bash
.github/assets/capture-screenshots.sh
```

Ce script rend tous les écrans en **PNG**, sans ouvrir de fenêtre (rendu hors-écran *Headless*), dans **`.github/assets/`**. Pour cette feature, ouvrez :

- `.github/assets/apercu-validation-import.png` — état d'entrée : avant import, bouton import
- `.github/assets/apercu-validation-revue.png` — revue : statuts variés + détail sélectionné

> Au départ ces images montrent le **placeholder** « à construire » ; elles se mettent à jour au fil de votre travail. La galerie est aussi **régénérée à chaque push sur `main`** (workflow `capture-vues.yml`) et consultable directement sur GitHub.

## Architecture

```
ValidationController  ──lie──>  ValidationViewModel  ──délègue──>  ServiceValidation (fourni)
  (Validation.fxml)             (liste filtrée + 4 actions)
```

## Sous-tâches (dans l'ordre)

- [ ] **1/3** — Implémenter `ValidationViewModel` (`ouvrirSur`, `valider`, `corriger`, `importer`, `exporter`).
- [ ] **2/3** — Construire `Validation.fxml`.
- [ ] **3/3** — Câbler `ValidationController`.

## Test d'acceptation de la feature

`ValidationViewTest`, livré `@Disabled`. Feature **terminée quand il est vert**.

## Critères d'acceptation (feature)

- [ ] Sous-tâches mergées ; `ValidationViewModelTest` et `ValidationViewTest` verts.
- [ ] Dans l'appli, l'onglet « Validation Tadarida » d'un passage déposé affiche la table, valide /
      corrige, et exporte le `_Vu`.

## Definition of Done (feature)

- [ ] Suite verte sans régression ; ArchUnit, `spotless:check`, `-Pquality-gate verify` verts.
- [ ] Chaque sous-tâche passée par une **PR relue**.

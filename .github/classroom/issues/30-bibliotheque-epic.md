# 🔊 [bibliotheque] Construire l'écran M-Bibliotheque (sons de référence)

**Issue chapeau**. Détail dans les 3 issues « fichier » qui suivent.

## L'écran à construire

**M-Bibliotheque** (statut COULD, parcours P10) liste les **sons de référence** marqués pendant la
validation, permet de les **écouter** (composant audio fourni) et d'**exporter** la bibliothèque (CSV +
copie des fichiers son). On l'atteint depuis la carte « Bibliothèque de sons » de l'accueil.

## 👀 Voir à quoi l'écran doit ressembler

Pour **visualiser** l'IHM et suivre votre progression, générez les aperçus :

```bash
.github/assets/capture-screenshots.sh
```

Ce script rend tous les écrans en **PNG**, sans ouvrir de fenêtre (rendu hors-écran *Headless*), dans **`.github/assets/`**. Pour cette feature, ouvrez :

- `.github/assets/apercu-bibliotheque-vide.png` — état vide : aucun son de référence, export inactif
- `.github/assets/apercu-bibliotheque-sons.png` — peuplée : sons de référence, détail + écoute, export actif

> Au départ ces images montrent le **placeholder** « à construire » ; elles se mettent à jour au fil de votre travail. La galerie est aussi **régénérée à chaque push sur `main`** (workflow `capture-vues.yml`) et consultable directement sur GitHub.

## Architecture

```
BibliothequeController  ──lie──>  BibliothequeViewModel  ──lit──>  ServiceBibliotheque (fourni)
   (Bibliotheque.fxml)
```

## Sous-tâches (dans l'ordre)

- [ ] **1/3** — Implémenter `BibliothequeViewModel` (`charger`, `exporter`).
- [ ] **2/3** — Construire `Bibliotheque.fxml`.
- [ ] **3/3** — Câbler `BibliothequeController`.

## Test d'acceptation de la feature

`BibliothequeViewTest`, livré `@Disabled`. Feature **terminée quand il est vert**.

## Critères d'acceptation (feature)

- [ ] Les 3 sous-tâches sont mergées ; `BibliothequeViewModelTest` et `BibliothequeViewTest` verts.
- [ ] Dans l'appli, la carte « Bibliothèque de sons » ouvre le vrai écran (table + détail + écoute).

## Definition of Done (feature)

- [ ] Suite verte sans régression ; ArchUnit vert ; `spotless:check` + `-Pquality-gate verify` verts.
- [ ] Chaque sous-tâche passée par une **PR relue**.

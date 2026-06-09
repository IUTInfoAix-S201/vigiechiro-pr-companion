# 🔧 [diagnostic] Construire l'écran M-Diagnostic (diagnostic matériel)

**Issue chapeau** : vue d'ensemble de la feature. Le travail détaillé est dans les 3 issues
« fichier » qui suivent (à faire **dans l'ordre**).

## L'écran à construire

**M-Diagnostic** affiche l'**état matériel d'une nuit** (parcours P6). On l'atteint depuis l'onglet
« Diagnostic » de l'écran M-Passage. Il montre :
- l'**enregistreur** (`PR <n° de série>`) ;
- la **courbe climatique** (température / hygrométrie de la nuit) ou un message si le relevé est
  absent (R20) ;
- les **anomalies** et les **évènements** du journal du capteur (R19) ;
- une note sur la disponibilité du **GPS** du point.

C'est un écran **en lecture seule** (aucune action) : un bon premier exercice MVVM.

## 👀 Voir à quoi l'écran doit ressembler

Pour **visualiser** l'IHM et suivre votre progression, générez les aperçus :

```bash
.github/assets/capture-screenshots.sh
```

Ce script rend tous les écrans en **PNG**, sans ouvrir de fenêtre (rendu hors-écran *Headless*), dans **`.github/assets/`**. Pour cette feature, ouvrez :

- `.github/assets/apercu-diagnostic.png` — relevé présent : courbe climat + anomalies + GPS
- `.github/assets/apercu-diagnostic-sans-releve.png` — relevé absent : absence signalée, anomalies seules

> Au départ ces images montrent le **placeholder** « à construire » ; elles se mettent à jour au fil de votre travail. La galerie est aussi **régénérée à chaque push sur `main`** (workflow `capture-vues.yml`) et consultable directement sur GitHub.

## Architecture (rappel)

```
DiagnosticController  ──lie──>  DiagnosticViewModel  ──lit──>  ServiceDiagnostic (fourni)
   (Diagnostic.fxml)              (propriétés observables)         (déjà testé)
```

## Sous-tâches (dans l'ordre)

- [ ] **#VM** — Implémenter `DiagnosticViewModel.ouvrirSur(...)` (le corps de la logique).
- [ ] **#FXML** — Construire `Diagnostic.fxml` (la vraie mise en page, avec les `fx:id`).
- [ ] **#Controleur** — Câbler `DiagnosticController` (relier les `@FXML` au ViewModel).

## Test d'acceptation de la feature

Le test d'intégration **`DiagnosticViewTest`** (dans `src/test/java/.../diagnostic/view/`) charge
l'écran, l'ouvre sur un passage et vérifie le câblage (graphe à 2 séries, listes peuplées,
enregistreur). Il est livré **`@Disabled`**. La feature est **terminée quand ce test est vert**
(retirez son `@Disabled`).

## Critères d'acceptation (feature)

- [ ] Les 3 sous-tâches sont terminées et mergées.
- [ ] `DiagnosticViewModelTest` **et** `DiagnosticViewTest` sont **réactivés et verts**.
- [ ] Dans l'appli (`./mvnw javafx:run`), ouvrir un passage puis l'onglet « Diagnostic matériel »
      affiche bien le graphe + les listes (et non plus le placeholder « à construire »).

## Definition of Done (feature)

- [ ] Toute la suite de tests passe sans régression.
- [ ] Aucune logique métier dans la vue/le controleur ; le ViewModel n'importe pas `javafx.scene`
      (tests **ArchUnit** verts).
- [ ] `./mvnw spotless:check` et `./mvnw -Pquality-gate verify` (PMD) sont verts.
- [ ] Chaque sous-tâche est passée par une **PR relue**.

# M-Site-detail - Détail d'un site de suivi

> **Type** : vue de détail (atteinte par clic sur une card de [M-Sites](M-Sites.md)).
> **Persona principal** : [Marie](../Personas/Marie.md), partagée avec [Karim](../Personas/Karim.md). [Samuel](../Personas/Samuel.md) accède à cette vue ponctuellement, sa vue de prédilection est [M-MultiSite](M-MultiSite.md).
> **Parcours couverts** : [P1 - Déclarer un site de suivi](../Parcours%20utilisateurs/P1%20-%20Déclarer%20un%20site%20de%20suivi.md).
> **Stories couvertes** : [E1.S2 - Ajouter / modifier / retirer des points](../Story%20mapping/E1%20-%20Gérer%20ses%20sites%20et%20points%20de%20suivi.md#e1s2), [E1.S3 - Saisir GPS et descriptif](../Story%20mapping/E1%20-%20Gérer%20ses%20sites%20et%20points%20de%20suivi.md#e1s3).

Cette vue présente **un site et tout ce qui s'y rattache** : sa fiche d'identité (n° de carré, nom, protocole, dates), ses points d'écoute (avec coordonnées GPS optionnelles), et l'historique des passages enregistrés sur ce site. C'est aussi depuis cet écran qu'on **modifie le site** (ajout/retrait de points, mise à jour des coordonnées GPS).

## Wireframe principal - site avec 3 points et plusieurs passages

<div markdown="0">
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1200 900" role="img" aria-label="Maquette M-Site-detail - Détail d'un site de suivi" style="max-width: 100%; height: auto; border: 1px solid #d0d7de; border-radius: 6px; background: #fafbfc;">
  <style>
    .frame { fill: #ffffff; stroke: #2c3e50; stroke-width: 1.5; }
    .titlebar { fill: #2c3e50; }
    .titletxt { fill: #ffffff; font: 600 14px sans-serif; }
    .topnav { fill: #34495e; }
    .navtxt-active { fill: #ffffff; font: 600 13px sans-serif; }
    .navtxt-inactive { fill: #bdc3c7; font: 400 13px sans-serif; }
    .breadcrumb { font: 13px sans-serif; fill: #4a90d9; }
    .breadcrumb-sep { font: 13px sans-serif; fill: #6a737d; }
    .breadcrumb-curr { font: 13px sans-serif; fill: #2c3e50; }
    .pagetitle { font: 700 24px sans-serif; fill: #2c3e50; }
    .pagesub { font: 14px sans-serif; fill: #6a737d; }
    .btn-primary { fill: #4a90d9; stroke: #2563a3; stroke-width: 1; }
    .btn-secondary { fill: #ffffff; stroke: #2c3e50; stroke-width: 1; }
    .btn-danger { fill: #ffffff; stroke: #a93226; stroke-width: 1; }
    .btn-txt { fill: #ffffff; font: 600 13px sans-serif; }
    .btn-txt-dark { fill: #2c3e50; font: 600 13px sans-serif; }
    .btn-txt-danger { fill: #a93226; font: 600 13px sans-serif; }
    .info-bar { fill: #f6f8fa; stroke: #d0d7de; stroke-width: 1; }
    .info-label { font: 11px sans-serif; fill: #6a737d; }
    .info-value { font: 600 14px sans-serif; fill: #2c3e50; }
    .section-title { font: 700 16px sans-serif; fill: #2c3e50; }
    .section-sub { font: 12px sans-serif; fill: #6a737d; }
    .point-card { fill: #ffffff; stroke: #d0d7de; stroke-width: 1; }
    .point-code { font: 700 18px sans-serif; fill: #2c3e50; }
    .point-desc { font: 13px sans-serif; fill: #6a737d; }
    .point-gps { font: 12px monospace; fill: #6a737d; }
    .gps-ok { fill: #1e8449; font: 600 12px sans-serif; }
    .gps-missing { fill: #b9770e; font: 600 12px sans-serif; }
    .ico-action { fill: #6a737d; font: 14px sans-serif; }
    .table-head { fill: #eef2f5; stroke: #2c3e50; stroke-width: 1; }
    .table-row { fill: #ffffff; stroke: #d0d7de; stroke-width: 0.5; }
    .table-row-alt { fill: #f6f8fa; stroke: #d0d7de; stroke-width: 0.5; }
    .col-head { font: 600 11px sans-serif; fill: #2c3e50; }
    .cell { font: 12px sans-serif; fill: #2c3e50; }
    .cell-sec { font: 12px sans-serif; fill: #6a737d; }
    .badge-pill { fill: #ffffff; stroke-width: 1; }
    .badge-imp { fill: #f6f8fa; stroke: #6a737d; stroke-width: 1; }
    .badge-trans { fill: #fef9e7; stroke: #b9770e; stroke-width: 1; }
    .badge-veri { fill: #fde7e7; stroke: #a93226; stroke-width: 1; }
    .badge-ok { fill: #d4edda; stroke: #1e8449; stroke-width: 1; }
    .badge-dep { fill: #cce4f7; stroke: #2563a3; stroke-width: 1; }
    .badge-txt { font: 600 11px sans-serif; }
    .footer { fill: #f6f8fa; stroke: #d0d7de; stroke-width: 0.5; }
    .footer-txt { font: 11px sans-serif; fill: #6a737d; }
  </style>

  <!-- Cadre + title bar + top nav (identique à M-Sites) -->
  <rect x="10" y="10" width="1180" height="880" rx="4" class="frame"/>
  <rect x="10" y="10" width="1180" height="32" rx="4" class="titlebar"/>
  <rect x="10" y="26" width="1180" height="16" class="titlebar"/>
  <text x="28" y="31" class="titletxt">🦇 VigieChiro PR Companion</text>
  <text x="1140" y="31" class="titletxt" text-anchor="end">— ☐ ☓</text>

  <rect x="10" y="42" width="1180" height="40" class="topnav"/>
  <text x="40" y="67" class="navtxt-active">🏠 Mes sites</text>
  <text x="170" y="67" class="navtxt-inactive">📥 Importer une nuit</text>
  <text x="330" y="67" class="navtxt-inactive">📊 Vue tabulaire</text>
  <text x="470" y="67" class="navtxt-inactive">⚙ Paramètres</text>
  <text x="1140" y="67" class="navtxt-inactive" text-anchor="end">👤 Local</text>

  <!-- Breadcrumb -->
  <text x="40" y="108" class="breadcrumb">‹ Mes sites</text>
  <text x="125" y="108" class="breadcrumb-sep">›</text>
  <text x="140" y="108" class="breadcrumb-curr">Carré 640380</text>

  <!-- Page header -->
  <text x="40" y="148" class="pagetitle">Carré 640380 — Étang de la Tuilière</text>
  <text x="40" y="172" class="pagesub">📍 Aix-en-Provence (13) · Protocole Vigie-Chiro Point Fixe</text>

  <!-- Boutons header (modifier / supprimer) -->
  <rect x="930" y="125" width="120" height="36" rx="4" class="btn-secondary"/>
  <text x="990" y="148" class="btn-txt-dark" text-anchor="middle">✏ Modifier</text>
  <rect x="1060" y="125" width="100" height="36" rx="4" class="btn-danger"/>
  <text x="1110" y="148" class="btn-txt-danger" text-anchor="middle">🗑 Supprimer</text>

  <!-- Bandeau d'infos clés -->
  <rect x="40" y="195" width="1120" height="60" rx="4" class="info-bar"/>
  <text x="60" y="216" class="info-label">N° DE CARRÉ</text>
  <text x="60" y="240" class="info-value">640380</text>
  <text x="200" y="216" class="info-label">DÉPARTEMENT</text>
  <text x="200" y="240" class="info-value">64 - Pyrénées-Atlantiques</text>
  <text x="450" y="216" class="info-label">PROTOCOLE</text>
  <text x="450" y="240" class="info-value">Point Fixe</text>
  <text x="610" y="216" class="info-label">CRÉÉ LE</text>
  <text x="610" y="240" class="info-value">2026-04-12</text>
  <text x="760" y="216" class="info-label">DERNIÈRE NUIT IMPORTÉE</text>
  <text x="760" y="240" class="info-value">2026-06-22 (il y a 2 j)</text>
  <text x="990" y="216" class="info-label">PASSAGES 2026</text>
  <text x="990" y="240" class="info-value">7 (dont 1 à vérifier ⚠)</text>

  <!-- Section Points d'écoute -->
  <text x="40" y="295" class="section-title">📍 Points d'écoute (3)</text>
  <text x="40" y="312" class="section-sub">Codes au format lettre + chiffre (R2). GPS optionnels mais débloquent la vérification astronomique (E6.S3).</text>
  <rect x="990" y="280" width="170" height="32" rx="4" class="btn-primary"/>
  <text x="1075" y="301" class="btn-txt" text-anchor="middle">+ Ajouter un point</text>

  <!-- Card point A1 -->
  <rect x="40" y="330" width="365" height="100" rx="6" class="point-card"/>
  <text x="60" y="358" class="point-code">A1</text>
  <text x="60" y="378" class="point-desc">Près du chêne, à 30 m du chemin</text>
  <text x="60" y="398" class="point-gps">📍 43.5298, 5.4474</text>
  <text x="200" y="398" class="gps-ok">✓ GPS</text>
  <text x="60" y="418" class="point-desc">3 passages cette saison</text>
  <text x="380" y="358" class="ico-action" text-anchor="end">✏</text>
  <text x="380" y="380" class="ico-action" text-anchor="end">🗑</text>

  <!-- Card point B2 -->
  <rect x="417" y="330" width="365" height="100" rx="6" class="point-card"/>
  <text x="437" y="358" class="point-code">B2</text>
  <text x="437" y="378" class="point-desc">Bord de l'étang, plage sud</text>
  <text x="437" y="398" class="point-gps">📍 43.5301, 5.4480</text>
  <text x="577" y="398" class="gps-ok">✓ GPS</text>
  <text x="437" y="418" class="point-desc">2 passages cette saison</text>
  <text x="757" y="358" class="ico-action" text-anchor="end">✏</text>
  <text x="757" y="380" class="ico-action" text-anchor="end">🗑</text>

  <!-- Card point C3 (sans GPS) -->
  <rect x="794" y="330" width="365" height="100" rx="6" class="point-card"/>
  <text x="814" y="358" class="point-code">C3</text>
  <text x="814" y="378" class="point-desc">(pas de description)</text>
  <text x="814" y="398" class="gps-missing">⚠ GPS manquant</text>
  <text x="814" y="418" class="point-desc">2 passages cette saison</text>
  <text x="1134" y="358" class="ico-action" text-anchor="end">✏</text>
  <text x="1134" y="380" class="ico-action" text-anchor="end">🗑</text>

  <!-- Section Passages enregistrés -->
  <text x="40" y="475" class="section-title">📅 Passages enregistrés sur ce site (7)</text>
  <text x="40" y="492" class="section-sub">Cliquez sur une ligne pour ouvrir le passage.</text>

  <!-- Filtre rapide -->
  <rect x="780" y="466" width="200" height="30" rx="4" class="btn-secondary"/>
  <text x="800" y="486" class="btn-txt-dark" font-size="12">🔽 Filtrer : tous statuts</text>
  <rect x="990" y="466" width="170" height="30" rx="4" class="btn-primary"/>
  <text x="1075" y="486" class="btn-txt" font-size="12" text-anchor="middle">📥 Importer une nuit</text>

  <!-- Tableau passages -->
  <rect x="40" y="510" width="1120" height="34" class="table-head"/>
  <text x="60" y="532" class="col-head">DATE</text>
  <text x="170" y="532" class="col-head">POINT</text>
  <text x="240" y="532" class="col-head">N° PASSAGE</text>
  <text x="370" y="532" class="col-head">STATUT</text>
  <text x="540" y="532" class="col-head">VERDICT</text>
  <text x="680" y="532" class="col-head">ENREGISTREUR</text>
  <text x="850" y="532" class="col-head">SÉQUENCES</text>
  <text x="970" y="532" class="col-head">DÉPOSÉ LE</text>
  <text x="1100" y="532" class="col-head">ACTIONS</text>

  <!-- Ligne 1 (la plus récente) -->
  <rect x="40" y="544" width="1120" height="32" class="table-row"/>
  <text x="60" y="565" class="cell">2026-06-22</text>
  <text x="170" y="565" class="cell">A1</text>
  <text x="240" y="565" class="cell">2</text>
  <rect x="365" y="551" width="105" height="20" rx="10" class="badge-trans"/>
  <text x="418" y="565" class="badge-txt" fill="#7e5109" text-anchor="middle">Transformé</text>
  <text x="540" y="565" class="cell-sec">— à vérifier</text>
  <text x="680" y="565" class="cell">PR 1925492</text>
  <text x="850" y="565" class="cell">3 614</text>
  <text x="970" y="565" class="cell-sec">—</text>
  <text x="1100" y="565" class="ico-action">🎧 Vérifier</text>

  <!-- Ligne 2 -->
  <rect x="40" y="576" width="1120" height="32" class="table-row-alt"/>
  <text x="60" y="597" class="cell">2026-06-15</text>
  <text x="170" y="597" class="cell">B2</text>
  <text x="240" y="597" class="cell">2</text>
  <rect x="365" y="583" width="105" height="20" rx="10" class="badge-dep"/>
  <text x="418" y="597" class="badge-txt" fill="#2563a3" text-anchor="middle">Déposé</text>
  <rect x="535" y="583" width="50" height="20" rx="10" class="badge-ok"/>
  <text x="560" y="597" class="badge-txt" fill="#1e6f3f" text-anchor="middle">OK</text>
  <text x="680" y="597" class="cell">PR 1925492</text>
  <text x="850" y="597" class="cell">2 870</text>
  <text x="970" y="597" class="cell">2026-06-16</text>
  <text x="1100" y="597" class="ico-action">📂 Ouvrir</text>

  <!-- Ligne 3 -->
  <rect x="40" y="608" width="1120" height="32" class="table-row"/>
  <text x="60" y="629" class="cell">2026-06-15</text>
  <text x="170" y="629" class="cell">C3</text>
  <text x="240" y="629" class="cell">2</text>
  <rect x="365" y="615" width="105" height="20" rx="10" class="badge-dep"/>
  <text x="418" y="629" class="badge-txt" fill="#2563a3" text-anchor="middle">Déposé</text>
  <rect x="535" y="615" width="80" height="20" rx="10" class="badge-trans"/>
  <text x="575" y="629" class="badge-txt" fill="#7e5109" text-anchor="middle">Douteux</text>
  <text x="680" y="629" class="cell">PR 1925487</text>
  <text x="850" y="629" class="cell">1 942</text>
  <text x="970" y="629" class="cell">2026-06-16</text>
  <text x="1100" y="629" class="ico-action">📂 Ouvrir</text>

  <!-- Ligne 4 -->
  <rect x="40" y="640" width="1120" height="32" class="table-row-alt"/>
  <text x="60" y="661" class="cell">2026-04-22</text>
  <text x="170" y="661" class="cell">A1</text>
  <text x="240" y="661" class="cell">1</text>
  <rect x="365" y="647" width="105" height="20" rx="10" class="badge-dep"/>
  <text x="418" y="661" class="badge-txt" fill="#2563a3" text-anchor="middle">Déposé</text>
  <rect x="535" y="647" width="50" height="20" rx="10" class="badge-ok"/>
  <text x="560" y="661" class="badge-txt" fill="#1e6f3f" text-anchor="middle">OK</text>
  <text x="680" y="661" class="cell">PR 1925492</text>
  <text x="850" y="661" class="cell">2 114</text>
  <text x="970" y="661" class="cell">2026-04-24</text>
  <text x="1100" y="661" class="ico-action">📂 Ouvrir</text>

  <!-- Ligne 5 -->
  <rect x="40" y="672" width="1120" height="32" class="table-row"/>
  <text x="60" y="693" class="cell">2026-04-22</text>
  <text x="170" y="693" class="cell">B2</text>
  <text x="240" y="693" class="cell">1</text>
  <rect x="365" y="679" width="105" height="20" rx="10" class="badge-dep"/>
  <text x="418" y="693" class="badge-txt" fill="#2563a3" text-anchor="middle">Déposé</text>
  <rect x="535" y="679" width="50" height="20" rx="10" class="badge-ok"/>
  <text x="560" y="693" class="badge-txt" fill="#1e6f3f" text-anchor="middle">OK</text>
  <text x="680" y="693" class="cell">PR 1925492</text>
  <text x="850" y="693" class="cell">2 558</text>
  <text x="970" y="693" class="cell">2026-04-24</text>
  <text x="1100" y="693" class="ico-action">📂 Ouvrir</text>

  <!-- Ligne 6 -->
  <rect x="40" y="704" width="1120" height="32" class="table-row-alt"/>
  <text x="60" y="725" class="cell">2026-04-22</text>
  <text x="170" y="725" class="cell">C3</text>
  <text x="240" y="725" class="cell">1</text>
  <rect x="365" y="711" width="105" height="20" rx="10" class="badge-dep"/>
  <text x="418" y="725" class="badge-txt" fill="#2563a3" text-anchor="middle">Déposé</text>
  <rect x="535" y="711" width="50" height="20" rx="10" class="badge-ok"/>
  <text x="560" y="725" class="badge-txt" fill="#1e6f3f" text-anchor="middle">OK</text>
  <text x="680" y="725" class="cell">PR 1925487</text>
  <text x="850" y="725" class="cell">1 783</text>
  <text x="970" y="725" class="cell">2026-04-24</text>
  <text x="1100" y="725" class="ico-action">📂 Ouvrir</text>

  <!-- Ligne 7 (test à jeter) -->
  <rect x="40" y="736" width="1120" height="32" class="table-row"/>
  <text x="60" y="757" class="cell">2026-04-08</text>
  <text x="170" y="757" class="cell">A1</text>
  <text x="240" y="757" class="cell">1</text>
  <rect x="365" y="743" width="105" height="20" rx="10" class="badge-dep"/>
  <text x="418" y="757" class="badge-txt" fill="#2563a3" text-anchor="middle">Déposé</text>
  <rect x="535" y="743" width="80" height="20" rx="10" class="badge-veri"/>
  <text x="575" y="757" class="badge-txt" fill="#a93226" text-anchor="middle">À jeter</text>
  <text x="680" y="757" class="cell">PR 1925492</text>
  <text x="850" y="757" class="cell">421</text>
  <text x="970" y="757" class="cell">—</text>
  <text x="1100" y="757" class="ico-action">📂 Ouvrir</text>

  <!-- Footer -->
  <rect x="10" y="860" width="1180" height="30" class="footer"/>
  <text x="40" y="880" class="footer-txt">💾 Base locale : ~/VigieChiroCompanion/companion.db</text>
  <text x="1140" y="880" class="footer-txt" text-anchor="end">v0.1.0-SAE2.01</text>
</svg>
</div>

### Annotations

- **Bandeau d'infos clés** : 6 cellules condensées (n° de carré, département dérivé des 2 premiers chiffres, protocole, date de création, dernière nuit importée, total passages 2026). C'est la fiche d'identité du site.
- **Cards points d'écoute** : 3 cards en ligne (sur écran large) ou en colonne (sur écran étroit). La card C3 montre l'état **GPS manquant** (badge orange `.gps-missing`) qui débloquerait [E6.S3](../Story%20mapping/E6%20-%20Diagnostiquer%20le%20matériel.md#e6s3) (vérification astronomique) si renseigné.
- **Tableau passages** : 7 lignes ici, mais la liste s'allonge à mesure de la saison. Filtrable par statut via le bouton 🔽 en haut à droite.
- **Codes statut** :
    - `Importé` (gris) : copie depuis la SD faite, transformation pas encore lancée
    - `Transformé` (orange) : séquences d'écoute disponibles, vérification à faire ([P3](../Parcours%20utilisateurs/P3%20-%20Vérifier%20l%27enregistrement%20par%20échantillonnage.md))
    - `Vérifié` (rouge si verdict À jeter) ou suivi du verdict OK / Douteux
    - `Déposé` (bleu) : téléversement déclaré sur Vigie-Chiro, attente Tadarida

### Interactions clés

| Élément | Action |
|---|---|
| Breadcrumb « ‹ Mes sites » | Retour à [M-Sites](M-Sites.md) |
| Bouton **✏ Modifier** (header) | Ouvre formulaire d'édition du site (mêmes champs que création) |
| Bouton **🗑 Supprimer** (header) | Confirmation forte. Si des passages sont rattachés, le bouton est désactivé avec message explicite ([E1.S2](../Story%20mapping/E1%20-%20Gérer%20ses%20sites%20et%20points%20de%20suivi.md#e1s2)) |
| Bouton **+ Ajouter un point** | Ouvre la modale d'édition d'un nouveau point (cf. variante ci-dessous) |
| ✏ sur une card de point | Ouvre la même modale en mode édition |
| 🗑 sur une card de point | Confirmation. Bloquée si des passages utilisent ce point |
| Bouton **📥 Importer une nuit** | Ouvre [M-Import](M-Import.md) avec le site pré-sélectionné |
| Clic sur une ligne du tableau | Ouvre [M-Passage](M-Passage.md) du passage sélectionné |
| 🎧 Vérifier (action ligne) | Ouvre [M-Qualification](M-Qualification.md) directement sur ce passage |
| 📂 Ouvrir (action ligne) | Idem clic sur la ligne, ouvre [M-Passage](M-Passage.md) |

---

## Variante - modale d'édition d'un point d'écoute

Activée par le bouton `+ Ajouter un point` ou par l'icône ✏ d'une card existante. Les champs GPS et le descriptif sont **optionnels** mais recommandés.

<div markdown="0">
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 540" role="img" aria-label="Maquette M-Site-detail - Modale ajout/édition d'un point" style="max-width: 100%; height: auto; border: 1px solid #d0d7de; border-radius: 6px; background: rgba(44,62,80,0.6);">
  <style>
    .modal-bg { fill: rgba(44,62,80,0.5); }
    .modal-frame { fill: #ffffff; stroke: #2c3e50; stroke-width: 1.5; }
    .modal-header { fill: #2c3e50; }
    .modal-header-txt { fill: #ffffff; font: 600 16px sans-serif; }
    .modal-close { fill: #ffffff; font: 600 18px sans-serif; }
    .field-label { font: 600 13px sans-serif; fill: #2c3e50; }
    .field-required { font: 600 13px sans-serif; fill: #a93226; }
    .field-hint { font: 12px sans-serif; fill: #6a737d; }
    .field-input { fill: #ffffff; stroke: #6a737d; stroke-width: 1; }
    .field-text { font: 13px sans-serif; fill: #2c3e50; }
    .field-placeholder { font: 13px sans-serif; fill: #bdc3c7; }
    .field-error { fill: #fde7e7; stroke: #a93226; stroke-width: 1.5; }
    .error-msg { font: 12px sans-serif; fill: #a93226; }
    .btn-primary { fill: #4a90d9; stroke: #2563a3; stroke-width: 1; }
    .btn-secondary { fill: #ffffff; stroke: #2c3e50; stroke-width: 1; }
    .btn-txt { fill: #ffffff; font: 600 13px sans-serif; }
    .btn-txt-dark { fill: #2c3e50; font: 600 13px sans-serif; }
    .section-divider { stroke: #eef2f5; stroke-width: 1; }
    .section-label { font: 700 12px sans-serif; fill: #6a737d; letter-spacing: 1px; }
  </style>

  <!-- Fond semi-transparent -->
  <rect x="0" y="0" width="800" height="540" class="modal-bg"/>

  <!-- Modale -->
  <rect x="100" y="50" width="600" height="440" rx="6" class="modal-frame"/>

  <!-- Header modale -->
  <rect x="100" y="50" width="600" height="48" rx="6" class="modal-header"/>
  <rect x="100" y="80" width="600" height="18" class="modal-header"/>
  <text x="124" y="80" class="modal-header-txt">Nouveau point d'écoute · Carré 640380</text>
  <text x="676" y="82" class="modal-close" text-anchor="end">✕</text>

  <!-- Section : identification -->
  <text x="124" y="128" class="section-label">IDENTIFICATION</text>

  <text x="124" y="156" class="field-label">Code du point</text>
  <text x="232" y="156" class="field-required">*</text>
  <rect x="124" y="166" width="120" height="34" rx="3" class="field-input"/>
  <text x="138" y="188" class="field-placeholder">A1</text>
  <text x="260" y="188" class="field-hint">1 lettre majuscule + 1 chiffre (ex. A1, Z4) — règle R2</text>

  <text x="124" y="226" class="field-label">Descriptif</text>
  <text x="190" y="226" class="field-hint">(optionnel, multi-ligne)</text>
  <rect x="124" y="236" width="552" height="56" rx="3" class="field-input"/>
  <text x="138" y="258" class="field-placeholder">Notes pour vous retrouver sur le terrain (« près du chêne, à 30 m... »)</text>

  <!-- Divider -->
  <line x1="124" y1="312" x2="676" y2="312" class="section-divider"/>

  <!-- Section : géolocalisation -->
  <text x="124" y="336" class="section-label">GÉOLOCALISATION (OPTIONNELLE)</text>
  <text x="124" y="354" class="field-hint">📡 Renseigner les coordonnées GPS débloque la vérification astronomique du diagnostic (cf. E6.S3).</text>

  <text x="124" y="382" class="field-label">Latitude</text>
  <rect x="124" y="392" width="180" height="34" rx="3" class="field-input"/>
  <text x="138" y="414" class="field-placeholder">43.5298</text>

  <text x="324" y="382" class="field-label">Longitude</text>
  <rect x="324" y="392" width="180" height="34" rx="3" class="field-input"/>
  <text x="338" y="414" class="field-placeholder">5.4474</text>

  <text x="524" y="412" class="field-hint">Décimal, virgule = point</text>

  <!-- Boutons en bas -->
  <rect x="466" y="450" width="100" height="34" rx="4" class="btn-secondary"/>
  <text x="516" y="471" class="btn-txt-dark" text-anchor="middle">Annuler</text>
  <rect x="576" y="450" width="100" height="34" rx="4" class="btn-primary"/>
  <text x="626" y="471" class="btn-txt" text-anchor="middle">+ Ajouter</text>
</svg>
</div>

### Notes sur la modale

- **Champs requis** vs **optionnels** : seul le code de point est obligatoire (étoile rouge `*`). Le descriptif et les coordonnées GPS sont optionnels mais utiles.
- La **validation R2** (1 lettre majuscule + 1 chiffre) est faite à la saisie : si l'utilisateur tape `AA` ou `1A`, le champ devient rouge avec un message d'erreur.
- La **validation des coordonnées** (latitude entre -90 et 90, longitude entre -180 et 180) est faite à la perte de focus.
- Le bouton **+ Ajouter** devient **Modifier** en mode édition d'un point existant. Les valeurs sont alors pré-remplies.

---

## Variante - confirmation de suppression bloquée

Si l'utilisateur tente de supprimer un point qui a des passages rattachés, l'opération est **bloquée** avec un message explicite ([E1.S2](../Story%20mapping/E1%20-%20Gérer%20ses%20sites%20et%20points%20de%20suivi.md#e1s2) critère « bloquée si des passages y sont rattachés »).

<div markdown="0">
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 380" role="img" aria-label="Maquette M-Site-detail - Suppression de point bloquée" style="max-width: 100%; height: auto; border: 1px solid #d0d7de; border-radius: 6px; background: rgba(44,62,80,0.6);">
  <style>
    .modal-bg { fill: rgba(44,62,80,0.5); }
    .modal-frame { fill: #ffffff; stroke: #a93226; stroke-width: 2; }
    .modal-header { fill: #a93226; }
    .modal-header-txt { fill: #ffffff; font: 600 16px sans-serif; }
    .body-icon { font: 60px sans-serif; }
    .body-title { font: 700 18px sans-serif; fill: #2c3e50; }
    .body-text { font: 13px sans-serif; fill: #2c3e50; }
    .body-hint { font: 12px sans-serif; fill: #6a737d; }
    .btn-primary { fill: #4a90d9; stroke: #2563a3; stroke-width: 1; }
    .btn-secondary { fill: #ffffff; stroke: #2c3e50; stroke-width: 1; }
    .btn-txt { fill: #ffffff; font: 600 13px sans-serif; }
    .btn-txt-dark { fill: #2c3e50; font: 600 13px sans-serif; }
  </style>

  <rect x="0" y="0" width="800" height="380" class="modal-bg"/>
  <rect x="150" y="50" width="500" height="280" rx="6" class="modal-frame"/>

  <rect x="150" y="50" width="500" height="44" rx="6" class="modal-header"/>
  <rect x="150" y="80" width="500" height="14" class="modal-header"/>
  <text x="174" y="80" class="modal-header-txt">⚠ Suppression impossible</text>

  <text x="200" y="170" class="body-icon">🚫</text>

  <text x="280" y="148" class="body-title">Le point A1 ne peut pas être supprimé</text>
  <text x="280" y="178" class="body-text">3 passages enregistrés sont rattachés à ce point.</text>
  <text x="280" y="200" class="body-text">Supprimer le point supprimerait aussi ces passages,</text>
  <text x="280" y="218" class="body-text">y compris leurs fichiers audio sur disque.</text>

  <text x="280" y="252" class="body-hint">Pour le supprimer quand même, supprimez d'abord ses</text>
  <text x="280" y="268" class="body-hint">passages depuis la vue détail du site.</text>

  <rect x="440" y="290" width="100" height="34" rx="4" class="btn-secondary"/>
  <text x="490" y="311" class="btn-txt-dark" text-anchor="middle">Compris</text>
  <rect x="550" y="290" width="100" height="34" rx="4" class="btn-primary"/>
  <text x="600" y="311" class="btn-txt" text-anchor="middle">Voir passages</text>
</svg>
</div>

### Notes sur la suppression bloquée

- L'application **n'autorise jamais** la suppression silencieuse de données métier ([R7](../Modèle%20conceptuel/Règles%20métier.md#r7)/[R9](../Modèle%20conceptuel/Règles%20métier.md#r9) implicites : intégrité des fichiers).
- Le bouton **« Voir passages »** filtre le tableau passages du site sur le point concerné, pour aider l'utilisateur à identifier ce qu'il devrait supprimer en premier.
- Le bouton **« Compris »** ferme simplement la modale sans rien faire.

## Notes pour l'implémentation

- La **section infos clés** est calculée à partir de la BD (DAO sites + DAO passages, agrégation par site_id).
- Le **tableau passages** doit supporter au moins 50 lignes sans pagination (cas Karim avec 2-3 enregistreurs sur le même site). Au-delà, prévoir une pagination ou un filtrage explicite.
- Le **statut workflow** affiché (`Importé`, `Transformé`, `Vérifié`, `Déposé`) est l'attribut persisté en BD ([E0.S3](../Story%20mapping/E0%20-%20Fondations%20de%20persistance.md#e0s3)). La couleur du badge est dérivée du statut, pas stockée.
- Les **icônes d'action** (✏, 🗑, 📂, 🎧) doivent être suffisamment grandes pour être cliquables (touch targets ≥ 24×24 px en pratique, 14px ici pour le mock).

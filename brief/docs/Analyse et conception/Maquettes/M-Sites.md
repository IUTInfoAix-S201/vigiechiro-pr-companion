# M-Sites - Mes sites de suivi

> **Type** : vue principale (point d'entrée de l'application).
> **Persona principal** : [Marie](../Personas/Marie.md), partagée avec [Karim](../Personas/Karim.md). [Samuel](../Personas/Samuel.md) bascule rapidement sur [M-MultiSite](M-MultiSite.md) pour sa volumétrie.
> **Parcours couverts** : [P1 - Déclarer un site de suivi](../Parcours%20utilisateurs/P1%20-%20Déclarer%20un%20site%20de%20suivi.md).
> **Stories couvertes** : [E1.S1 - Saisir un site](../Story%20mapping/E1%20-%20Gérer%20ses%20sites%20et%20points%20de%20suivi.md#e1s1), [E1.S4 - Vue des sites déclarés](../Story%20mapping/E1%20-%20Gérer%20ses%20sites%20et%20points%20de%20suivi.md#e1s4).

C'est le **premier écran** que l'utilisateur voit en démarrant l'application. Tous ses sites de suivi déclarés y sont listés sous forme de cartes, avec pour chacun : le n° de carré, le nom convivial, le nombre de points d'écoute, le nombre de passages enregistrés cette saison, et la date du dernier passage importé. Un bouton primary `+ Nouveau site` est toujours visible en haut à droite. Le clic sur une carte ouvre [M-Site-detail](M-Site-detail.md).

## Wireframe principal - utilisateur avec plusieurs sites déclarés

<div markdown="0">
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1200 700" role="img" aria-label="Maquette M-Sites - Mes sites de suivi" style="max-width: 100%; height: auto; border: 1px solid #d0d7de; border-radius: 6px; background: #fafbfc;">
  <style>
    .frame { fill: #ffffff; stroke: #2c3e50; stroke-width: 1.5; }
    .titlebar { fill: #2c3e50; }
    .titletxt { fill: #ffffff; font: 600 14px sans-serif; }
    .topnav { fill: #34495e; }
    .navtxt { fill: #ffffff; font: 600 13px sans-serif; }
    .navtxt-active { fill: #ffffff; font: 600 13px sans-serif; }
    .navtxt-inactive { fill: #bdc3c7; font: 400 13px sans-serif; }
    .navunder { stroke: #4a90d9; stroke-width: 3; fill: none; }
    .pagetitle { font: 700 22px sans-serif; fill: #2c3e50; }
    .pagesub { font: 13px sans-serif; fill: #6a737d; }
    .btn-primary { fill: #4a90d9; stroke: #2563a3; stroke-width: 1; }
    .btn-txt { fill: #ffffff; font: 600 13px sans-serif; }
    .card { fill: #ffffff; stroke: #d0d7de; stroke-width: 1; }
    .card-hover { fill: #f6f8fa; stroke: #4a90d9; stroke-width: 1.5; }
    .carre-no { font: 700 20px sans-serif; fill: #2c3e50; }
    .carre-name { font: 500 14px sans-serif; fill: #6a737d; }
    .stat-num { font: 700 18px sans-serif; fill: #4a90d9; }
    .stat-label { font: 11px sans-serif; fill: #6a737d; }
    .stat-divider { stroke: #d0d7de; stroke-width: 1; }
    .badge-fresh { fill: #d4edda; stroke: #27ae60; stroke-width: 1; }
    .badge-stale { fill: #fff3cd; stroke: #b9770e; stroke-width: 1; }
    .badge-cold { fill: #f6f8fa; stroke: #6a737d; stroke-width: 1; }
    .badge-fresh-txt { font: 600 11px sans-serif; fill: #1e6f3f; }
    .badge-stale-txt { font: 600 11px sans-serif; fill: #7e5109; }
    .badge-cold-txt { font: 600 11px sans-serif; fill: #2c3e50; }
    .ico-action { fill: #6a737d; }
    .chev { fill: #6a737d; }
    .footer { fill: #f6f8fa; stroke: #d0d7de; stroke-width: 0.5; }
    .footer-txt { font: 11px sans-serif; fill: #6a737d; }
  </style>

  <!-- Cadre fenêtre -->
  <rect x="10" y="10" width="1180" height="680" rx="4" class="frame"/>

  <!-- Title bar -->
  <rect x="10" y="10" width="1180" height="32" rx="4" class="titlebar"/>
  <rect x="10" y="26" width="1180" height="16" class="titlebar"/>
  <text x="28" y="31" class="titletxt">🦇 VigieChiro PR Companion</text>
  <text x="1140" y="31" class="titletxt" text-anchor="end">— ☐ ☓</text>

  <!-- Top nav -->
  <rect x="10" y="42" width="1180" height="40" class="topnav"/>
  <text x="40" y="67" class="navtxt-active">🏠 Mes sites</text>
  <line x1="32" y1="78" x2="142" y2="78" class="navunder"/>
  <text x="170" y="67" class="navtxt-inactive">📥 Importer une nuit</text>
  <text x="330" y="67" class="navtxt-inactive">📊 Vue tabulaire</text>
  <text x="470" y="67" class="navtxt-inactive">⚙ Paramètres</text>
  <text x="1140" y="67" class="navtxt-inactive" text-anchor="end">👤 Local</text>

  <!-- Page header -->
  <text x="40" y="120" class="pagetitle">Mes sites de suivi</text>
  <text x="40" y="142" class="pagesub">3 sites déclarés · 12 passages enregistrés en 2026</text>

  <!-- Bouton primary "+ Nouveau site" -->
  <rect x="990" y="100" width="170" height="40" rx="4" class="btn-primary"/>
  <text x="1075" y="125" class="btn-txt" text-anchor="middle">+ Nouveau site</text>

  <!-- Carte site 1 : Carré 640380 (frais, dernier passage récent) -->
  <rect x="40" y="170" width="1120" height="120" rx="6" class="card-hover"/>
  <!-- Colonne 1: n° de carré + nom -->
  <text x="64" y="206" class="carre-no">Carré 640380</text>
  <text x="64" y="228" class="carre-name">📍 Étang de la Tuilière (Aix-en-Provence)</text>
  <!-- Badge fraîcheur -->
  <rect x="64" y="244" width="160" height="22" rx="11" class="badge-fresh"/>
  <text x="144" y="259" class="badge-fresh-txt" text-anchor="middle">✓ Dernier passage : il y a 2 j</text>
  <!-- Divider vertical -->
  <line x1="500" y1="190" x2="500" y2="270" class="stat-divider"/>
  <!-- Colonne 2: stats points -->
  <text x="540" y="208" class="stat-num">3</text>
  <text x="540" y="226" class="stat-label">points d'écoute</text>
  <text x="540" y="248" class="carre-name">A1 · B2 · C3</text>
  <!-- Divider vertical -->
  <line x1="720" y1="190" x2="720" y2="270" class="stat-divider"/>
  <!-- Colonne 3: stats passages -->
  <text x="760" y="208" class="stat-num">7</text>
  <text x="760" y="226" class="stat-label">passages en 2026</text>
  <text x="760" y="248" class="carre-name">dont 1 à vérifier ⚠</text>
  <!-- Divider vertical -->
  <line x1="940" y1="190" x2="940" y2="270" class="stat-divider"/>
  <!-- Colonne 4: actions -->
  <rect x="970" y="200" width="170" height="34" rx="4" class="btn-primary"/>
  <text x="1055" y="222" class="btn-txt" text-anchor="middle">📥 Importer une nuit</text>
  <text x="970" y="258" class="pagesub">✏ Modifier · 🗑 Supprimer</text>
  <!-- Chevron clickable indicator -->
  <text x="1145" y="234" class="chev" text-anchor="end" font-size="18">›</text>

  <!-- Carte site 2 : Carré 752204 (passage il y a une semaine) -->
  <rect x="40" y="310" width="1120" height="120" rx="6" class="card"/>
  <text x="64" y="346" class="carre-no">Carré 752204</text>
  <text x="64" y="368" class="carre-name">📍 ZAC Nord (Marseille)</text>
  <rect x="64" y="384" width="160" height="22" rx="11" class="badge-stale"/>
  <text x="144" y="399" class="badge-stale-txt" text-anchor="middle">⏱ Dernier passage : il y a 8 j</text>
  <line x1="500" y1="330" x2="500" y2="410" class="stat-divider"/>
  <text x="540" y="348" class="stat-num">2</text>
  <text x="540" y="366" class="stat-label">points d'écoute</text>
  <text x="540" y="388" class="carre-name">A1 · B2</text>
  <line x1="720" y1="330" x2="720" y2="410" class="stat-divider"/>
  <text x="760" y="348" class="stat-num">3</text>
  <text x="760" y="366" class="stat-label">passages en 2026</text>
  <text x="760" y="388" class="carre-name">tous vérifiés</text>
  <line x1="940" y1="330" x2="940" y2="410" class="stat-divider"/>
  <rect x="970" y="340" width="170" height="34" rx="4" class="btn-primary"/>
  <text x="1055" y="362" class="btn-txt" text-anchor="middle">📥 Importer une nuit</text>
  <text x="970" y="398" class="pagesub">✏ Modifier · 🗑 Supprimer</text>
  <text x="1145" y="374" class="chev" text-anchor="end" font-size="18">›</text>

  <!-- Carte site 3 : Carré 013570 (test, aucun passage) -->
  <rect x="40" y="450" width="1120" height="120" rx="6" class="card"/>
  <text x="64" y="486" class="carre-no">Carré 013570</text>
  <text x="64" y="508" class="carre-name">📍 Carré de test (sans coordonnées GPS)</text>
  <rect x="64" y="524" width="140" height="22" rx="11" class="badge-cold"/>
  <text x="134" y="539" class="badge-cold-txt" text-anchor="middle">– Aucun passage</text>
  <line x1="500" y1="470" x2="500" y2="550" class="stat-divider"/>
  <text x="540" y="488" class="stat-num">1</text>
  <text x="540" y="506" class="stat-label">point d'écoute</text>
  <text x="540" y="528" class="carre-name">A1</text>
  <line x1="720" y1="470" x2="720" y2="550" class="stat-divider"/>
  <text x="760" y="488" class="stat-num">0</text>
  <text x="760" y="506" class="stat-label">passages en 2026</text>
  <text x="760" y="528" class="carre-name">jamais utilisé</text>
  <line x1="940" y1="470" x2="940" y2="550" class="stat-divider"/>
  <rect x="970" y="480" width="170" height="34" rx="4" class="btn-primary"/>
  <text x="1055" y="502" class="btn-txt" text-anchor="middle">📥 Importer une nuit</text>
  <text x="970" y="538" class="pagesub">✏ Modifier · 🗑 Supprimer</text>
  <text x="1145" y="514" class="chev" text-anchor="end" font-size="18">›</text>

  <!-- Footer -->
  <rect x="10" y="660" width="1180" height="30" class="footer"/>
  <text x="40" y="680" class="footer-txt">💾 Base locale : ~/VigieChiroCompanion/companion.db · ✓ Schéma à jour</text>
  <text x="1140" y="680" class="footer-txt" text-anchor="end">v0.1.0-SAE2.01</text>
</svg>
</div>

### Annotations

- **Carte 1 (Carré 640380)** : c'est le site en cours d'utilisation, affiché en surbrillance bleue (`.card-hover`) car le pointeur de souris le survole. Le badge vert « il y a 2 j » + l'indicateur « 1 à vérifier ⚠ » incitent à enchaîner sur l'import ou la vérification.
- **Carte 2 (Carré 752204)** : site secondaire, dernier passage il y a une semaine (badge orange). Pas de passage urgent à traiter.
- **Carte 3 (Carré 013570)** : site déclaré mais jamais utilisé. Badge gris neutre. Idéal pour montrer qu'on peut préparer un site avant la première nuit.

### Interactions clés

| Élément | Action |
|---|---|
| Clic sur la carte | Ouvre [M-Site-detail](M-Site-detail.md) avec le site sélectionné |
| Bouton **+ Nouveau site** (en haut à droite) | Ouvre le formulaire de création d'un site ([E1.S1](../Story%20mapping/E1%20-%20Gérer%20ses%20sites%20et%20points%20de%20suivi.md#e1s1)) |
| Bouton **📥 Importer une nuit** | Ouvre [M-Import](M-Import.md) avec le site et le point pré-sélectionnés ([E2.S2](../Story%20mapping/E2%20-%20Importer%20et%20transformer%20une%20nuit.md#e2s2)) |
| ✏ Modifier | Ouvre le formulaire d'édition (mêmes champs que création, valeurs pré-remplies) |
| 🗑 Supprimer | Confirmation forte si des passages sont rattachés au site ([E1.S2](../Story%20mapping/E1%20-%20Gérer%20ses%20sites%20et%20points%20de%20suivi.md#e1s2)) |
| Onglet **Vue tabulaire** (top nav) | Bascule vers [M-MultiSite](M-MultiSite.md) (vue multi-sites pour Karim/Samuel) |

---

## Variante - état vide (premier lancement)

À la toute première ouverture de l'application, aucun site n'est encore déclaré. Plutôt que d'afficher une vue vide, on guide explicitement l'utilisateur vers la création de son premier site.

<div markdown="0">
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1200 700" role="img" aria-label="Maquette M-Sites - État vide (premier lancement)" style="max-width: 100%; height: auto; border: 1px solid #d0d7de; border-radius: 6px; background: #fafbfc;">
  <style>
    .frame { fill: #ffffff; stroke: #2c3e50; stroke-width: 1.5; }
    .titlebar { fill: #2c3e50; }
    .titletxt { fill: #ffffff; font: 600 14px sans-serif; }
    .topnav { fill: #34495e; }
    .navtxt-active { fill: #ffffff; font: 600 13px sans-serif; }
    .navtxt-inactive { fill: #bdc3c7; font: 400 13px sans-serif; }
    .navunder { stroke: #4a90d9; stroke-width: 3; fill: none; }
    .pagetitle { font: 700 22px sans-serif; fill: #2c3e50; }
    .empty-icon { font: 80px sans-serif; fill: #d0d7de; }
    .empty-title { font: 700 22px sans-serif; fill: #2c3e50; }
    .empty-sub { font: 14px sans-serif; fill: #6a737d; }
    .btn-primary-big { fill: #4a90d9; stroke: #2563a3; stroke-width: 1; }
    .btn-txt-big { fill: #ffffff; font: 600 15px sans-serif; }
    .hint-box { fill: #fef9e7; stroke: #b9770e; stroke-width: 1; }
    .hint-txt { font: 13px sans-serif; fill: #5d4e00; }
    .hint-title { font: 600 13px sans-serif; fill: #5d4e00; }
    .footer { fill: #f6f8fa; stroke: #d0d7de; stroke-width: 0.5; }
    .footer-txt { font: 11px sans-serif; fill: #6a737d; }
  </style>

  <rect x="10" y="10" width="1180" height="680" rx="4" class="frame"/>
  <rect x="10" y="10" width="1180" height="32" rx="4" class="titlebar"/>
  <rect x="10" y="26" width="1180" height="16" class="titlebar"/>
  <text x="28" y="31" class="titletxt">🦇 VigieChiro PR Companion</text>
  <text x="1140" y="31" class="titletxt" text-anchor="end">— ☐ ☓</text>

  <rect x="10" y="42" width="1180" height="40" class="topnav"/>
  <text x="40" y="67" class="navtxt-active">🏠 Mes sites</text>
  <line x1="32" y1="78" x2="142" y2="78" class="navunder"/>
  <text x="170" y="67" class="navtxt-inactive">📥 Importer une nuit</text>
  <text x="330" y="67" class="navtxt-inactive">📊 Vue tabulaire</text>
  <text x="470" y="67" class="navtxt-inactive">⚙ Paramètres</text>
  <text x="1140" y="67" class="navtxt-inactive" text-anchor="end">👤 Local</text>

  <text x="40" y="120" class="pagetitle">Mes sites de suivi</text>

  <!-- Zone centrale "vide" avec gros CTA -->
  <text x="600" y="280" class="empty-icon" text-anchor="middle">🌐</text>
  <text x="600" y="340" class="empty-title" text-anchor="middle">Bienvenue ! Commencez par déclarer votre premier site.</text>
  <text x="600" y="370" class="empty-sub" text-anchor="middle">Un site = un carré Vigie-Chiro (6 chiffres) avec un ou plusieurs points d'écoute.</text>
  <text x="600" y="392" class="empty-sub" text-anchor="middle">Vous ne pouvez pas importer une nuit tant qu'aucun site n'est déclaré.</text>

  <!-- Gros bouton primary centré -->
  <rect x="430" y="420" width="340" height="56" rx="6" class="btn-primary-big"/>
  <text x="600" y="455" class="btn-txt-big" text-anchor="middle">+ Ajouter mon premier site de suivi</text>

  <!-- Encart d'aide -->
  <rect x="280" y="510" width="640" height="130" rx="6" class="hint-box"/>
  <text x="300" y="535" class="hint-title">💡 Vous n'avez pas encore créé votre site sur Vigie-Chiro ?</text>
  <text x="300" y="558" class="hint-txt">Le carré et les points doivent d'abord être déclarés sur le portail web</text>
  <text x="300" y="578" class="hint-txt">vigiechiro.herokuapp.com - récupérez ensuite le n° de carré (6 chiffres) et les</text>
  <text x="300" y="598" class="hint-txt">codes points (ex. A1, B2) pour les saisir ici.</text>
  <text x="300" y="624" class="hint-txt">🔗 https://vigiechiro.herokuapp.com (s'ouvre dans le navigateur)</text>

  <rect x="10" y="660" width="1180" height="30" class="footer"/>
  <text x="40" y="680" class="footer-txt">💾 Base locale : ~/VigieChiroCompanion/companion.db · ✓ Schéma initialisé</text>
  <text x="1140" y="680" class="footer-txt" text-anchor="end">v0.1.0-SAE2.01</text>
</svg>
</div>

### Notes sur l'état vide

- Le bouton **+ Ajouter mon premier site** est dimensionné explicitement plus grand que le bouton normal pour le marquer comme l'**unique action** disponible.
- Le **rappel pédagogique** dans l'encart jaune est volontaire : Marie (persona débutante) peut ne pas savoir que les sites doivent d'abord être créés sur le portail web. Mieux vaut le dire ici qu'attendre un message d'erreur cryptique.
- Une fois le premier site créé, la vue bascule immédiatement vers le wireframe principal (une carte présente).

## Notes pour l'implémentation

- Cette vue se base entièrement sur la story [E1.S4](../Story%20mapping/E1%20-%20Gérer%20ses%20sites%20et%20points%20de%20suivi.md#e1s4) pour la logique d'agrégation (compteurs de passages, date du dernier passage).
- Le **survol** d'une carte (`.card-hover`) doit être implémenté via un effet CSS / pseudo-class JavaFX, pas via une carte différente dans le DOM.
- Les **badges colorés de fraîcheur** sont calculés à partir de la date du dernier passage :
    - vert `.badge-fresh` si dernier passage < 7 jours
    - orange `.badge-stale` si dernier passage entre 7 et 30 jours
    - gris `.badge-cold` si > 30 jours ou aucun passage
- Le bouton **📥 Importer une nuit** par carte est un raccourci ergonomique pour Karim/Samuel : il pré-sélectionne le site + propose la liste des points dans la modale d'import suivante.

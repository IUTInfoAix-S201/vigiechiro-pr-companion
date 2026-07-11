# Lot

L'écran **Lot** (« Préparer le dépôt ») prépare et trace le **dépôt** d'une nuit vérifiée sur la
plateforme Vigie-Chiro. Le dépôt suit un **flux ordonné en quatre étapes**, rappelé en haut de l'écran
par un fil d'étapes (l'étape courante est mise en avant) :

**① Préparer · ② Générer les archives · ③ Téléverser · ④ Marquer déposé.**

## ① Vérifier et préparer le lot

![L'écran Lot : récapitulatif du lot et première étape « Vérifier et préparer le lot ».](../assets/captures/apercu-lot-preparer.png)

Le **récapitulatif** indique le nombre de séquences et le volume. Une **checklist de cohérence** montre,
contrôle par contrôle et **même quand tout est satisfait**, ce qui est vérifié : transformation
effectuée, fichiers bien nommés, journal du capteur présent, relevé climatique. Chaque ligne est marquée
**✓** (satisfait), **✗** (à corriger, bloquant) ou **⚠** (avertissement non bloquant, comme un relevé
climatique absent). « Vérifier et préparer le lot » **verrouille** ensuite la liste des séquences qui
partiront. Vos fichiers d'origine ne sont pas modifiés. Le passage passe alors au statut « Prêt à
déposer ».

## ② Générer les archives de dépôt

![L'état « Prêt à déposer » : l'étape « Générer les archives » devient active.](../assets/captures/apercu-lot-deposer.png)

Ce que l'on téléverse sur Vigie-Chiro, ce sont des **archives ZIP** (≤ 700 Mo), découpées depuis les
séquences et écrites dans le sous-dossier `depot/` de la session. La génération peut être **longue**
sur une grosse nuit : elle s'exécute en arrière-plan, avec un indicateur d'activité, et les actions
sont neutralisées le temps de l'écriture (on ne risque pas de téléverser une archive incomplète).

![Génération des archives en cours : indicateur d'activité, actions désactivées.](../assets/captures/apercu-lot-generation.png)

Le tableau de suivi des archives laisse **choisir et réordonner ses colonnes** (clic droit ou menu ☰
« outils ») : voir [Personnaliser les tableaux](../personnaliser-les-tableaux.md).

## ③ Téléverser sur Vigie-Chiro

![Archives générées : la liste des ZIP s'affiche et « Ouvrir le dossier » s'active.](../assets/captures/apercu-lot-archives.png)

Une fois les archives produites, deux chemins s'offrent à vous :

- **Téléversement automatique** (application connectée à Vigie-Chiro) : le bouton
  **« Téléverser sur Vigie-Chiro »** dépose la nuit directement — la participation est créée (ou
  réutilisée si elle l'a été à l'import), puis chaque fichier est téléversé. Une **table de dépôt**
  suit chaque fichier (en attente → en cours → déposé, ou échec avec la raison au survol), et la
  **barre de statut** en bas de la fenêtre affiche l'avancement en continu, même quand vous faites
  défiler l'écran.
- **Téléversement manuel** (repli, sans connexion) : **« Ouvrir le dossier »** ouvre le sous-dossier
  `depot/` dans le gestionnaire de fichiers, et vous déposez les archives sur Vigie-Chiro depuis votre
  navigateur.

### Un dépôt interrompu se reprend

Le dépôt automatique est **reprenable** : une coupure réseau, une fermeture de l'application ou un
échec partiel ne font **rien perdre**. Le passage prend le statut « **Dépôt en cours** » et, à la
réouverture de l'écran, la table de dépôt réaffiche l'état exact de chaque fichier. Le bouton devient
alors « **Retenter les échecs** » : seuls les fichiers manquants sont re-téléversés — jamais ceux déjà
en ligne. Le passage ne devient « Déposé » que lorsque **tous** les fichiers sont en ligne.

## ④ Marquer le passage déposé

![L'état « Déposé » : toutes les étapes sont franchies.](../assets/captures/apercu-lot-depose.png)

Cette étape ne concerne que le **téléversement manuel** : une fois vos archives déposées depuis le
navigateur, « Marquer déposé » fait passer le passage au statut « Déposé » (ce qui déverrouille ensuite
la validation Tadarida) et trace la date du dépôt. Avec le téléversement automatique, ce marquage est
fait pour vous, à la fin d'un dépôt complet.

## La barre de statut : l'état du lot en permanence

L'écran est long ; la **barre de statut** du bas de fenêtre garde l'essentiel sous les yeux :

- à **gauche**, le contexte (« Carré 640380 · A1 · N° 2 ») ;
- au **centre**, le statut et le récapitulatif (« Prêt à déposer · 4806 séquences · 13,2 Go ») ;
- à **droite**, l'état vivant : la progression du dépôt, sinon celle de la génération d'archives
  (avec l'estimation du temps restant), sinon une alerte d'espace disque, sinon le bilan des archives
  présentes (« 21 archive(s) · 5,9 Go dans depot/ »).

## Checklist de cohérence : ce qui bloque

Si la nuit n'est pas en état d'être déposée (par exemple séquences d'écoute absentes ou journal du
capteur manquant), les contrôles concernés passent en **✗** dans la checklist, avec la raison et la
correction à apporter. Le bouton « Vérifier et préparer le lot » reste grisé tant qu'un contrôle est en
échec. Un **⚠** (relevé climatique absent) n'empêche pas, lui, de préparer le lot.

![L'état incohérent : la checklist montre les contrôles ✓ et ✗ ; la préparation est bloquée.](../assets/captures/apercu-lot-alertes.png)

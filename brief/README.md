# SAE 2.01 - VigieChiro PR Companion (édition 2026)

> Énoncé pédagogique de la SAE 2.01 du semestre 2 du BUT Informatique, IUT d'Aix-Marseille.
> SAE commune aux modules **R2.02 - Développement d'applications avec IHM** et **R2.03 - Qualité de développement**.

## Sommaire

1. [Présentation du projet](Présentation%20du%20projet.md)
2. [Contraintes techniques](Contraintes%20techniques.md)
3. [Objectifs qualités](Objectifs%20qualités.md)
4. [Expression du besoin](Expression%20du%20besoin.md)
5. [**Analyse et conception**](Analyse%20et%20conception/) (dossier fourni : personas, parcours, story mapping, périmètre MVP, planification)
6. [Jalons et livrables](Jalons%20et%20livrables.md)
7. [Calendrier de travail](Calendrier%20de%20travail.md)
8. [Consignes générales](Consignes%20générales.md)

> 📌 **SAE de développement** : la phase d'analyse et de conception est portée par l'équipe pédagogique. Vous recevez un dossier de spécification opérationnel ([`Analyse et conception/`](Analyse%20et%20conception/)) et vous concentrez votre énergie sur le développement (R2.02) et la qualité de la production (R2.03).

## Données d'exemple fournies

Un jeu de données réel issu d'une session de capture nocturne (22-23 avril 2026, PR n° 1925492) vous est fourni en deux variantes complémentaires :

### 🟢 Sample versionné dans le dépôt - [`samples/`](samples/)

**~518 Mo** disponibles immédiatement après `git clone`. Suffit pour démarrer, pour la CI, et pour tester la majorité des stories. Contient le LogPR + THLog complets, **191 WAV** redécoupés couvrant tous les taxa principaux, et les **2 CSV d'observations** filtrés en cohérence (473 obs sur 4031). Voir [`samples/README.md`](samples/README.md) pour le détail.

### 🔵 Full dataset à télécharger - `data/` (gitignored)

**11 Go** : 1572 WAV bruts + 2114 WAV redécoupés + 4031 observations Tadarida. Indispensable pour valider les stories de **volumétrie** ([O3](Objectifs%20qualités/Objectifs%20qualités/O3.md), [O5](Objectifs%20qualités/Objectifs%20qualités/O5.md)).

> ⏳ **À venir** : lien de téléchargement (AmeTICE / Nextcloud AMU). En attendant, demandez-le à l'équipe pédagogique. Une fois l'archive récupérée :
>
> ```bash
> mkdir -p data
> unzip ~/Téléchargements/20260423-selected.zip -d data/
> ```
>
> Le dossier `data/` est listé dans le `.gitignore` : aucun risque de commit accidentel.

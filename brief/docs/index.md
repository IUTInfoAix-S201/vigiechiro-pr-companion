# SAE 2.01 - VigieChiro PR Companion

<div markdown="0" style="margin: 1rem 0 2rem 0;">
  <img src="assets/illustrations/hero-bandeau.webp"
       alt="Nuit de session d'enregistrement acoustique : un Passive Recorder sur son piquet, des chauves-souris en vol émettant des ondes d'écholocation sous une pleine lune en lisière de forêt"
       style="width: 100%; height: auto; display: block; border-radius: 4px;">
</div>

> Dossier de conception et **documentation produit** de *VigieChiro PR Companion*, l'application née de
> la SAE 2.01 du semestre 2 du BUT Informatique de l'IUT d'Aix-Marseille (SAE commune aux modules
> **R2.02 - Développement d'applications avec IHM** et **R2.03 - Qualité de développement**).

!!! info "De l'énoncé au produit"
    Ce dossier a d'abord été l'**énoncé** d'une SAE de développement. Il décrit désormais l'application
    **telle qu'elle a été construite** : le besoin, le modèle de données, les parcours et les écrans
    réels. La genèse pédagogique (story mapping, arbitrage MVP, planification) est conservée mais
    rédigée **au passé**, comme mémoire du projet. Le code de l'application est public :
    [IUTInfoAix-S201/vigiechiro-pr-companion](https://github.com/IUTInfoAix-S201/vigiechiro-pr-companion)
    (documentation utilisateur et d'architecture séparées).

!!! tip "Présentation en slides"
    Une vue d'ensemble rapide du projet est disponible en slides :
    [**VigieChiro PR Companion**](https://iutinfoaix-r202.github.io/cours/presentation-sae-2.01.html)
    (support R2.02), à parcourir avant de plonger dans les sections détaillées ci-dessous.

## Sommaire

1. [Présentation du projet](Présentation%20du%20projet.md)
2. [Stack technique et architecture](Contraintes%20techniques.md)
3. [Objectifs qualités](Objectifs%20qualités/index.md)
4. [Expression du besoin](Expression%20du%20besoin.md)
5. [**Analyse et conception**](Analyse%20et%20conception/index.md) (modèle conceptuel, personas, parcours, et — au passé — story mapping, périmètre MVP, planification)
6. [Genèse pédagogique](Travail%20à%20faire.md) (méthode et organisation de la SAE, conservées pour mémoire)
7. [Jalons et livrables](Jalons%20et%20livrables.md)
8. [Calendrier de travail](Calendrier%20de%20travail.md)
9. [Consignes générales](Consignes%20générales.md)

## Données d'exemple fournies

Un jeu de données réel issu d'une session d'enregistrement nocturne (22-23 avril 2026, PR n° 1925492) accompagne le projet en deux variantes complémentaires :

### 🟢 Échantillon versionné dans un dépôt dédié

Un **échantillon représentatif** d'une nuit (audio réduit + observations complètes) est versionné dans le dépôt [`vigiechiro-pr-companion-exemple-nuit`](https://github.com/IUTInfoAix-S201/vigiechiro-pr-companion-exemple-nuit), disponible immédiatement après `git clone`. Suffit pour démarrer, pour la CI, et pour tester la majorité des stories. Le détail du contenu est décrit dans le `README` de ce dépôt.

### 🔵 Full dataset sur Zenodo

**~4,2 Go zippés** (~11 Go décompressés) : 1572 WAV bruts + 2109 WAV redécoupés + 4031 observations Tadarida. Indispensable pour valider les stories de **volumétrie** ([O3](Objectifs%20qualités/Objectifs%20qualités/O3.md), [O5](Objectifs%20qualités/Objectifs%20qualités/O5.md)).

!!! tip "Archive permanente : DOI Zenodo"
    La nuit complète est archivée sur Zenodo, DOI [10.5281/zenodo.20492247](https://doi.org/10.5281/zenodo.20492247) (lien permanent, accès libre) : c'est elle qui permet d'éprouver l'application sur les volumes réels.

    **Depuis votre navigateur** : [fiche Zenodo du jeu de données](https://zenodo.org/records/20492247).

    **Depuis la ligne de commande**, à la racine de votre clone du brief :

    ```bash
    mkdir -p data
    curl -L -o data/Car640380-2026-Pass2-Z1.zip \
      "https://zenodo.org/records/20492247/files/Car640380-2026-Pass2-Z1.zip?download=1"
    unzip data/Car640380-2026-Pass2-Z1.zip -d data/
    rm data/Car640380-2026-Pass2-Z1.zip   # optionnel, libère ~4,2 Go
    ```

    Le dossier `data/` est listé dans le `.gitignore` : aucun risque de commit accidentel.

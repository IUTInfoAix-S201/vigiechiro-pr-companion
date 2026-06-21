# Prise en main

Cette section explique comment **installer**, **lancer** VigieChiro et **situer les écrans** au
premier démarrage.

## Installer

Des installeurs prêts à l'emploi (Linux, macOS, Windows) sont publiés sur la page
[Releases](https://github.com/IUTInfoAix-S201/vigiechiro-pr-companion/releases) du projet :
téléchargez celui de votre système, puis installez-le comme une application classique.

!!! note "Avertissement de sécurité possible"
    Les installeurs ne sont pas signés. Votre système peut afficher un avertissement à la
    première ouverture (Gatekeeper sur macOS, SmartScreen sur Windows) : autorisez l'application
    pour continuer.

## Lancer depuis les sources

Si vous travaillez à partir du code, l'application se lance avec le Maven Wrapper :

```bash
./mvnw javafx:run
```

## Premier démarrage

Au lancement, l'application ouvre son **écran d'accueil**, d'où vous accédez aux différentes
activités (sites, importation, qualification, dépôt...). Le [parcours métier](parcours/index.md)
détaille l'ordre dans lequel les utiliser.

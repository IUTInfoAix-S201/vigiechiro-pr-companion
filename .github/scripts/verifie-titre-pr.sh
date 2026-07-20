#!/usr/bin/env bash
# Vérifie qu'un titre de PR suit Conventional Commits (#2105).
#
# Le dépôt fusionne en SQUASH avec `squash_merge_commit_title = PR_TITLE` : le titre de la PR devient
# le sujet du commit sur `main`, et les messages des commits de la branche sont écartés à la fusion.
# C'est donc ce titre, et lui seul, que semantic-release lira.
#
# Ce contrôle existe parce que son absence a coûté cher : la pratique a dérivé vers `type(scope) :`
# (avec l'espace français avant les deux-points), que Conventional Commits n'admet pas. semantic-release
# a cessé de publier le 2026-07-18 en finissant VERT à chaque push, et 58 commits releasables se sont
# accumulés sans que rien ne le signale. Cf. ADR 0040.
#
# Usage : verifie-titre-pr.sh "<titre>"   (sortie 0 si conforme, 1 sinon)
set -euo pipefail

TITRE="${1-}"

# Types réellement pratiqués dans le dépôt. `feat` -> mineure, `fix`/`perf` -> patch ; les autres ne
# déclenchent pas de version (cf. CONTRIBUTING.md §3).
MOTIF='^(feat|fix|perf|refactor|docs|test|chore|ci|build|style|revert)(\([a-z0-9._-]+\))?!?: .+'

if [ -z "${TITRE}" ]; then
    echo "::error::Titre de PR vide."
    exit 1
fi

echo "Titre : ${TITRE}"

if printf '%s' "${TITRE}" | grep -qE "${MOTIF}"; then
    echo "Titre conforme."
    exit 0
fi

echo "::error::Le titre de la PR ne suit pas Conventional Commits."
echo

# L'erreur la plus fréquente mérite d'être nommée : c'est celle qui a arrêté la release.
if printf '%s' "${TITRE}" | grep -qE '^[a-z]+(\([a-z0-9._-]+\))? +:'; then
    ATTENDU=$(printf '%s' "${TITRE}" | sed -E 's/^([a-z]+(\([a-z0-9._-]+\))?) +:/\1:/')
    echo "Cause probable : un ESPACE avant les deux-points."
    echo
    echo "  écrit   : ${TITRE}"
    echo "  attendu : ${ATTENDU}"
    echo
    echo "Dans 'feat(scope): sujet', le ':' est un token de syntaxe, pas une ponctuation de phrase :"
    echo "la règle typographique française de l'espace avant ':' ne s'y applique pas. Un espace y rend"
    echo "le titre illisible pour semantic-release, qui cesse de publier SANS rougir."
    echo "Cf. dev-docs/decisions/0040-le-sujet-de-commit-est-une-syntaxe.md"
else
    echo "Forme attendue : type(scope): sujet en français"
    echo
    echo "  feat(passage): écran pivot d'une nuit (statut + navigation)"
    echo "  fix(importation): import hors fil JavaFX gelait l'écran"
    echo
    echo "Types admis : feat, fix, perf, refactor, docs, test, chore, ci, build, style, revert."
fi
exit 1

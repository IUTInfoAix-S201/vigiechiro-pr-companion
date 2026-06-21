#!/usr/bin/env bash
# Garde-fou : toute capture d'écran référencée par une page de doc utilisateur
# (docs/**/*.md, motif apercu-*.png) doit À LA FOIS exister dans .github/assets/
# ET être déclarée dans captures.manifest (donc régénérée par capture-vues.yml).
#
# Complète check-captures.sh : celui-ci garantit qu'aucune VUE n'est livrée sans
# capture ; celui-là garantit qu'aucune PAGE ne référence une capture absente ou
# non régénérée. Lancé en CI par .github/workflows/docs.yml.
#
# Exit 0 si tout est cohérent, 1 sinon (détails sur stdout).
set -uo pipefail

ICI="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
RACINE="$(cd "$ICI/../.." && pwd)"
cd "$RACINE"

ASSETS=".github/assets"
MANIFEST="$ASSETS/captures.manifest"
ERREURS=0

# Captures DÉCLARÉES au manifeste (jetons apercu-*.png après le « : »).
declarees="$(grep -v '^[[:space:]]*#' "$MANIFEST" \
  | sed 's/^[^:]*://' \
  | tr ' \t' '\n\n' \
  | grep -E '^apercu-[a-z0-9-]+\.png$' \
  | sort -u)"

# Captures RÉFÉRENCÉES par la doc.
referencees="$(grep -rhoE 'apercu-[a-z0-9-]+\.png' docs --include='*.md' 2>/dev/null | sort -u)"

if [ -z "$referencees" ]; then
  echo "Aucune capture référencée par la doc : rien à vérifier."
  exit 0
fi

while IFS= read -r png; do
  [ -z "$png" ] && continue
  if [ ! -f "$ASSETS/$png" ]; then
    echo "✗ $png : référencée par la doc mais ABSENTE de $ASSETS/"
    ERREURS=$((ERREURS + 1))
    continue
  fi
  if ! printf '%s\n' "$declarees" | grep -qx "$png"; then
    echo "✗ $png : présente mais NON déclarée dans captures.manifest (à ajouter pour la régénération)"
    ERREURS=$((ERREURS + 1))
  fi
done <<< "$referencees"

if [ "$ERREURS" -eq 0 ]; then
  echo "✓ Toutes les captures référencées par la doc existent et sont déclarées au manifeste."
  exit 0
fi
echo ""
echo "✗ $ERREURS capture(s) de doc manquante(s)."
echo "  Pour en ajouter une : rendu dans le Capture* de la feature + entrée dans captures.manifest (cf. #191)."
exit 1

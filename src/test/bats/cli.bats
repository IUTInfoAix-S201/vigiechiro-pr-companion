#!/usr/bin/env bats
#
# E2E de la CLI vigiechiro (#1572, chantier #1565) au niveau SHELL, sur le fat-jar shadé : ce que les
# tests Java in-process (CliTest & co.) ne voient pas — le packaging réel, l'analyse des arguments par
# picocli, et les CODES DE SORTIE d'un vrai processus.
#
# Contrats HORS-LIGNE : aide générale, aide de CHAQUE sous-commande (un test parcourt les 35),
# validation d'arguments, refus métier, lectures locales sur base vide. La couverture des chemins
# RÉSEAU (import, dépôt, ancrage) reste cadrée en suite (#1592).
#
# `--help` est activé sur chaque sous-commande (Cli.executer, #1592) : `reactiver --help` décrit la
# commande au lieu d'échouer « Unknown option ».
#
# Lancer :  ./mvnw -DskipTests package   # produit target/vigiechiro-*-shaded.jar
#           bats src/test/bats
# (ou définir VIGIECHIRO_JAR=/chemin/vers/le-fat-jar.jar)

setup() {
  JAR="${VIGIECHIRO_JAR:-$(ls "${BATS_TEST_DIRNAME}"/../../../target/vigiechiro-*-shaded.jar 2>/dev/null | head -1)}"
  if [ -z "${JAR}" ] || [ ! -f "${JAR}" ]; then
    skip "fat-jar introuvable : lancer './mvnw -DskipTests package' d'abord (ou définir VIGIECHIRO_JAR)"
  fi
}

# Un vrai processus : workspace jetable (base SQLite créée sous le tmpdir du test), aucun jeton VigieChiro
# (on éprouve les contrats hors-ligne). Même point d'entrée que le smoke-test CI (fr.univ_amu.iut.cli.Cli).
cli() {
  java --enable-native-access=ALL-UNNAMED -Dvigiechiro.workspace="${BATS_TEST_TMPDIR}" \
    -cp "${JAR}" fr.univ_amu.iut.cli.Cli "$@"
}

@test "aide générale : liste les commandes du chantier, exit 0" {
  run cli --help
  [ "${status}" -eq 0 ]
  [[ "${output}" == *"Usage: vigiechiro"* ]]
  [[ "${output}" == *"reconstruire-passage"* ]]
  [[ "${output}" == *"reactiver"* ]]
}

@test "reactiver --help : décrit la commande et ses options, exit 0 (#1592)" {
  run cli reactiver --help
  [ "${status}" -eq 0 ]
  [[ "${output}" == *"Usage: vigiechiro reactiver"* ]]
  [[ "${output}" == *"--passage"* ]]
  [[ "${output}" == *"--source"* ]]
}

@test "reconstruire-passage --help : décrit la commande, exit 0 (#1592)" {
  run cli reconstruire-passage --help
  [ "${status}" -eq 0 ]
  [[ "${output}" == *"Usage: vigiechiro reconstruire-passage"* ]]
  [[ "${output}" == *"--participation"* ]]
}

@test "TOUTES les sous-commandes répondent à --help (exit 0 + usage) (#1592)" {
  # Le correctif --help (Cli.executer) vaut pour toutes les commandes d'un coup : on le prouve sur
  # CHACUNE. On extrait la liste depuis l'aide générale (1re colonne des lignes de la section Commands,
  # les lignes de description étant bien plus indentées), puis on interroge l'aide de chaque commande.
  run cli --help
  [ "${status}" -eq 0 ]
  local commandes
  commandes=$(printf '%s\n' "${output}" | awk '/^Commands:/{f=1} f && /^  [a-z]/{print $1}')
  [ -n "${commandes}" ]

  local n=0
  for commande in ${commandes}; do
    run cli "${commande}" --help
    [ "${status}" -eq 0 ] || {
      echo "« ${commande} --help » a échoué (exit ${status})"
      return 1
    }
    [[ "${output}" == *"Usage: vigiechiro ${commande}"* ]] || {
      echo "« ${commande} --help » n'affiche pas son usage"
      return 1
    }
    n=$((n + 1))
  done
  echo "sous-commandes vérifiées : ${n}"
  [ "${n}" -ge 20 ] # garde-fou : l'extraction a bien trouvé les commandes (35 attendues)
}

@test "reconstruire-passage hors connexion : refus métier expliqué, exit 1" {
  # Sans jeton, lister/reconstruire exige la plateforme : refus « non connecté » (pas un plantage muet).
  run cli reconstruire-passage
  [ "${status}" -eq 1 ]
  [[ "${output}" == *"connect"* ]]
}

@test "reconstruire-passage --participation sans valeur : erreur d'usage picocli, exit 2" {
  run cli reconstruire-passage --participation
  [ "${status}" -eq 2 ]
}

@test "reactiver sans options requises : erreur d'usage picocli, exit 2" {
  run cli reactiver
  [ "${status}" -eq 2 ]
  [[ "${output}" == *"passage"* ]]
}

@test "reactiver --passage 1 --source <dossier inexistant> : refus métier, exit 1" {
  run cli reactiver --passage 1 --source "${BATS_TEST_TMPDIR}/pas-la"
  [ "${status}" -eq 1 ]
  [[ "${output}" == *"Dossier introuvable"* ]]
}

# --- Lectures locales (base jetable vide) : la CLI migre la base puis lit, sans réseau ------------

@test "lister-sites : base vide, exit 0" {
  run cli lister-sites
  [ "${status}" -eq 0 ]
  [[ "${output}" == *"Aucun site"* ]]
}

@test "lister-passages : base vide, exit 0" {
  run cli lister-passages
  [ "${status}" -eq 0 ]
  [[ "${output}" == *"Aucun passage"* ]]
}

@test "statut-passage --passage <inconnu> : refus métier, exit 1" {
  run cli statut-passage --passage 999999
  [ "${status}" -eq 1 ]
  [[ "${output}" == *"introuvable"* ]]
}

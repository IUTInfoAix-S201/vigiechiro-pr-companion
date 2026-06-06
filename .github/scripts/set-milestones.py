#!/usr/bin/env python3
"""Crée les milestones MoSCoW (+ Passe finale) avec échéances et y rattache les issues.

Pour un repo (ou tous les forks d'équipe de l'org Classroom) :
  - crée 4 milestones (idempotent par titre) avec leur `due_on` :
    MUST, SHOULD, COULD, Passe finale ; met à jour l'échéance si elle diffère ;
  - rattache chaque issue à son milestone selon son tag `[feature]` (ou son titre
    pour les 2 issues sans tag), d'après la table de priorité du README.

Dry-run par défaut ; --apply pour écrire.

Usages :
  python3 set-milestones.py --apply                         # tous les forks d'équipe
  python3 set-milestones.py --apply --repo <owner/name>     # un repo
  python3 set-milestones.py --apply --repo "$GITHUB_REPOSITORY"   # en CI (bootstrap)
"""
import argparse
import json
import re
import subprocess
import sys
from datetime import datetime, timezone

ORG = "IUTInfoAix-S201-2026"
FORK_RE = re.compile(r"^vigiechiro-pr-companion-.+$")
TAG_RE = re.compile(r"\[([^\]]+)\]")

# Milestones (titre, description, due_on ISO8601 avec décalage Paris).
# NB : GitHub affiche les échéances à la granularité du JOUR (l'heure peut ne pas
# être visible) ; les heures servent surtout à ordonner (tri par échéance).
MILESTONES = [
    ("MUST", "Priorité MoSCoW : incontournable — cœur du livrable (MVP). "
             "Échéance : 18/06/2026 à 08h15.",
     "2026-06-18T08:15:00+02:00"),
    ("Passe finale", "Passes transverses de fin de projet : E2E, qualité, app, "
                     "accessibilité, documentation, performances. "
                     "Échéance : 18/06/2026 à 10h15.",
     "2026-06-18T10:15:00+02:00"),
    ("SHOULD", "Priorité MoSCoW : important mais non bloquant. Échéance : 18/07/2026.",
     "2026-07-18T23:59:00+02:00"),
    ("COULD", "Priorité MoSCoW : optionnel / bonus (dont les extensions). "
              "Échéance : 31/12/2026.",
     "2026-12-31T23:59:00+01:00"),
]

TAG_MS = {
    "passage": "MUST", "importation": "MUST", "qualification": "MUST", "lot": "MUST",
    "validation": "SHOULD", "multisite": "SHOULD", "diagnostic": "SHOULD",
    "bibliotheque": "COULD", "extension": "COULD",
    "passe finale": "Passe finale", "vérification": "Passe finale",
}
TITLE_MS = [("Bienvenue", "MUST"), ("Pour aller plus loin", "COULD")]


def gh(args, check=True, stdin=None):
    r = subprocess.run(["gh", *args], input=stdin, capture_output=True, text=True)
    if check and r.returncode != 0:
        raise RuntimeError(f"gh {' '.join(args)} -> {r.returncode}\n{r.stderr.strip()}")
    return r


def gh_json(args):
    return json.loads(gh(args).stdout or "[]")


def slug(repo):
    return repo if "/" in repo else f"{ORG}/{repo}"


def list_team_repos():
    repos = gh_json(["repo", "list", ORG, "--limit", "200", "--json", "name"])
    return [f"{ORG}/{r['name']}" for r in sorted(repos, key=lambda x: x["name"])
            if FORK_RE.match(r["name"])]


def list_issues(s):
    data = gh_json(["api", f"repos/{s}/issues?state=all&per_page=100"])
    if len(data) >= 100:
        print(f"  ⚠ {s}: >=100 entrées /issues, pagination non gérée", file=sys.stderr)
    return [i for i in data if "pull_request" not in i]


def target_milestone(title):
    m = TAG_RE.search(title)
    if m and m.group(1) in TAG_MS:
        return TAG_MS[m.group(1)]
    for sub, ms in TITLE_MS:
        if sub in title:
            return ms
    return None


def _utc_date(iso):
    if not iso:
        return None
    dt = datetime.fromisoformat(iso.replace("Z", "+00:00"))
    return dt.astimezone(timezone.utc).date()


def ensure_milestones(s, apply):
    """Crée/maj les milestones avec leur échéance. Renvoie {titre: numéro}."""
    existing = {m["title"]: m
                for m in gh_json(["api", f"repos/{s}/milestones?state=all&per_page=100"])}
    result = {}
    for title, desc, due in MILESTONES:
        cur = existing.get(title)
        if cur is None:
            if apply:
                r = gh(["api", "--method", "POST", f"repos/{s}/milestones",
                        "-f", f"title={title}", "-f", f"description={desc}", "-f", f"due_on={due}"])
                result[title] = json.loads(r.stdout)["number"]
                print(f"     + créé : {title} (échéance {due[:16]})")
            else:
                result[title] = None
                print(f"     ~ (dry) créerait : {title} (échéance {due[:16]})")
        else:
            result[title] = cur["number"]
            need_due = _utc_date(cur.get("due_on")) != _utc_date(due)
            need_desc = (cur.get("description") or "") != desc
            if need_due or need_desc:
                if apply:
                    gh(["api", "--method", "PATCH", f"repos/{s}/milestones/{cur['number']}",
                        "-f", f"due_on={due}", "-f", f"description={desc}"])
                    print(f"     ✎ maj : {title}"
                          + (" [échéance]" if need_due else "") + (" [desc]" if need_desc else ""))
                else:
                    print(f"     ~ (dry) majʼerait : {title}")
    return result


def assign(s, number, ms_number):
    gh(["api", "--method", "PATCH", f"repos/{s}/issues/{number}", "-F", f"milestone={ms_number}"])


def process_repo(s, apply):
    issues = list_issues(s)
    ms_num = ensure_milestones(s, apply)
    counts, n_assign, n_skip, n_none = {}, 0, 0, 0
    for i in issues:
        target = target_milestone(i["title"])
        if not target:
            n_none += 1
            continue
        counts[target] = counts.get(target, 0) + 1
        current = i["milestone"]["title"] if i.get("milestone") else None
        if current == target:
            n_skip += 1
            continue
        if apply and ms_num.get(target):
            assign(s, i["number"], ms_num[target])
        n_assign += 1
    repartition = ", ".join(f"{k}={counts[k]}" for k, _, _ in MILESTONES if k in counts)
    print(f"     répartition : {repartition}"
          + (f" | hors-milestone : {n_none}" if n_none else "")
          + f"\n     {'assignées' if apply else '(dry) à assigner'} : {n_assign}"
          + (f" | déjà OK : {n_skip}" if n_skip else ""))
    return n_assign


def main():
    ap = argparse.ArgumentParser(description=__doc__,
                                 formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--apply", action="store_true", help="écrit réellement (sinon dry-run)")
    ap.add_argument("--repo", action="append", help="repo (nom court ou owner/name), répétable")
    args = ap.parse_args()

    slugs = [slug(r) for r in args.repo] if args.repo else list_team_repos()
    print(f"[{'APPLY' if args.apply else 'DRY-RUN'}] {len(slugs)} repo(s)")
    total = 0
    for s in slugs:
        print(f"\n### {s}")
        try:
            total += process_repo(s, args.apply)
        except Exception as e:
            print(f"  ✖ {s}: {e}", file=sys.stderr)
    print(f"\n=== Total : {total} affectation(s) "
          f"({'appliquées' if args.apply else 'simulées'}) ===")


if __name__ == "__main__":
    main()

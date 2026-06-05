#!/usr/bin/env python3
"""Met a jour le board de gestion de projet (GitHub Project v2 de l'equipe).

Comble les transitions que les workflows integres du Project v2 ne couvrent pas :
  - PR ouverte (non-draft) / prete pour revue / rouverte -> issue(s) liee(s) en "In review"
  - PR repassee en brouillon                             -> issue(s) liee(s) en "In progress"
  - nouvelle issue creee                                 -> ajoutee au board (-> Backlog)

Les statuts "In progress" (PR liee) et "Done" (PR mergee / issue fermee) sont deja geres
par les workflows integres du projet : on ne les redouble pas ici.

Entree : variables d'environnement positionnees par le workflow
  GH_TOKEN      PAT avec "Projects: Read and write" sur l'org (+ Issues/PR en lecture).
                Si absent -> sortie propre sans rien faire (le secret n'est pas encore pose).
  EVENT_NAME    "pull_request" ou "issues".
  ACTION        github.event.action (opened, ready_for_review, converted_to_draft, reopened...).
  REPO          "owner/name".
  PR_NUMBER / PR_DRAFT / ISSUE_NUMBER  selon l'evenement.
"""

import json
import os
import subprocess
import sys


def gql(query, variables):
    proc = subprocess.run(
        ["gh", "api", "graphql", "--input", "-"],
        input=json.dumps({"query": query, "variables": variables}),
        capture_output=True,
        text=True,
    )
    try:
        data = json.loads(proc.stdout)
    except Exception:
        print("Reponse GraphQL illisible:", proc.stdout[:300], proc.stderr[:300])
        sys.exit(1)
    if "errors" in data:
        print("Erreur GraphQL:", json.dumps(data["errors"])[:400])
        sys.exit(1)
    return data["data"]


def main():
    if not os.environ.get("GH_TOKEN", "").strip():
        print("Secret PROJECTS_AUTOMATION_TOKEN absent -> board non mis a jour "
              "(configurez le secret d'organisation pour activer l'automatisation).")
        return 0

    owner, repo = os.environ["REPO"].split("/")
    event = os.environ["EVENT_NAME"]
    action = os.environ.get("ACTION", "")

    # Projet "Gestion de projet ..." lie a ce depot (un seul attendu).
    projs = gql(
        "query($o:String!,$r:String!){repository(owner:$o,name:$r)"
        "{projectsV2(first:10){nodes{number title id}}}}",
        {"o": owner, "r": repo},
    )["repository"]["projectsV2"]["nodes"]
    cand = [p for p in projs if p["title"].startswith("Gestion de projet")] or projs
    if not cand:
        print("Aucun Project v2 lie a ce depot -> rien a faire.")
        return 0
    proj = cand[0]
    pid, pnum = proj["id"], proj["number"]

    # Champ Status + ses options.
    field = gql(
        "query($id:ID!){node(id:$id){... on ProjectV2{field(name:\"Status\")"
        "{... on ProjectV2SingleSelectField{id options{id name}}}}}}",
        {"id": pid},
    )["node"]["field"]
    fid = field["id"]
    opt = {o["name"]: o["id"] for o in field["options"]}

    def set_status(item_id, name):
        if name not in opt:
            print("Option Status absente du projet:", name)
            return
        gql(
            "mutation($p:ID!,$i:ID!,$f:ID!,$o:String!){updateProjectV2ItemFieldValue("
            "input:{projectId:$p,itemId:$i,fieldId:$f,value:{singleSelectOptionId:$o}})"
            "{projectV2Item{id}}}",
            {"p": pid, "i": item_id, "f": fid, "o": opt[name]},
        )
        print("  item", item_id, "->", name)

    if event == "issues":
        num = int(os.environ["ISSUE_NUMBER"])
        iss = gql(
            "query($o:String!,$r:String!,$n:Int!){repository(owner:$o,name:$r)"
            "{issue(number:$n){id projectItems(first:10){nodes{project{number}}}}}}",
            {"o": owner, "r": repo, "n": num},
        )["repository"]["issue"]
        if any(it["project"]["number"] == pnum for it in iss["projectItems"]["nodes"]):
            print("Issue deja sur le board.")
            return 0
        gql(
            "mutation($p:ID!,$c:ID!){addProjectV2ItemById(input:{projectId:$p,contentId:$c})"
            "{item{id}}}",
            {"p": pid, "c": iss["id"]},
        )
        print("Nouvelle issue ajoutee au board (-> Backlog).")
        return 0

    # event == "pull_request"
    draft = os.environ.get("PR_DRAFT", "false") == "true"
    if action == "converted_to_draft":
        target = "In progress"
    elif action in ("opened", "ready_for_review", "reopened") and not draft:
        target = "In review"
    else:
        print("Action/etat PR sans transition dediee:", action, "draft=", draft)
        return 0

    num = int(os.environ["PR_NUMBER"])
    pr = gql(
        "query($o:String!,$r:String!,$n:Int!){repository(owner:$o,name:$r)"
        "{pullRequest(number:$n){closingIssuesReferences(first:20){nodes{number "
        "projectItems(first:10){nodes{id project{number}}}}}}}}",
        {"o": owner, "r": repo, "n": num},
    )["repository"]["pullRequest"]
    issues = pr["closingIssuesReferences"]["nodes"]
    if not issues:
        print("La PR ne ferme aucune issue (Closes #N) -> rien a deplacer.")
        return 0
    for iss in issues:
        items = [it for it in iss["projectItems"]["nodes"] if it["project"]["number"] == pnum]
        if items:
            set_status(items[0]["id"], target)
        else:
            print("Issue #%d absente du board -> ignoree." % iss["number"])
    return 0


if __name__ == "__main__":
    sys.exit(main())

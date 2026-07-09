package fr.univ_amu.iut.commun.view;

import com.google.inject.Singleton;

/// Défaut inerte de [OuvrirConnexion] : utilisé quand la feature `connexion` n'est pas chargée
/// (outils de capture, tests du socle). L'entrée de menu reste présente mais sans effet, et affiche un
/// libellé neutre. En application complète, `ConnexionModule` fournit l'implémentation réelle.
@Singleton
public final class OuvrirConnexionAucun implements OuvrirConnexion {

    @Override
    public void ouvrir() {
        // Feature connexion absente : aucune modale à ouvrir.
    }

    @Override
    public String libelleMenu() {
        return "Connexion VigieChiro…";
    }
}

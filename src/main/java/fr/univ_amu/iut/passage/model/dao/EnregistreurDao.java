package fr.univ_amu.iut.passage.model.dao;

import fr.univ_amu.iut.commun.persistence.DaoGenerique;
import fr.univ_amu.iut.commun.persistence.RowMapper;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import fr.univ_amu.iut.passage.model.Enregistreur;

/// DAO de l'entité [Enregistreur] (table `recorder`).
///
/// Comme `recorder` a une **clé naturelle** (`serial_number`, en `TEXT`) lue depuis le journal du
/// capteur, [#insert(Enregistreur)] fait un **upsert** (`INSERT … ON CONFLICT … DO UPDATE`) :
/// rencontrer deux fois le même enregistreur (sur deux passages successifs) rafraîchit ses
/// métadonnées au lieu de violer la contrainte de clé primaire. C'est le patron à recopier pour
/// les entités à clé naturelle alimentées par import.
public class EnregistreurDao extends DaoGenerique<Enregistreur, String> {

    private static final RowMapper<Enregistreur> MAPPER = rs ->
            new Enregistreur(rs.getString("serial_number"), rs.getString("model_version"), rs.getString("comment"));

    public EnregistreurDao(SourceDeDonnees source) {
        super(source);
    }

    @Override
    protected String table() {
        return "recorder";
    }

    @Override
    protected String colonneCle() {
        return "serial_number";
    }

    @Override
    protected RowMapper<Enregistreur> mapper() {
        return MAPPER;
    }

    /// Insère ou met à jour l'enregistreur (upsert sur la clé naturelle `serial_number`). Renvoie
    /// l'entité telle quelle (aucune clé générée pour une clé naturelle).
    @Override
    public Enregistreur insert(Enregistreur enregistreur) {
        executerMaj(
                "INSERT INTO recorder (serial_number, model_version, comment) VALUES (?, ?, ?)"
                        + " ON CONFLICT(serial_number) DO UPDATE SET"
                        + " model_version = excluded.model_version, comment = excluded.comment",
                enregistreur.numeroSerie(),
                enregistreur.versionModele(),
                enregistreur.commentaire());
        return enregistreur;
    }

    @Override
    public void update(Enregistreur enregistreur) {
        executerMaj(
                "UPDATE recorder SET model_version = ?, comment = ? WHERE serial_number = ?",
                enregistreur.versionModele(),
                enregistreur.commentaire(),
                enregistreur.numeroSerie());
    }
}

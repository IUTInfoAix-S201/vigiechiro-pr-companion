package fr.univ_amu.iut.commun.model.dao;

import fr.univ_amu.iut.commun.model.LienVigieChiro;
import fr.univ_amu.iut.commun.persistence.DaoGenerique;
import fr.univ_amu.iut.commun.persistence.DataAccessException;
import fr.univ_amu.iut.commun.persistence.RowMapper;
import fr.univ_amu.iut.commun.persistence.SourceDeDonnees;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/// DAO des correspondances locale ↔ VigieChiro (table `vigiechiro_link`, cf. [LienVigieChiro], #728).
///
/// Table à **clé primaire composite** (`entite`, `ref_locale`) : les lectures/écritures nominales
/// sont donc toujours **portées par l'entité** ([#objectidPour], [#tous], [#remplacer]), et non par
/// la seule clé locale. Les `findById` / `delete` hérités de [DaoGenerique] (mono-colonne) restent
/// disponibles mais ne distinguent pas l'entité : ne pas les utiliser pour ce DAO.
///
/// Deux chemins d'écriture, tous deux **idempotents** :
/// - [#upsert(LienVigieChiro)] : pose (ou remplace) une correspondance unitaire (`ON CONFLICT`) ;
/// - [#remplacer(String, Map)] : **resynchronisation** transactionnelle d'une entité entière (purge
///   puis réinsertion), qui supprime au passage les correspondances devenues obsolètes.
public class LienVigieChiroDao extends DaoGenerique<LienVigieChiro, String> {

    private static final RowMapper<LienVigieChiro> MAPPER =
            rs -> new LienVigieChiro(rs.getString("entite"), rs.getString("ref_locale"), rs.getString("objectid"));

    public LienVigieChiroDao(SourceDeDonnees source) {
        super(source);
    }

    @Override
    protected String table() {
        return "vigiechiro_link";
    }

    @Override
    protected String colonneCle() {
        return "ref_locale";
    }

    @Override
    protected RowMapper<LienVigieChiro> mapper() {
        return MAPPER;
    }

    /// `objectid` VigieChiro correspondant à `refLocale` pour l'`entite` donnée, ou vide si aucune
    /// correspondance n'a encore été rapprochée.
    public Optional<String> objectidPour(String entite, String refLocale) {
        return queryUnique(
                        "SELECT * FROM vigiechiro_link WHERE entite = ? AND ref_locale = ?", MAPPER, entite, refLocale)
                .map(LienVigieChiro::objectid);
    }

    /// Toutes les correspondances d'une entité, sous forme de table `ref_locale -> objectid` (ordre
    /// d'insertion préservé). Utile aux features consommatrices pour résoudre en masse leurs clés.
    public Map<String, String> tous(String entite) {
        Map<String, String> liens = new LinkedHashMap<>();
        for (LienVigieChiro lien :
                query("SELECT * FROM vigiechiro_link WHERE entite = ? ORDER BY ref_locale", MAPPER, entite)) {
            liens.put(lien.refLocale(), lien.objectid());
        }
        return liens;
    }

    /// Nombre de correspondances enregistrées pour une entité (indicateur de synchro).
    public long compter(String entite) {
        return query("SELECT * FROM vigiechiro_link WHERE entite = ?", MAPPER, entite)
                .size();
    }

    /// **Upsert** unitaire : pose la correspondance, ou remplace l'`objectid` si `(entite, ref_locale)`
    /// existe déjà (SQLite `ON CONFLICT`). Idempotent.
    public void upsert(LienVigieChiro lien) {
        executerMaj(
                "INSERT INTO vigiechiro_link (entite, ref_locale, objectid) VALUES (?, ?, ?) "
                        + "ON CONFLICT(entite, ref_locale) DO UPDATE SET objectid = excluded.objectid",
                lien.entite(),
                lien.refLocale(),
                lien.objectid());
    }

    /// **Resynchronisation** transactionnelle d'une entité : purge toutes ses correspondances puis
    /// réinsère celles de `liens` (`ref_locale -> objectid`), en une seule transaction (purge + lot
    /// atomiques). Reflète l'état courant de la plateforme et supprime les correspondances obsolètes.
    /// Idempotent : rejouer avec la même table produit le même résultat.
    public void remplacer(String entite, Map<String, String> liens) {
        try (Connection cx = source.getConnection()) {
            cx.setAutoCommit(false);
            try {
                try (PreparedStatement purge = cx.prepareStatement("DELETE FROM vigiechiro_link WHERE entite = ?")) {
                    purge.setString(1, entite);
                    purge.executeUpdate();
                }
                try (PreparedStatement insert = cx.prepareStatement(
                        "INSERT INTO vigiechiro_link (entite, ref_locale, objectid) VALUES (?, ?, ?)")) {
                    for (Map.Entry<String, String> entree : liens.entrySet()) {
                        insert.setString(1, entite);
                        insert.setString(2, entree.getKey());
                        insert.setString(3, entree.getValue());
                        insert.addBatch();
                    }
                    insert.executeBatch();
                }
                cx.commit();
            } catch (SQLException echec) {
                cx.rollback();
                throw echec;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Échec de la resynchronisation des correspondances : " + entite, e);
        }
    }

    @Override
    public LienVigieChiro insert(LienVigieChiro lien) {
        executerMaj(
                "INSERT INTO vigiechiro_link (entite, ref_locale, objectid) VALUES (?, ?, ?)",
                lien.entite(),
                lien.refLocale(),
                lien.objectid());
        return lien;
    }

    @Override
    public void update(LienVigieChiro lien) {
        executerMaj(
                "UPDATE vigiechiro_link SET objectid = ? WHERE entite = ? AND ref_locale = ?",
                lien.objectid(),
                lien.entite(),
                lien.refLocale());
    }
}

package fr.univ_amu.iut.validation.model;

import fr.univ_amu.iut.commun.api.ClientVigieChiro;
import fr.univ_amu.iut.commun.api.RapprochementVigieChiro;
import fr.univ_amu.iut.commun.api.TaxonVigieChiro;
import fr.univ_amu.iut.commun.model.LienVigieChiro;
import fr.univ_amu.iut.commun.model.dao.LienVigieChiroDao;
import fr.univ_amu.iut.validation.model.dao.TaxonDao;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/// Rapproche le référentiel **taxons** local avec VigieChiro (#728, axe 1) : appelle
/// `GET /taxons/liste` et relie chaque taxon local à son `objectid` par égalité `code == libelle_court`
/// (nos taxons portent le code Tadarida, qui est le `libelle_court` de la plateforme).
///
/// Contribué au `Multibinder<RapprochementVigieChiro>` par `ValidationModule` ; invoqué à la connexion
/// par `connexion`. Ne dépend que du [TaxonDao] de sa feature et du [LienVigieChiroDao] du socle : le
/// client est reçu **en argument**, jamais injecté (les injecteurs autonomes restent valides).
public class RapprochementTaxons implements RapprochementVigieChiro {

    private static final Logger LOG = Logger.getLogger(RapprochementTaxons.class.getName());

    private final TaxonDao taxonDao;
    private final LienVigieChiroDao liens;

    public RapprochementTaxons(TaxonDao taxonDao, LienVigieChiroDao liens) {
        this.taxonDao = Objects.requireNonNull(taxonDao, "taxonDao");
        this.liens = Objects.requireNonNull(liens, "liens");
    }

    @Override
    public void synchroniser(ClientVigieChiro client) {
        try {
            Set<String> codesLocaux =
                    taxonDao.findAll().stream().map(Taxon::code).collect(Collectors.toSet());
            Map<String, String> correspondances = new LinkedHashMap<>();
            for (TaxonVigieChiro taxon : client.taxons()) {
                if (codesLocaux.contains(taxon.libelleCourt())) {
                    correspondances.put(taxon.libelleCourt(), taxon.id());
                }
            }
            // Liste vide = non connecté / API indisponible : on ne purge pas les correspondances déjà
            // acquises (un incident réseau transitoire ne doit pas effacer un mapping valide).
            if (!correspondances.isEmpty()) {
                liens.remplacer(LienVigieChiro.ENTITE_TAXON, correspondances);
            }
        } catch (RuntimeException echec) {
            LOG.log(Level.FINE, echec, () -> "Rapprochement des taxons VigieChiro ignoré (best-effort)");
        }
    }
}

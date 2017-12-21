package gr.cite.geoanalytics.dataaccess.entities.layer.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerImport;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;

@Repository
public class LayerImportDaoImpl extends JpaDao<LayerImport, UUID> implements LayerImportDao {

	public static Logger logger = LoggerFactory.getLogger(LayerImportDaoImpl.class);

	@Override
	public List<LayerImport> findLayerImportsOfPrincipal(Principal creator) {
		logger.debug("Retrieving layer imports of principal : " + creator);

		List<LayerImport> result = null;

		try {
			TypedQuery<LayerImport> query = entityManager.createQuery("from LayerImport li where li.creator = :creator", LayerImport.class);
			query.setParameter("creator", creator);
			result = query.getResultList();
		} catch (Exception e) {
			logger.error("Could not retrieve layer imports of principal: " + creator, e);
		}

		return result == null ? new ArrayList<>() : result;
	}

	@Override
	public LayerImport loadDetails(LayerImport t) {
		return null;
	}
}

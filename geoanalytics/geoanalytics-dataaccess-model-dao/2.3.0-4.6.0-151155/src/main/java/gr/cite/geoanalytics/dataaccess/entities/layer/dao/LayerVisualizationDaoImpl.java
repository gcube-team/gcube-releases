package gr.cite.geoanalytics.dataaccess.entities.layer.dao;

import java.util.UUID;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerVisualization;

/**
 * @author vfloros
 *
 */
@Repository
public class LayerVisualizationDaoImpl extends JpaDao<LayerVisualization, UUID> implements LayerVisualizationDao {

	public static Logger log = LoggerFactory.getLogger(LayerVisualizationDaoImpl.class);

	@Override
	public LayerVisualization loadDetails(LayerVisualization t) {
		return null;
	}

	@Override
	public LayerVisualization getLayerVisualizationByLayerIDAndTenant(UUID layerID, UUID tenantID) {
		log.debug("Searching for layerVisualization by layer id: " + layerID + " and tenant id: " + tenantID);
		StringBuilder sb = new StringBuilder("");
		sb.append("FROM LayerVisualization WHERE layer.id = :layerID AND tenant.id = :tenantID");
		
		LayerVisualization result = null;

		TypedQuery<LayerVisualization> query = entityManager.createQuery(sb.toString(), LayerVisualization.class);
		query.setParameter("layerID", layerID);
		query.setParameter("tenantID", tenantID);
		
		try{
			result = query.getSingleResult();
		} catch(javax.persistence.NoResultException e) {}
		
		return result;
	}
}

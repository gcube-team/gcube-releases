package gr.cite.geoanalytics.dataaccess.entities.coverage.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.coverage.Coverage;

public class CoverageDaoImpl extends JpaDao<Coverage, UUID> implements CoverageDao {

	public static Logger logger = LoggerFactory.getLogger(CoverageDaoImpl.class);

	@Override
	public Coverage findCoverageByLayer(UUID layerID) {
		logger.debug("Retrieving Coverage of layer with ID: " + layerID);

		Coverage result = null;

		try {
			TypedQuery<Coverage> query = entityManager.createQuery("from Coverage cvrg where cvrg.layerID = :layerID", Coverage.class);
			query.setParameter("layerID", layerID);
			result = query.getSingleResult();
		} catch (Exception e) {
			logger.error("Could not retrieve Coverage of layer with ID: " + layerID, e);
		}

		return result;
	}

	@Override
	public List<String> getAllLayerIDs() {
		logger.debug("Finding all layerIDs (distinct) within Coverage table ");
		
		List<String> result = null;
		
		TypedQuery<UUID> query = entityManager.createQuery("select distinct(cvrg.layerID) from Coverage cvrg", UUID.class);
		
		result = query.getResultList().parallelStream().map(uuid -> uuid.toString()).collect(Collectors.toList());
		
		logger.debug("Found " + (result != null ? result.size() : 0) + " results");
		
		return result.size() > 0 ? result : new ArrayList<>();
	}
	

	@Override
	public Coverage loadDetails(Coverage t) {
		return null;
	}
}

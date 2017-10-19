package gr.cite.geoanalytics.dataaccess.entities.layer.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;

public class LayerDaoImpl extends JpaDao<Layer, UUID> implements LayerDao {

	public static Logger log = LoggerFactory.getLogger(LayerDaoImpl.class);

	@Override
	public Layer loadDetails(Layer t) {
		// TODO Auto-generated method stub
		return t;
	}
	
	
	@Override
	public Layer getLayerById(UUID id) {
		Layer layer = null;
		log.debug("Searching for layer by id: " + id);
		TypedQuery<Layer> query = entityManager
				.createQuery("from Layer where id = :id", Layer.class);
		query.setParameter("id", id);
		layer = query.getSingleResult();
		return layer;
	}
	
	@Override
	public List<Layer> findLayersByName(String layerName){
		log.debug("Retrieving layer by name: "+ layerName);
		TypedQuery<Layer> query = entityManager
				.createQuery("from Layer where name = :layerName", Layer.class);
		query.setParameter("layerName", layerName);
		return query.getResultList();
	}


	@Override
	public List<Layer> findLayersByTenant(Tenant tenant) throws Exception {
		log.debug("Retrieving layer by tenant: "+ tenant.getName());

		List<Layer> result = new ArrayList<Layer>();
		
		try {
			String queryString = "SELECT lt.layer FROM LayerTenant lt WHERE lt.tenant = :tenant";

			TypedQuery<Layer> typedQuery = entityManager.createQuery(queryString, Layer.class);
			typedQuery.setParameter("tenant", tenant);

			result = typedQuery.getResultList();
		} catch(Exception e){
			throw new Exception("Could not retrieve layers of tenant " + tenant.getId(), e);
		}
		
		return result;
	}

	@Override
	public List<Layer> findLayersNotLinkedToSomeTenant() throws Exception {

		List<Layer> result = new ArrayList<Layer>();
		
		try {
			StringBuilder queryB = new StringBuilder("");
			queryB.append("SELECT l FROM Layer l LEFT JOIN l.layerTenants");
			queryB.append(" WHERE l.id NOT IN (SELECT lt.layer.id FROM LayerTenant lt)");
			
			String queryString = queryB.toString();

			TypedQuery<Layer> typedQuery = entityManager.createQuery(queryString, Layer.class);

			result = typedQuery.getResultList();
		} catch(Exception e){
			throw new Exception("There are no layers connected to no tennants", e);
		}
		
		return result;
	}
	
	@Override
	public Layer findTemplateLayerByGeocodeSystem(GeocodeSystem geocodeSystem) throws Exception{
		log.debug("Retrieving template layer of geocode system " + geocodeSystem.getName());
		
		TypedQuery<Layer> query = entityManager.createQuery("from Layer where isTemplate = 1 and geocodeSystem = :geocodeSystem", Layer.class);
		query.setParameter("geocodeSystem", geocodeSystem);
		
		List<Layer> results = query.getResultList();
		
		if(results == null || results.isEmpty()){
			throw new Exception("No template layer for geocode system " + geocodeSystem.getName());
		}
		
		if(results.size() > 1){
			throw new Exception("More than 1 template layers for geocode system " + geocodeSystem.getName());		
		}
		
		return results.get(0);	
	}
	
	@Override
	public List<Layer> getTemplateLayers() {
		log.debug("Retrieving template layers");
		TypedQuery<Layer> query = entityManager.createQuery("from Layer where isTemplate = 1", Layer.class);
		return query.getResultList();		
	}

	@Override
	public List<Layer> getLayersWithStyle(String styleName) throws Exception {
		log.debug("Getting layers with style: "+styleName);
		
		TypedQuery<Layer> query = entityManager.createQuery("from Layer where style = :style", Layer.class);
		query.setParameter("style", styleName);
		
		List<Layer> results = query.getResultList();
		
		if(results == null || results.isEmpty()){
			throw new Exception("No layer with style: " + styleName);
		}
		
		return results;
		
	}
	
	@Override
	public List<Layer> getLayersOfGeocodeSystem(GeocodeSystem geocodeSystem){
		log.debug("Retrieving template layer of geocode system " + geocodeSystem.getName());
		
		TypedQuery<Layer> query = entityManager.createQuery("from Layer where geocodeSystem = :geocodeSystem", Layer.class);
		query.setParameter("geocodeSystem", geocodeSystem);
		
		List<Layer> results = query.getResultList();
		
		if(results == null || results.isEmpty()){
			log.warn("No layers of geocode system " + geocodeSystem.getName() + " were found");
		}
		
		return results;		
	}

}
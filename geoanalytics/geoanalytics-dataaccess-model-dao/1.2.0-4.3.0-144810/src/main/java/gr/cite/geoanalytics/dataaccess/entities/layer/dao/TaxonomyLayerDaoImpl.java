package gr.cite.geoanalytics.dataaccess.entities.layer.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.geocode.TaxonomyLayer;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;

public class TaxonomyLayerDaoImpl extends JpaDao<TaxonomyLayer, UUID> implements TaxonomyLayerDao {

	public static Logger log = LoggerFactory.getLogger(TaxonomyLayerDaoImpl.class);

	@Override
	public TaxonomyLayer loadDetails(TaxonomyLayer t) {
		// TODO Auto-generated method stub
		return t;
	}
	
	
	@Override
	public List<Layer> findLayerByNameAndGeocodeSystem(String layerName, String taxonomyName) {
		List<Layer> layers = null;
		log.debug("Searching for layer by name: " + layerName +" and taxonomy: "+taxonomyName);
		TypedQuery<Layer> query = entityManager
				.createQuery("from TaxonomyLayer tl where tl.taxonomy.name = :taxonomyName and tl.layer.name = :layerName", Layer.class);
		query.setParameter("layerName", layerName);
		query.setParameter("taxonomyName", taxonomyName);
		layers = query.getResultList();
		return layers;
	}

	

	@Override
	public List<Layer> findLayerByNameAndGeocodeSystems(String layerName, List<GeocodeSystem> taxonomies) {
		log.debug("Fetching layer by name: "+layerName+" and taxonomies :"+ taxonomies.toString());
		List<Layer> result = null;
		
		TypedQuery<Layer> query = entityManager.createQuery(
				"from TaxonomyLayer tl where tl.layer.name = :layerName and tl.taxonomy in (:taxonomies)", Layer.class);
		query.setParameter("layerName", layerName);
		query.setParameter("taxonomies", taxonomies);
		
		result = query.getResultList();
		
		log.debug("Taxonomies that we searched in:");
		
		//System.out.println("Taxonomies that we searched in:");
		//taxonomies.forEach(taxonomy -> System.out.println("Taxonomy: " + taxonomy.getName()));
		
		log.debug((result != null ? result.size() : 0) + " results");
		
		if(log.isDebugEnabled() && result != null) {
			for (Layer layer : (List<Layer>) result)
				log.debug("Layer (" + layer.getName() + ")");
		}
		
		return result;
	}


	@Override
	public List<Layer> findByGeocodeSystem(GeocodeSystem taxonomy) {
		List<Layer> result = null;
		TypedQuery<Layer> query = entityManager.createQuery(
				"select layer from TaxonomyLayer tl where tl.taxonomy = :taxonomy", Layer.class);
		query.setParameter("taxonomy", taxonomy);
		result = query.getResultList();
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
			log.debug("Found " + ((List<Layer>) result).size() + " layers for taxonomy: "+taxonomy.toString());
		return result;
	}


	@Override
	public List<Layer> findByLayerNameAndGeocodeSystem(String layerName, GeocodeSystem taxonomy) {
		log.debug("Fetching layers by layerName: " + layerName + " and taxonomy: " + taxonomy.toString());
		List<Layer> result = null;
		TypedQuery<Layer> query = entityManager.createQuery(
				"select layer from TaxonomyLayer tl where tl.name = :layerName and tl.taxonomy = :taxonomy", Layer.class);
		query.setParameter("layerName", layerName);
		query.setParameter("taxonomy", taxonomy);
		result = query.getResultList();
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
			log.debug("Found: "+((List<Layer>) result).size() + " results for "+layerName + " and taxonomy: "+taxonomy.toString());
		return result;
	}

	
	@Override
	public List<Layer> findAllLayersByGeocodeSystems(List<GeocodeSystem> taxonomies) {
		log.debug("Fetching layer by taxonomies :"+ taxonomies.toString());
		List<Layer> result = null;
		TypedQuery<Layer> query = entityManager.createQuery(
				"from TaxonomyLayer tl where tl.taxonomy in (:taxonomies)", Layer.class);
		query.setParameter("taxonomies", taxonomies);
		result = query.getResultList();
		log.debug("Got "+result.size() +" layers results for given taxonomies: "+
				taxonomies.stream().map(GeocodeSystem::getName).collect(Collectors.toList()));
		return result;
	}


	@Override
	public List<GeocodeSystem> findTaxonomiesOfLayer(Layer layer) {
		List<GeocodeSystem> result = null;
		TypedQuery<GeocodeSystem> query = entityManager.createQuery(
				"select taxonomy from TaxonomyLayer tl where tl.layer = :layer", GeocodeSystem.class);
		query.setParameter("layer", layer);
		result = query.getResultList();
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
			log.debug("Found " + ((List<GeocodeSystem>) result).size() + " taxonomies for layer: "+result.toString());
		return result;
	}
	
	
}

package gr.cite.geoanalytics.dataaccess.entities.layer.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.geocode.TaxonomyLayer;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;

public interface TaxonomyLayerDao extends Dao<TaxonomyLayer, UUID> {

	public List<Layer> findLayerByNameAndGeocodeSystem(String layerName, String taxonomyName);
	public List<Layer> findLayerByNameAndGeocodeSystems(String layerName, List<GeocodeSystem> taxonomies);
	
	public List<Layer> findAllLayersByGeocodeSystems(List<GeocodeSystem> taxonomies);
	
	public List<Layer> findByGeocodeSystem(GeocodeSystem t);
	public List<Layer> findByLayerNameAndGeocodeSystem(String name, GeocodeSystem t);
	
	public List<GeocodeSystem> findTaxonomiesOfLayer(Layer l);
	
	
}

package gr.cite.geoanalytics.dataaccess.entities.layer.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;

public interface LayerDao extends Dao<Layer, UUID> {

	public Layer getLayerById(UUID id);

	public List<Layer> findLayersByName(String layerName);
	
	public List<Layer> findLayersByTenant(Tenant tenant) throws Exception;
	public List<Layer> findLayersNotLinkedToSomeTenant() throws Exception;
	public Layer findTemplateLayerByGeocodeSystem(GeocodeSystem geocodeSystem) throws Exception;
	public List<Layer> getTemplateLayers();

	public List<Layer> getLayersWithStyle(String styleName) throws Exception;
}

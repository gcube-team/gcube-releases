package gr.cite.geoanalytics.dataaccess.entities.layer.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerTenant;

public interface LayerTenantDao extends Dao<LayerTenant, UUID>{

	
	List<LayerTenant> getLayerTenantsByLayerId(UUID id);
	LayerTenant findLayerTenantByLayer(Layer layer);
	
	
}

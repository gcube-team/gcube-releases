/**
 * 
 */
package gr.cite.geoanalytics.dataaccess.entities.layer.dao;

import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerVisualization;

/**
 * @author vfloros
 *
 */
public interface LayerVisualizationDao extends Dao<LayerVisualization, UUID> {
	public LayerVisualization getLayerVisualizationByLayerIDAndTenant(UUID layerID, UUID tenantID);
}

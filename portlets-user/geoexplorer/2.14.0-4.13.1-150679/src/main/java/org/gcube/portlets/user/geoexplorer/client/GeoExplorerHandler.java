/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.client;

import java.util.List;

import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;

/**
 * @author ceras
 *
 * modified by Francesco M.
 */
public interface GeoExplorerHandler {
	
	public void layerItemsSelected(List<LayerItem> layerItems);
	
	/**
	 * 
	 * @param defaultLayers
	 */
	public void loadDefaultLayers(List<LayerItem> defaultLayers);

	/**
	 * @param baseLayers
	 */
	public void loadBaseLayers(List<LayerItem> baseLayers);
	
}

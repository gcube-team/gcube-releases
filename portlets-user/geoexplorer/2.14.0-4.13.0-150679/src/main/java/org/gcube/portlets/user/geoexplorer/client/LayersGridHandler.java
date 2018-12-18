/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.client;

import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;

/**
 * @author ceras
 *
 */
public interface LayersGridHandler {

	/**
	 * @param layerItem
	 */
	void clickLayer(LayerItem layerItem);

	/**
	 * 
	 */
	void layersGridRendered();

	/**
	 * @param l
	 */
	void openLayer(LayerItem l);

}

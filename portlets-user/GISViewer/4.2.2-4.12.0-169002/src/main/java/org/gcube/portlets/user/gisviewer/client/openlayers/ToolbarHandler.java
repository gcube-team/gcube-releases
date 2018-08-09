package org.gcube.portlets.user.gisviewer.client.openlayers;

import org.gcube.portlets.user.gisviewer.client.commons.beans.ExportFormat;
import org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem;

/**
 * The Interface ToolbarHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 30, 2015
 */
public interface ToolbarHandler {
	
	/**
	 * Open browser map image.
	 *
	 * @param exportItem the export item
	 * @param isWMS the is wms
	 */
	public void openBrowserMapImage(ExportFormat exportItem, boolean isWMS);
	
	/**
	 * Save map image.
	 *
	 * @param exportItem the export item
	 * @param isWMS the is wms
	 */
	public void saveMapImage(ExportFormat exportItem, boolean isWMS);
	
	/**
	 * Checks if is save supported.
	 *
	 * @return true, if is save supported
	 */
	public boolean isSaveSupported();
	
	/**
	 * Deactivate transect.
	 *
	 * @param layerItemTransect the layer item transect
	 */
	public void deactivateTransect(LayerItem layerItemTransect);
}

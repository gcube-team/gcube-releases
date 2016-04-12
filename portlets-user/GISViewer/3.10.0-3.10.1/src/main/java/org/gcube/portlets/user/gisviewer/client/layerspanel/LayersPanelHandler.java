package org.gcube.portlets.user.gisviewer.client.layerspanel;

import org.gcube.portlets.user.gisviewer.client.commons.beans.ExportFormat;
import org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem;

/**
 * The Interface LayersPanelHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 30, 2015
 */
public interface LayersPanelHandler {

	/**
	 * Activate transect.
	 *
	 * @param layerItem the layer item
	 * @param table the table
	 * @param field the field
	 */
	public void activateTransect(LayerItem layerItem, String table, String field);

	/**
	 * Open browser layer image.
	 *
	 * @param layerItem the layer item
	 * @param exportItem the export item
	 * @param isWMS the is wms
	 */
	public void openBrowserLayerImage(LayerItem layerItem, ExportFormat exportItem, boolean isWMS);

	/**
	 * Save layer image.
	 *
	 * @param layerItem the layer item
	 * @param exportItem the export item
	 * @param isWMS the is wms
	 */
	public void saveLayerImage(LayerItem layerItem, ExportFormat exportItem, boolean isWMS);

	/**
	 * Show legend.
	 *
	 * @param layerItem the layer item
	 * @param left the left
	 * @param top the top
	 */
	public void showLegend(LayerItem layerItem, int left, int top);


	/**
	 * Apply style for layer.
	 *
	 * @param layerItem the layer item
	 * @param style the style
	 */
	public void applyStyleForLayer(LayerItem layerItem, String style);

	/**
	 * Sets the opacity layer.
	 *
	 * @param layerItem the layer item
	 * @param value the value
	 */
	public void setOpacityLayer(LayerItem layerItem, double value);

	/**
	 * Show layer.
	 *
	 * @param layerItem the layer item
	 */
	public void showLayer(LayerItem layerItem);

	/**
	 * Hide layer.
	 *
	 * @param layerItem the layer item
	 */
	public void hideLayer(LayerItem layerItem);

	/**
	 * Show filter query.
	 *
	 * @param layerItem the layer item
	 * @param left the left
	 * @param top the top
	 * @return true, if successful
	 */
	public boolean showFilterQuery(LayerItem layerItem, int left, int top);

	/**
	 * Removes the filter query.
	 *
	 * @param layerItem the layer item
	 */
	public void removeFilterQuery(LayerItem layerItem);

	/**
	 * Update layers order.
	 */
	public void updateLayersOrder();

	/**
	 * Checks if is save supported.
	 *
	 * @return true, if is save supported
	 */
	public boolean isSaveSupported();

	/**
	 * Deactivate transect.
	 *
	 * @param layerItem the layer item
	 */
	public void deactivateTransect(LayerItem layerItem);

	/**
	 * Removes the layer.
	 *
	 * @param layerItem the layer item
	 */
	public void removeLayer(LayerItem layerItem);


	/**
	 * Z axis value changed.
	 * @param layerItem
	 *
	 * @param value the value
	 */
	public void zAxisValueChanged(LayerItem layerItem, Double value);

}

package org.gcube.portlets.user.geoexplorer.client.rpc;

import java.util.List;

import org.gcube.portlets.user.geoexplorer.client.beans.GeoexplorerMetadataStyleInterface;
import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;

import com.extjs.gxt.ui.client.data.FilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Interface GeoExplorerServiceAsync.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 6, 2017
 */
public interface GeoExplorerServiceAsync {

	/**
	 * Gets the layers.
	 *
	 * @param config the config
	 * @param callback the callback
	 * @return the layers
	 */
	public void getLayers(FilterPagingLoadConfig config, AsyncCallback<PagingLoadResult<LayerItem>> callback);

	/**
	 * Hard refresh.
	 *
	 * @param asyncCallback the async callback
	 */
	public void hardRefresh(AsyncCallback<Boolean> asyncCallback);


	/**
	 * Gets the default layers.
	 *
	 * @param callback the callback
	 * @return the default layers
	 */
	public void getDefaultLayers(AsyncCallback<List<LayerItem>> callback);

	/**
	 * Gets the base layers.
	 *
	 * @param callback the callback
	 * @return the base layers
	 */
	public void getBaseLayers(AsyncCallback<List<LayerItem>> callback);

	/**
	 * Gets the list layer item by uuid.
	 *
	 * @param listMetadataUUID the list metadata uuid
	 * @param callback the callback
	 * @return the list layer item by uuid
	 * @throws Exception the exception
	 */

	public void  getListLayerItemByUUID(List<String> listMetadataUUID, AsyncCallback<List<LayerItem>> callback) throws Exception;

	/**
	 * Inits the geo parameters.
	 *
	 * @param asyncCallback the async callback
	 */
	public void initGeoParameters(AsyncCallback<String> asyncCallback);

	/**
	 * Gets the geoexplorer styles.
	 *
	 * @param onlyIsDisplay the only is display
	 * @param asyncCallback the async callback
	 * @return the geoexplorer styles
	 * @throws Exception the exception
	 */
	public void getGeoexplorerStyles(boolean onlyIsDisplay, AsyncCallback<List<? extends GeoexplorerMetadataStyleInterface>> asyncCallback) throws Exception;

	/**
	 * Gets the gis viewer link for uuid.
	 *
	 * @param uuid the uuid
	 * @param asyncCallback the async callback
	 * @return the gis viewer link for uuid
	 * @throws Exception the exception
	 */
	public void getGisViewerLinkForUUID(String uuid, AsyncCallback<String> asyncCallback) throws Exception;


	/**
	 * Checks if is session expired.
	 *
	 * @param asyncCallback the async callback
	 * @throws Exception the exception
	 */
	public void isSessionExpired(AsyncCallback<Boolean> asyncCallback);

}

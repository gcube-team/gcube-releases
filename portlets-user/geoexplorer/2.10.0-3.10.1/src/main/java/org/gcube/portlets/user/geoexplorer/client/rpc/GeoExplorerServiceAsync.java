package org.gcube.portlets.user.geoexplorer.client.rpc;

import java.util.List;

import org.gcube.portlets.user.geoexplorer.client.beans.GeoexplorerMetadataStyleInterface;
import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;

import com.extjs.gxt.ui.client.data.FilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GeoExplorerServiceAsync {

	public void getLayers(FilterPagingLoadConfig config, AsyncCallback<PagingLoadResult<LayerItem>> callback);

	/**
	 * @param asyncCallback
	 */
	public void hardRefresh(AsyncCallback<Boolean> asyncCallback);
	
	
	/**
	 * 
	 * @param callback
	 */
	public void getDefaultLayers(AsyncCallback<List<LayerItem>> callback);
	
	/**
	 * 
	 * @param callback
	 */
	public void getBaseLayers(AsyncCallback<List<LayerItem>> callback);
	
	/**
	 * 
	 * @param callback
	 * @throws Exception
	 */
	
	public void  getListLayerItemByUUID(List<String> listMetadataUUID, AsyncCallback<List<LayerItem>> callback) throws Exception;

	/**
	 * @param asyncCallback
	 */
	public void initGeoParameters(AsyncCallback<String> asyncCallback);
	
	/**
	 * @param onlyIsDisplay
	 * @param asyncCallback
	 */
	public void getGeoexplorerStyles(boolean onlyIsDisplay, AsyncCallback<List<? extends GeoexplorerMetadataStyleInterface>> asyncCallback) throws Exception;

	/**
	 * 
	 * @param uuid
	 * @param asyncCallback
	 * @throws Exception
	 */
	public void getGisViewerLinkForUUID(String uuid, AsyncCallback<String> asyncCallback) throws Exception;
	
}

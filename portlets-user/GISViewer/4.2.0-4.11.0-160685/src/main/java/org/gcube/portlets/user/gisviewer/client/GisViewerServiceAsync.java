package org.gcube.portlets.user.gisviewer.client;


import java.util.List;

import org.gcube.portlets.user.gisviewer.client.commons.beans.DataResult;
import org.gcube.portlets.user.gisviewer.client.commons.beans.GeoInformationForWMSRequest;
import org.gcube.portlets.user.gisviewer.client.commons.beans.GisViewerBaseLayerInterface;
import org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem;
import org.gcube.portlets.user.gisviewer.client.commons.beans.Property;
import org.gcube.portlets.user.gisviewer.client.commons.beans.TransectParameters;
import org.gcube.portlets.user.gisviewer.client.commons.beans.WebFeatureTable;
import org.gcube.portlets.user.gisviewer.client.commons.beans.WmsRequest;

import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The Interface GisViewerServiceAsync.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 30, 2015
 */
public interface GisViewerServiceAsync {

	/**
	 * Gets the data result.
	 *
	 * @param urls the urls
	 * @param callback the callback
	 * @return the data result
	 */
	public void getDataResult(List<String> urls, AsyncCallback<List<DataResult>> callback);

//	/**
//	 * Gets the groups info.
//	 *
//	 * @param groupName the group name
//	 * @param callback the callback
//	 * @return the groups info
//	 */
//	public void getGroupsInfo(String groupName, AsyncCallback<LayerItemsResult> callback);

//	/**
//	 * Gets the layers info.
//	 *
//	 * @param layersName the layers name
//	 * @param callback the callback
//	 * @return the layers info
//	 */
//	public void getLayersInfo(List<String> layersName, AsyncCallback<LayerItemsResult> callback);
//
//	/**
//	 * Gets the layers info by layer items.
//	 *
//	 * @param layerItems the layer items
//	 * @param asyncCallback the async callback
//	 * @return the layers info by layer items
//	 */
//	public void getLayersInfoByLayerItems(List<LayerItem> layerItems, AsyncCallback<LayerItemsResult> asyncCallback);

	/**
	 * Gets the transect parameters.
	 *
	 * @param callback the callback
	 * @return the transect parameters
	 */
	void getTransectParameters(AsyncCallback<TransectParameters> callback);

	/**
	 * Gets the list property.
	 *
	 * @param geoserverUrl the geoserver url
	 * @param layer the layer
	 * @param callback the callback
	 * @return the list property
	 */
	void getListProperty(String geoserverUrl, LayerItem layer,
			AsyncCallback<List<Property>> callback);

	/**
	 * Gets the data result.
	 *
	 * @param layerItems the layer items
	 * @param bbox the bbox
	 * @param maxWFSFeature the max wfs feature
	 * @param callback the callback
	 * @param zoomLevel
	 * @return the data result
	 */
	void getDataResult(List<LayerItem> layerItems, String bbox,
			int maxWFSFeature, int zoomLevel, AsyncCallback<List<WebFeatureTable>> callback);

	/**
	 * Gets the base layers to gis viewer.
	 *
	 * @param callback the callback
	 * @return the base layers to gis viewer
	 */
	void getBaseLayersToGisViewer(AsyncCallback<List<? extends GisViewerBaseLayerInterface>> callback);

	/**
	 * Parses the wms request.
	 *
	 * @param request the request
	 * @param callback the callback
	 */
	void parseWmsRequest(WmsRequest request, AsyncCallback<String> callback);


	/**
	 * Parses the wms request.
	 *
	 * @param wmsRequest the wms request
	 * @param layerName the layer name
	 * @param callback the callback
	 */
	void parseWmsRequest(String wmsRequest, String layerName,
		AsyncCallback<GeoInformationForWMSRequest> callback);

	/**
	 * Gets the gcube security token.
	 *
	 * @param callback the callback
	 * @return the gcube security token
	 */
	void getGcubeSecurityToken(AsyncCallback<String> callback);

}

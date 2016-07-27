package org.gcube.portlets.user.gisviewer.client;


import java.util.List;

import org.gcube.portlets.user.gisviewer.client.commons.beans.DataResult;
import org.gcube.portlets.user.gisviewer.client.commons.beans.GeoInformationForWMSRequest;
import org.gcube.portlets.user.gisviewer.client.commons.beans.GisViewerBaseLayerInterface;
import org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem;
import org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItemsResult;
import org.gcube.portlets.user.gisviewer.client.commons.beans.Property;
import org.gcube.portlets.user.gisviewer.client.commons.beans.TransectParameters;
import org.gcube.portlets.user.gisviewer.client.commons.beans.WebFeatureTable;
import org.gcube.portlets.user.gisviewer.client.commons.beans.WmsRequest;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 21, 2016
 */
@RemoteServiceRelativePath("GisViewerService")
public interface GisViewerService extends RemoteService {

	/**
	 * Gets the transect parameters.
	 *
	 * @return the transect parameters
	 */
	TransectParameters getTransectParameters();

	/**
	 * Gets the data result.
	 *
	 * @param urls the urls
	 * @return the data result
	 */
	List<DataResult> getDataResult(List<String> urls);

	/**
	 * Gets the groups info.
	 *
	 * @param groupName the group name
	 * @return the groups info
	 */
	LayerItemsResult getGroupsInfo(String groupName);

	/**
	 * Gets the layers info.
	 *
	 * @param layersName the layers name
	 * @return the layers info
	 */
	LayerItemsResult getLayersInfo(List<String> layersName);

	/**
	 * Gets the layers info by layer items.
	 *
	 * @param layerItems the layer items
	 * @return the layers info by layer items
	 */
	LayerItemsResult getLayersInfoByLayerItems(List<LayerItem> layerItems);

	/**
	 * Added by Francesco M. 10/09/2013
	 *
	 * @param geoserverUrl the geoserver url
	 * @param layer the layer
	 * @return the list property
	 */
	List<Property> getListProperty(String geoserverUrl, LayerItem layer);

	/**
	 * Gets the data result.
	 *
	 * @param layerItems the layer items
	 * @param bbox the bbox
	 * @param maxWFSFeature the max wfs feature
	 * @param zoomLevel the zoom level
	 * @return the data result
	 */
	List<WebFeatureTable> getDataResult(
		List<LayerItem> layerItems, String bbox, int maxWFSFeature,
		int zoomLevel);

	/**
	 * Gets the base layers to gis viewer.
	 *
	 * @return the base layers to gis viewer
	 */
	List<? extends GisViewerBaseLayerInterface> getBaseLayersToGisViewer();

	/**
	 * Parses the wms request.
	 *
	 * @param request the request
	 * @return the string
	 * @throws Exception the exception
	 */
	String parseWmsRequest(WmsRequest request) throws Exception;


	/**
	 * Gets the gcube security token.
	 *
	 * @return the gcube security token
	 * @throws Exception the exception
	 */
	String getGcubeSecurityToken() throws Exception;


	/**
	 * Parses the wms request.
	 *
	 * @param wmsRequest the wms request
	 * @param layerName the layer name
	 * @return the gis viewer wms valid parameters
	 * @throws Exception the exception
	 */
	GeoInformationForWMSRequest parseWmsRequest(String wmsRequest, String layerName) throws Exception;

}

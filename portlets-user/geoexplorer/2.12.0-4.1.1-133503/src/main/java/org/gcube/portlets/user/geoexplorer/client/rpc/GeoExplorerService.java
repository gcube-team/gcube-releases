package org.gcube.portlets.user.geoexplorer.client.rpc;

import java.util.List;

import org.gcube.portlets.user.geoexplorer.client.beans.GeoexplorerMetadataStyleInterface;
import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;

import com.extjs.gxt.ui.client.data.FilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("GeoExplorerService")
public interface GeoExplorerService extends RemoteService {
	
	public PagingLoadResult<LayerItem> getLayers(FilterPagingLoadConfig config) throws Exception;
	
	public String initGeoParameters() throws Exception;

	/**
	 * @return
	 * @throws Exception
	 */
	public Boolean hardRefresh() throws Exception;
	
	/**
	 * 
	 * @return
	 * @throws Exception 
	 */
	public List<LayerItem> getDefaultLayers() throws Exception;

	/**
	 * @param listMetadataUUID
	 * @return
	 * @throws Exception
	 */
	public List<LayerItem> getListLayerItemByUUID(List<String> listMetadataUUID) throws Exception;

	/**
	 * @return
	 * @throws Exception
	 */
	public List<LayerItem> getBaseLayers() throws Exception;

	/**
	 * @param onlyIsDisplay
	 * @return
	 * @throws Exception
	 */
	public List<? extends GeoexplorerMetadataStyleInterface> getGeoexplorerStyles(boolean onlyIsDisplay) throws Exception;

	/**
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public String getGisViewerLinkForUUID(String uuid) throws Exception;

}

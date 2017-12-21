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
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 30, 2017
 */
@RemoteServiceRelativePath("GeoExplorerService")
public interface GeoExplorerService extends RemoteService {

	/**
	 * Gets the layers.
	 *
	 * @param config the config
	 * @return the layers
	 * @throws Exception the exception
	 */
	public PagingLoadResult<LayerItem> getLayers(FilterPagingLoadConfig config) throws Exception;

	/**
	 * Inits the geo parameters.
	 *
	 * @return the string
	 * @throws Exception the exception
	 */
	public String initGeoParameters() throws Exception;


	/**
	 * Hard refresh.
	 *
	 * @return the boolean
	 * @throws Exception the exception
	 */
	public Boolean hardRefresh() throws Exception;

	/**
	 * Gets the default layers.
	 *
	 * @return the default layers
	 * @throws Exception the exception
	 */
	public List<LayerItem> getDefaultLayers() throws Exception;

	/**
	 * Gets the list layer item by uuid.
	 *
	 * @param listMetadataUUID the list metadata uuid
	 * @return the list layer item by uuid
	 * @throws Exception the exception
	 */
	public List<LayerItem> getListLayerItemByUUID(List<String> listMetadataUUID) throws Exception;

	/**
	 * Gets the base layers.
	 *
	 * @return the base layers
	 * @throws Exception the exception
	 */
	public List<LayerItem> getBaseLayers() throws Exception;

	/**
	 * Gets the geoexplorer styles.
	 *
	 * @param onlyIsDisplay the only is display
	 * @return the geoexplorer styles
	 * @throws Exception the exception
	 */
	public List<? extends GeoexplorerMetadataStyleInterface> getGeoexplorerStyles(boolean onlyIsDisplay) throws Exception;

	/**
	 * Gets the gis viewer link for uuid.
	 *
	 * @param uuid the uuid
	 * @return the gis viewer link for uuid
	 * @throws Exception the exception
	 */
	public String getGisViewerLinkForUUID(String uuid) throws Exception;

	/**
	 * Checks if is session expired.
	 *
	 * @return the boolean
	 */
	Boolean isSessionExpired();

	/**
	 * Gets the layers by uuid.
	 *
	 * @param listUUIDs the list uui ds
	 * @return the layers by uuid
	 * @throws Exception the exception
	 */
	PagingLoadResult<LayerItem> getLayersByUUID(List<String> listUUIDs)
		throws Exception;

}

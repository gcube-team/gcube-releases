/**
 *
 */
package org.gcube.portlets.user.gcubegisviewer.client;

import java.util.Map;

import org.gcube.portlets.user.gisviewer.client.GisViewerService;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


/**
 * The Interface GCubeGisViewerService.
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * @author updated by Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 27, 2015
 */
@RemoteServiceRelativePath("GisViewerService")
public interface GCubeGisViewerService extends GisViewerService {

	/**
	 * Save layer item.
	 *
	 * @param name the name
	 * @param mimeType the mime type
	 * @param url the url
	 * @param destinationFolderId the destination folder id
	 * @throws Exception the exception
	 */
	public void saveLayerItem(String name, String mimeType, String url, String destinationFolderId) throws Exception;

	/**
	 * Save map image item.
	 *
	 * @param name the name
	 * @param outputFormat the output format
	 * @param parameters the parameters
	 * @param folderId the folder id
	 * @throws GCubeGisViewerServiceException the g cube gis viewer service exception
	 */
	void saveMapImageItem(String name, String outputFormat, Map<String, String> parameters, String folderId) throws GCubeGisViewerServiceException;

}

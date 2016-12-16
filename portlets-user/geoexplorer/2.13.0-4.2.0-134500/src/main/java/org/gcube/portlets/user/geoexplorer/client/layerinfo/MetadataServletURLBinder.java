/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.client.layerinfo;

import org.gcube.portlets.user.geoexplorer.client.Constants;
import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Random;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 5, 2013
 *
 */
public class MetadataServletURLBinder {
	

	/**
	 * 
	 * @param servletName
	 * @param layer
	 * @param returnTagHead
	 * @param returnTagBody
	 * @param loadPreviewLayer
	 * @param scope
	 * @return
	 */
	public static String getMetadataViewerURL(String servletName, LayerItem layer, boolean returnTagHead, boolean returnTagBody, boolean loadPreviewLayer, String scope) {
		// <serverName>/<servletName>?geoserver=<geoserverUrl>&layer=<layerCompleteName>
		return GWT.getModuleBaseURL() + servletName + "?" + Constants.UUID
				+ "=" + layer.getUuid() + "&" + Constants.GETBODYHTML + "="
				+ returnTagBody + "&" + Constants.GETHEADHTML + "="
				+ returnTagHead + "&" + Constants.LOADPREVIEW + "="
				+ loadPreviewLayer + "&" + Constants.RANDOM +"="
				+ Random.nextInt(Constants.UPPERBOUND)*Random.nextInt(Constants.UPPERBOUND) 
				+ "&" + Constants.SCOPE +"=" +scope;
				

	}
	

	/**
	 * 
	 * @param layer
	 * @param currTab
	 * @param scope
	 * @return
	 */
	public static String getEmbeddedGeonetworkMetadataViewerURL(LayerItem layer, String currTab, String scope) {
		return GWT.getModuleBaseURL() + Constants.EMBEDDED_GEONETWORK_METADATA_ISO19139_VIEW + "?" 
				+ Constants.UUID+ "=" + layer.getUuid() + "&" 
				+ Constants.CURRTAB + "=" + currTab+ "&"
				+ Constants.RANDOM +"="+Random.nextInt(Constants.UPPERBOUND)*Random.nextInt(Constants.UPPERBOUND)
				+ "&" + Constants.SCOPE +"=" +scope;
	}

	/**
	 * 
	 * @param uuid
	 * @param returnTagHead
	 * @param returnTagBody
	 * @param loadPreviewLayer
	 * @return
	 */
	public static String getMetadataSourceViewerURLWithUUIDParameter(String uuid, String scope) {
		return getMetadataSourceViewerURL() 
				+ "?" + Constants.UUID + "=" 
				+ uuid+ "&" + Constants.RANDOM 
				+"="+Random.nextInt(Constants.UPPERBOUND)*Random.nextInt(Constants.UPPERBOUND)
				+ "&" + Constants.SCOPE +"=" +scope;
	}
	

	/**
	 * 	
	 * @return
	 */
	public static String getMetadataSourceViewerURL() {
		return GWT.getModuleBaseURL() + Constants.METADATA_ISO19139_SOURCE_VIEW;
	}

}

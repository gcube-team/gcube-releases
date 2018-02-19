/**
 *
 */
package org.gcube.portlets.user.gisviewerapp.client;

import org.gcube.portlets.user.gisviewer.client.GisViewerPanel;
import org.gcube.portlets.user.gisviewer.client.GisViewerPanel.LayerType;
import org.gcube.portlets.user.gisviewer.client.commons.utils.WmsParameters;
import org.gcube.portlets.user.gisviewer.client.commons.utils.WmsUrlValidator;

import com.google.gwt.user.client.Window;



/**
 * The Class WmsRequestConverter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 17, 2017
 */
public class WmsRequestConverter {

	private GisViewerPanel gisViewer;

	private LayerType layerType = LayerType.FEATURE_TYPE;
	private String title;
	private String displayLayerName;
	private String url;
	private boolean isExternal = false;
	private boolean isBase = false;
	private boolean displayInLayerSwitcher = false;
	private boolean onTop = true;
	private String wmsRequest;

	/**
	 * Instantiates a new wms request converter.
	 *
	 * @param gisViewerPanel the gis viewer panel
	 */
	public WmsRequestConverter(GisViewerPanel gisViewerPanel) {
		this.gisViewer = gisViewerPanel;
	}


	/**
	 * Adds the request to gis viewer.
	 *
	 * @param wmsRequest the wms request
	 * @param displayName the display name
	 * @param layerUUID the layer uuid
	 * @throws Exception the exception
	 */
	public void addRequestToGisViewer(String wmsRequest, String displayName, String layerUUID) throws Exception{
		this.wmsRequest = wmsRequest;

		//FIND BASE URL
		int indexStart = wmsRequest.indexOf("?");
		if(indexStart>=0){
			url = wmsRequest.substring(0, indexStart); //get only base uri
			url = url.trim(); //string trim
		}else{
			Window.alert("Bad wms request '?' not found!");
//			throw new Exception("Bad server request '?' not found!");
		}

		String layerName = WmsUrlValidator.getValueOfParameter(WmsParameters.LAYERS, wmsRequest);
		displayName = displayName==null || displayName.isEmpty()?layerName:displayName;
		gisViewer.addLayerByWmsRequest(displayName, layerName, wmsRequest, isBase, displayInLayerSwitcher, layerUUID, onTop);
	}


	/**
	 * Gets the wms request.
	 *
	 * @return the wmsRequest
	 */
	public String getWmsRequest() {

		return wmsRequest;
	}

	/**
	 * Gets the layer type.
	 *
	 * @return the layer type
	 */
	public LayerType getLayerType() {
		return layerType;
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets the layer name.
	 *
	 * @return the layer name
	 */
	public String getLayerName() {
		return displayLayerName;
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Checks if is external.
	 *
	 * @return true, if is external
	 */
	public boolean isExternal() {
		return isExternal;
	}

	/**
	 * Checks if is base.
	 *
	 * @return true, if is base
	 */
	public boolean isBase() {
		return isBase;
	}

	/**
	 * Checks if is display in layer switcher.
	 *
	 * @return true, if is display in layer switcher
	 */
	public boolean isDisplayInLayerSwitcher() {
		return displayInLayerSwitcher;
	}

	/**
	 * Checks if is on top.
	 *
	 * @return true, if is on top
	 */
	public boolean isOnTop() {
		return onTop;
	}

	/**
	 * Sets the layer type.
	 *
	 * @param layerType the new layer type
	 */
	public void setLayerType(LayerType layerType) {
		this.layerType = layerType;
	}

	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Sets the layer name.
	 *
	 * @param layerName the new layer name
	 */
	public void setLayerName(String layerName) {
		this.displayLayerName = layerName;
	}

	/**
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Sets the external.
	 *
	 * @param isExternal the new external
	 */
	public void setExternal(boolean isExternal) {
		this.isExternal = isExternal;
	}

	/**
	 * Sets the base.
	 *
	 * @param isBase the new base
	 */
	public void setBase(boolean isBase) {
		this.isBase = isBase;
	}

	/**
	 * Sets the display in layer switcher.
	 *
	 * @param displayInLayerSwitcher the new display in layer switcher
	 */
	public void setDisplayInLayerSwitcher(boolean displayInLayerSwitcher) {
		this.displayInLayerSwitcher = displayInLayerSwitcher;
	}

	/**
	 * Sets the on top.
	 *
	 * @param onTop the new on top
	 */
	public void setOnTop(boolean onTop) {
		this.onTop = onTop;
	}
}

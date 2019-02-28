/**
 *
 */
package org.gcube.portlets.user.gisviewer.client.commons.beans;

import java.io.Serializable;
import java.util.HashMap;



/**
 * The Class GeoInformationForWMSRequest.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 12, 2016
 */
public class GeoInformationForWMSRequest implements Serializable{

	private static final long serialVersionUID = 8871057872297550295L;
	private String baseWmsServiceHost;
	private String wmsRequest;
	private String layerName;
	private String versionWMS;
	private String crs;
	private HashMap<String, String> mapWMSNoStandardParams;
	private Styles styles;

	//TODO TO BE REMOVED
	private boolean isNcWMS;

	private ZAxis zAxis;


	/**
	 * Instantiates a new geo information for wms request.
	 */
	public GeoInformationForWMSRequest() {

	}


	/**
	 * Instantiates a new geo information for wms request.
	 *
	 * @param baseWmsServiceHost the base wms service host
	 * @param wmsRequest the wms request
	 * @param layerName the layer name
	 * @param versionWms the version wms
	 * @param crs the crs
	 * @param mapWmsNoStandard the map wms not standard
	 * @param styles the layer styles
	 * @param zAxis the z axis
	 */
	public GeoInformationForWMSRequest(String baseWmsServiceHost, String wmsRequest, String layerName, String versionWms, String crs, HashMap<String, String> mapWmsNoStandard, Styles styles, boolean isNcWMS, ZAxis zAxis) {
		this.baseWmsServiceHost = baseWmsServiceHost;
		this.wmsRequest = wmsRequest;
		this.layerName = layerName;
		this.versionWMS = versionWms;
		this.crs = crs;
		this.mapWMSNoStandardParams = mapWmsNoStandard;
		this.styles = styles;
		this.zAxis = zAxis;
		this.isNcWMS = isNcWMS;
	}

	/**
	 * Gets the z axis.
	 *
	 * @return the zAxis
	 */
	public ZAxis getZAxis() {

		return zAxis;
	}

	/**
	 * Sets the z axis.
	 *
	 * @param zAxis the zAxis to set
	 */
	public void setZAxis(ZAxis zAxis) {

		this.zAxis = zAxis;
	}

	/**
	 * Gets the base wms service host.
	 *
	 * @return the baseWmsServiceHost
	 */
	public String getBaseWmsServiceHost() {

		return baseWmsServiceHost;
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
	 * Gets the layer name.
	 *
	 * @return the layerName
	 */
	public String getLayerName() {

		return layerName;
	}


	/**
	 * Gets the version wms.
	 *
	 * @return the versionWMS
	 */
	public String getVersionWMS() {

		return versionWMS;
	}


	/**
	 * Gets the crs.
	 *
	 * @return the crs
	 */
	public String getCrs() {

		return crs;
	}


	/**
	 * Gets the map wms no standard.
	 *
	 * @return the mapWMSNoStandard
	 */
	public HashMap<String, String> getMapWMSNoStandard() {

		return mapWMSNoStandardParams;
	}


	/**
	 * Gets the styles.
	 *
	 * @return the styles
	 */
	public Styles getStyles() {

		return styles;
	}


	/**
	 * Checks if is nc wms.
	 *
	 * @return the isNcWMS
	 */
	public boolean isNcWMS() {

		return isNcWMS;
	}


	/**
	 * Sets the base wms service host.
	 *
	 * @param baseWmsServiceHost the baseWmsServiceHost to set
	 */
	public void setBaseWmsServiceHost(String baseWmsServiceHost) {

		this.baseWmsServiceHost = baseWmsServiceHost;
	}


	/**
	 * Sets the wms request.
	 *
	 * @param wmsRequest the wmsRequest to set
	 */
	public void setWmsRequest(String wmsRequest) {

		this.wmsRequest = wmsRequest;
	}


	/**
	 * Sets the layer name.
	 *
	 * @param layerName the layerName to set
	 */
	public void setLayerName(String layerName) {

		this.layerName = layerName;
	}


	/**
	 * Sets the version wms.
	 *
	 * @param versionWMS the versionWMS to set
	 */
	public void setVersionWMS(String versionWMS) {

		this.versionWMS = versionWMS;
	}


	/**
	 * Sets the crs.
	 *
	 * @param crs the crs to set
	 */
	public void setCrs(String crs) {

		this.crs = crs;
	}


	/**
	 * Sets the map wms no standard.
	 *
	 * @param mapWMSNoStandard the mapWMSNoStandard to set
	 */
	public void setMapWMSNoStandard(HashMap<String, String> mapWMSNoStandard) {

		this.mapWMSNoStandardParams = mapWMSNoStandard;
	}


	/**
	 * Sets the styles.
	 *
	 * @param styles the styles to set
	 */
	public void setStyles(Styles styles) {

		this.styles = styles;
	}


	/**
	 * Sets the nc wms.
	 *
	 * @param isNcWMS the isNcWMS to set
	 */
	public void setNcWMS(boolean isNcWMS) {

		this.isNcWMS = isNcWMS;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("GeoInformationForWMSRequest [baseWmsServiceHost=");
		builder.append(baseWmsServiceHost);
		builder.append(", wmsRequest=");
		builder.append(wmsRequest);
		builder.append(", layerName=");
		builder.append(layerName);
		builder.append(", versionWMS=");
		builder.append(versionWMS);
		builder.append(", crs=");
		builder.append(crs);
		builder.append(", mapWMSNoStandardParams=");
		builder.append(mapWMSNoStandardParams);
		builder.append(", styles=");
		builder.append(styles);
		builder.append(", isNcWMS=");
		builder.append(isNcWMS);
		builder.append(", zAxis=");
		builder.append(zAxis);
		builder.append("]");
		return builder.toString();
	}


}

/**
 *
 */
package org.gcube.portlets.user.gisviewer.client.commons.beans;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * The Class WmsRequest.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 27, 2015
 */
public class WmsRequest implements IsSerializable{

	private String bbox;
	private String width;
	private String height;
	private String wmsServerURI;
	private String wmsServerVersion;
	private String layer;
	private String style;
	private String cqlfilter;
	private String srs;
	private String crs;
	private String format;
	private String transparent;
	private String pairWmsNonStandardParameters;
	private Double elevation;

	/**
	 * Instantiates a new wms request.
	 */
	public WmsRequest() {
	}

	/**
	 * Instantiates a new wms request.
	 *
	 * @param bbox the bbox
	 * @param width the width
	 * @param height the height
	 * @param wmsServerURI the wms server uri
	 * @param wmsServerVersion the wms server version
	 * @param layer the layer
	 * @param style the style
	 * @param cqlfilter the cqlfilter
	 * @param srs the srs
	 * @param crs the crs
	 * @param format the format
	 * @param transparent the transparent
	 * @param pairWmsNonStandardParameters key1=value1&key2=value2...keyN=valueN of wms non-standard parameters
	 * @param elevation the elevation
	 */
	public WmsRequest(String bbox, String width, String height,
			String wmsServerURI, String wmsServerVersion, String layer,
			String style, String cqlfilter, String srs, String crs,
			String format, String transparent, String pairWmsNonStandardParameters, Double elevation) {
		super();
		this.bbox = bbox;
		this.width = width;
		this.height = height;
		this.wmsServerURI = wmsServerURI;
		this.wmsServerVersion = wmsServerVersion;
		this.layer = layer;
		this.style = style;
		this.cqlfilter = cqlfilter;
		this.srs = srs;
		this.crs = crs;
		this.format = format;
		this.transparent = transparent;
		this.pairWmsNonStandardParameters = pairWmsNonStandardParameters;
		this.elevation = elevation;
	}

	/**
	 * Gets the elevation.
	 *
	 * @return the elevation
	 */
	public Double getElevation() {

		return elevation;
	}

	/**
	 * Sets the elevation.
	 *
	 * @param elevation the elevation to set
	 */
	public void setElevation(Double elevation) {

		this.elevation = elevation;
	}

	/**
	 * Gets the bbox.
	 *
	 * @return the bbox
	 */
	public String getBbox() {
		return bbox;
	}

	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	public String getHeight() {
		return height;
	}

	/**
	 * Gets the wms server uri.
	 *
	 * @return the wms server uri
	 */
	public String getWmsServerURI() {
		return wmsServerURI;
	}

	/**
	 * Gets the wms server version.
	 *
	 * @return the wms server version
	 */
	public String getWmsServerVersion() {
		return wmsServerVersion;
	}

	/**
	 * Gets the layer.
	 *
	 * @return the layer
	 */
	public String getLayer() {
		return layer;
	}

	/**
	 * Gets the style.
	 *
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * Gets the cqlfilter.
	 *
	 * @return the cqlfilter
	 */
	public String getCqlfilter() {
		return cqlfilter;
	}

	/**
	 * Gets the srs.
	 *
	 * @return the srs
	 */
	public String getSrs() {
		return srs;
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
	 * Gets the format.
	 *
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Gets the transparent.
	 *
	 * @return the transparent
	 */
	public String getTransparent() {
		return transparent;
	}

	/**
	 * Sets the bbox.
	 *
	 * @param bbox the new bbox
	 */
	public void setBbox(String bbox) {
		this.bbox = bbox;
	}

	/**
	 * Sets the width.
	 *
	 * @param width the new width
	 */
	public void setWidth(String width) {
		this.width = width;
	}

	/**
	 * Sets the height.
	 *
	 * @param height the new height
	 */
	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * Sets the wms server uri.
	 *
	 * @param wmsServerURI the new wms server uri
	 */
	public void setWmsServerURI(String wmsServerURI) {
		this.wmsServerURI = wmsServerURI;
	}

	/**
	 * Sets the wms server version.
	 *
	 * @param wmsServerVersion the new wms server version
	 */
	public void setWmsServerVersion(String wmsServerVersion) {
		this.wmsServerVersion = wmsServerVersion;
	}

	/**
	 * Sets the layer.
	 *
	 * @param layer the new layer
	 */
	public void setLayer(String layer) {
		this.layer = layer;
	}

	/**
	 * Sets the style.
	 *
	 * @param style the new style
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * Sets the cqlfilter.
	 *
	 * @param cqlfilter the new cqlfilter
	 */
	public void setCqlfilter(String cqlfilter) {
		this.cqlfilter = cqlfilter;
	}

	/**
	 * Sets the srs.
	 *
	 * @param srs the new srs
	 */
	public void setSrs(String srs) {
		this.srs = srs;
	}

	/**
	 * Sets the crs.
	 *
	 * @param crs the new crs
	 */
	public void setCrs(String crs) {
		this.crs = crs;
	}

	/**
	 * Sets the format.
	 *
	 * @param format the new format
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * Sets the transparent.
	 *
	 * @param transparent the new transparent
	 */
	public void setTransparent(String transparent) {
		this.transparent = transparent;
	}

	/**
	 * Gets the pair wms non standard parameters.
	 *
	 * @return the pairWmsNonStandardParameters
	 */
	public String getPairWmsNonStandardParameters() {
		return pairWmsNonStandardParameters;
	}

	/**
	 * Sets the pair wms non standard parameters.
	 *
	 * @param pairWmsNonStandardParameters the pairWmsNonStandardParameters to set
	 */
	public void setPairWmsNonStandardParameters(String pairWmsNonStandardParameters) {
		this.pairWmsNonStandardParameters = pairWmsNonStandardParameters;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("WmsRequest [bbox=");
		builder.append(bbox);
		builder.append(", width=");
		builder.append(width);
		builder.append(", height=");
		builder.append(height);
		builder.append(", wmsServerURI=");
		builder.append(wmsServerURI);
		builder.append(", wmsServerVersion=");
		builder.append(wmsServerVersion);
		builder.append(", layer=");
		builder.append(layer);
		builder.append(", style=");
		builder.append(style);
		builder.append(", cqlfilter=");
		builder.append(cqlfilter);
		builder.append(", srs=");
		builder.append(srs);
		builder.append(", crs=");
		builder.append(crs);
		builder.append(", format=");
		builder.append(format);
		builder.append(", transparent=");
		builder.append(transparent);
		builder.append(", pairWmsNonStandardParameters=");
		builder.append(pairWmsNonStandardParameters);
		builder.append(", elevation=");
		builder.append(elevation);
		builder.append("]");
		return builder.toString();
	}

}

/**
 *
 */
package org.gcube.portlets.user.gisviewer.client.commons.beans;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * The Class LayerStyles.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 10, 2016
 */
public class Styles implements IsSerializable{

	private List<String> geoStyles;
	private Map<String, String> mapNcWmsStyles;
	boolean isNcWms  = false;

	/**
	 * Instantiates a new layer styles.
	 */
	public Styles() {
	}

	/**
	 * Instantiates a new layer styles.
	 *
	 * @param geoStyles the geo styles
	 * @param mapNcWmsStyles the map nc wms styles
	 * @param isNcWms the is nc wms
	 */
	public Styles(List<String> geoStyles, Map<String, String> mapNcWmsStyles,boolean isNcWms) {

		this.geoStyles = geoStyles;
		this.mapNcWmsStyles = mapNcWmsStyles;
		this.isNcWms = isNcWms;
	}


	/**
	 * Gets the geo styles.
	 *
	 * @return the geoStyles
	 */
	public List<String> getGeoStyles() {

		return geoStyles;
	}


	/**
	 * Gets the map nc wms styles.
	 *
	 * @return the mapNcWmsStyles
	 */
	public Map<String, String> getMapNcWmsStyles() {

		return mapNcWmsStyles;
	}


	/**
	 * Checks if is nc wms.
	 *
	 * @return the isNcWms
	 */
	public boolean isNcWms() {

		return isNcWms;
	}


	/**
	 * Sets the geo styles.
	 *
	 * @param geoStyles the geoStyles to set
	 */
	public void setGeoStyles(List<String> geoStyles) {

		this.geoStyles = geoStyles;
	}


	/**
	 * Sets the map nc wms styles.
	 *
	 * @param mapNcWmsStyles the mapNcWmsStyles to set
	 */
	public void setMapNcWmsStyles(Map<String, String> mapNcWmsStyles) {

		this.mapNcWmsStyles = mapNcWmsStyles;
	}


	/**
	 * Sets the nc wms.
	 *
	 * @param isNcWms the isNcWms to set
	 */
	public void setNcWms(boolean isNcWms) {

		this.isNcWms = isNcWms;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("Styles [geoStyles=");
		builder.append(geoStyles);
		builder.append(", mapNcWmsStyles=");
		builder.append(mapNcWmsStyles);
		builder.append(", isNcWms=");
		builder.append(isNcWms);
		builder.append("]");
		return builder.toString();
	}
}

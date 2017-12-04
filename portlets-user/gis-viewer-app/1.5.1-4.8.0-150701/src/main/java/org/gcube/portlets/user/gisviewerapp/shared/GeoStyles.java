/**
 *
 */

package org.gcube.portlets.user.gisviewerapp.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * The Class GeoStyles.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 17, 2017
 */
public class GeoStyles implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 7307380872802285337L;
	private List<String> styles;
	boolean isNcWMS = false;
	private Map<String, String> mapNcWmsStyles;

	/**
	 * Instantiates a new geo styles.
	 */
	public GeoStyles() {

	}

	/**
	 * Instantiates a new geo styles.
	 *
	 * @param styles the styles
	 * @param isNcWMS the is nc wms
	 */
	public GeoStyles(List<String> styles, boolean isNcWMS) {

		super();
		this.styles = styles;
		this.isNcWMS = isNcWMS;
	}


	/**
	 * Gets the styles.
	 *
	 * @return the styles
	 */
	public List<String> getStyles() {

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
	 * Sets the styles.
	 *
	 * @param styles the styles to set
	 */
	public void setStyles(List<String> styles) {

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

	/**
	 * Sets the map nc wms styles.
	 *
	 * @param mapNcWmsStyles the mapNcWmsStandardStyles to set
	 */
	public void setMapNcWmsStyles(Map<String, String> mapNcWmsStyles) {

		this.mapNcWmsStyles = mapNcWmsStyles;
	}

	/**
	 * Gets the map nc wms styles.
	 *
	 * @return the mapNcWmsStyles
	 */
	public Map<String, String> getMapNcWmsStyles() {

		return mapNcWmsStyles==null?new HashMap<String, String>(1):mapNcWmsStyles;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("GeoStyles [styles=");
		builder.append(styles);
		builder.append(", isNcWMS=");
		builder.append(isNcWMS);
		builder.append(", mapNcWmsStyles=");
		builder.append(mapNcWmsStyles);
		builder.append("]");
		return builder.toString();
	}

}

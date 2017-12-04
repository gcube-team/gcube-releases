/**
 *
 */

package org.gcube.portlets.user.gisviewerapp.shared;

import java.io.Serializable;
import java.util.Map;



/**
 * The Class GeoInformation.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 17, 2017
 */
public class GeoInformation implements Serializable {

	private static final long serialVersionUID = 9205334969349325638L;
	private String title;
	private String displayName;
	private String layerName;
	private Map<String, String> parametersMap;
	private GeoStyles geoStyle;
	private Map<String, String> mapWmsNoStandardParams;

	/**
	 * Instantiates a new geo information.
	 */
	public GeoInformation() {

	}

	/**
	 * Sets the title.
	 *
	 * @param title
	 *            the new title
	 */
	public void setTitle(String title) {

		this.title = title;
	}

	/**
	 * Sets the display name.
	 *
	 * @param displayName
	 *            the new display name
	 */
	public void setDisplayName(String displayName) {

		this.displayName = displayName;
	}

	/**
	 * Sets the layer name.
	 *
	 * @param layerName
	 *            the new layer name
	 */
	public void setLayerName(String layerName) {

		this.layerName = layerName;
	}

	/**
	 * Sets the geo style.
	 *
	 * @param geo the new geo style
	 */
	public void setGeoStyle(GeoStyles geo) {

		this.geoStyle = geo;
	}

	/**
	 * Gets the geo style.
	 *
	 * @return the geoStyle
	 */
	public GeoStyles getGeoStyle() {

		return geoStyle;
	}

	/**
	 * Sets the parameters map.
	 *
	 * @param parametersMap
	 *            the parameters map
	 */
	public void setParametersMap(Map<String, String> parametersMap) {

		this.parametersMap = parametersMap;
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
	 * Gets the display name.
	 *
	 * @return the displayName
	 */
	public String getDisplayName() {

		return displayName;
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
	 * Gets the parameters map.
	 *
	 * @return the parametersMap
	 */
	public Map<String, String> getParametersMap() {

		return parametersMap;
	}

	/**
	 * Sets the no wms parameters.
	 *
	 * @param mapWmsNotStandardParams the map wms not standard params
	 */
	public void setNoWmsParameters(Map<String, String> mapWmsNotStandardParams) {
		this.mapWmsNoStandardParams = mapWmsNotStandardParams;

	}

	/**
	 * Gets the map wms no standard params.
	 *
	 * @return the map wms no standard params
	 */
	public Map<String, String> getMapWmsNoStandardParams() {
		return mapWmsNoStandardParams;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("GeoInformation [title=");
		builder.append(title);
		builder.append(", displayName=");
		builder.append(displayName);
		builder.append(", layerName=");
		builder.append(layerName);
		builder.append(", parametersMap=");
		builder.append(parametersMap);
		builder.append(", geoStyle=");
		builder.append(geoStyle);
		builder.append(", mapWmsNoStandardParams=");
		builder.append(mapWmsNoStandardParams);
		builder.append("]");
		return builder.toString();
	}
}

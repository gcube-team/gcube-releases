/**
 *
 */
package org.gcube.portlets.user.geoexplorer.client.beans;


import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The Class LayerItem.
 * @author Ceras
 * @author updated by Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 24, 2015
 */
public class LayerItem extends BaseTreeModel implements IsSerializable, Cloneable {


	private static final long serialVersionUID = -8759678454213762088L;
	private BoundsMap boundsMap;

	private List<String> styles = null;
	private String versionWMS = "";
	private String crs = "";

	public static final String LAYER_TITLE = "Title";
	public static final String LAYER_UUID = "UUID";
	public static final String TOPIC_CATEGORY = "Topic Category";
	public static final String PUBLICATION_DATE = "Publication Date";
	public static final String SCOPE_CODE = "Scope Code";
	public static final String GEOSERVER_URL = "Geoserver Url";
	public static final String LAYER_NAME = "name"; // THE VALUE OF THIS
													// PROPERTY IS EQUAL TO
													// LAYER_TITLE
	public static final String LAYER = "layer"; // THE VALUE OF THIS PROPERTY IS
												// EQUAL TO LAYER_NAME
	public static final String WMS_SERVICE_URL = "WMS_SERVICE_URL";
	public static final String FULL_REQUEST_WMS = "FULL_REQUEST_WMS";
	public static final String IS_INTERNAL_LAYER = "IS_INTERNAL_LAYER";
	public static final String ABSTRACT_DESCRIPTION = "ABSTRACT_DESCRIPTION";
	public static final String KEYWORDS = "KEYWORDS";

	public static final int MAX_CHARS = 100;
	public static final int CHARS_AFTER_ELLIPSE = 0;
//	public static final String LAYER_STYLES = "LAYER_STYLES";

	private HashMap<String, String> mapWmsNotStandardParameters; //WMS vendor parameters that are non-standard (eg. Thredds colorscalerange=XYZ)
	private boolean isNcWMS = false; //SUPPORT NON-STANDARD WMS?

	/**
	 * Instantiates a new layer item.
	 *
	 * @param uuid the uuid
	 * @param title the title
	 * @param layerName the layer name
	 * @param topicCategory the topic category
	 * @param publicationDate the publication date
	 * @param scopeCode the scope code
	 * @param geoserverUrl the geoserver url
	 * @param wmsServiceUrl the wms service url
	 * @param fullRequestWms the full request wms
	 * @param isInternalLayer the is internal layer
	 * @param styles the styles
	 * @param abstractDescription the abstract description
	 * @param listKeywords the list keywords
	 * @param mapWmsNotStandard the map wms not standard
	 * @param isNcWms the is nc wms
	 *
	 * {@link LayerItem#LayerItem(String, String, String, String, Date, String, String, String, String, boolean, String, List) use new constructor}
	 */
	@Deprecated
	public LayerItem(String uuid, String title, String layerName,
			String topicCategory, Date publicationDate, String scopeCode,
			String geoserverUrl, String wmsServiceUrl, String fullRequestWms,
			boolean isInternalLayer, List<String> styles, String abstractDescription, List<String> listKeywords, HashMap<String, String> mapWmsNotStandard, boolean isNcWms) {
		set(LAYER_UUID, uuid);
		set(LAYER_TITLE, title);
		set(TOPIC_CATEGORY, topicCategory);
		set(PUBLICATION_DATE, publicationDate);
		set(SCOPE_CODE, scopeCode);
		set(GEOSERVER_URL, geoserverUrl);
		set(LAYER_NAME, layerName);
		set(LAYER, layerName);
		set(WMS_SERVICE_URL, wmsServiceUrl);
		set(FULL_REQUEST_WMS, fullRequestWms);
		set(IS_INTERNAL_LAYER, isInternalLayer);
		setNcWMS(isNcWms);
		setKeywords(listKeywords);
		setAbstractDescription(abstractDescription);
		setStyles(styles);
		setMapWmsNotStandardParameters(mapWmsNotStandard);

	}


	/**
	 * Instantiates a new layer item.
	 *
	 * @param uuid the uuid
	 * @param title the title
	 * @param layerName the layer name
	 * @param topicCategory the topic category
	 * @param publicationDate the publication date
	 * @param scopeCode the scope code
	 * @param geoserverUrl the geoserver url
	 * @param wmsServiceUrl the wms service url
	 * @param fullRequestWms the full request wms
	 * @param isInternalLayer the is internal layer
	 * @param abstractDescription the abstract description
	 * @param listKeywords the list keywords
	 */
	public LayerItem(String uuid, String title, String layerName,
		String topicCategory, Date publicationDate, String scopeCode,
		String geoserverUrl, String wmsServiceUrl, String fullRequestWms,
		boolean isInternalLayer, String abstractDescription, List<String> listKeywords) {
	set(LAYER_UUID, uuid);
	set(LAYER_TITLE, title);
	set(TOPIC_CATEGORY, topicCategory);
	set(PUBLICATION_DATE, publicationDate);
	set(SCOPE_CODE, scopeCode);
	set(GEOSERVER_URL, geoserverUrl);
	set(LAYER_NAME, layerName);
	set(LAYER, layerName);
	set(WMS_SERVICE_URL, wmsServiceUrl);
	set(FULL_REQUEST_WMS, fullRequestWms);
	set(IS_INTERNAL_LAYER, isInternalLayer);
	setKeywords(listKeywords);
	setAbstractDescription(abstractDescription);
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
	 * Sets the nc wms.
	 *
	 * @param isNcWMS the isNcWMS to set
	 */
	@Deprecated
	public void setNcWMS(boolean isNcWMS) {
		this.isNcWMS = isNcWMS;
	}

	/**
	 * Sets the abstract description.
	 *
	 * @param abstractDescription the new abstract description
	 */
	public void setAbstractDescription(String abstractDescription) {
		set(ABSTRACT_DESCRIPTION, abstractDescription);
	}

	/**
	 * Sets the keywords.
	 *
	 * @param listKeywords the new keywords
	 */
	public void setKeywords(List<String> listKeywords) {
		set(KEYWORDS, listKeywords);
	}

	/**
	 * Gets the keywords.
	 *
	 * @return the keywords
	 */
	public List<String> getKeywords() {
		return (List<String>) get(KEYWORDS);
	}

	/**
	 * Gets the map wms not standard parameters.
	 *
	 * @return the mapWmsNotStandardParameters
	 */
	public HashMap<String, String> getMapWmsNotStandardParameters() {
		return mapWmsNotStandardParameters;
	}

	/**
	 * Sets the map wms not standard parameters.
	 *
	 * @param mapWmsNotStandardParameters the mapWmsNotStandardParameters to set
	 */
	@Deprecated
	public void setMapWmsNotStandardParameters(
			HashMap<String, String> mapWmsNotStandardParameters) {
		this.mapWmsNotStandardParameters = mapWmsNotStandardParameters;
	}

	/**
	 * Instantiates a new layer item.
	 */
	public LayerItem() {
	}

	/**
	 * Gets the abstract description.
	 *
	 * @param ellipsis the ellipsis
	 * @param maxCharacters the maximum characters that are acceptable for the unshortended string. Must be at least 3, otherwise a string with ellipses is too long already.
	 * @param charactersAfterEllipsis the characters after ellipsis
	 * @return the abstract description
	 */
	public String getAbstractDescription(boolean ellipsis, int maxCharacters, int charactersAfterEllipsis) {

		String abstractDescr = get(ABSTRACT_DESCRIPTION);

		if(abstractDescr != null && ellipsis){
			try{
				abstractDescr = ellipsize(abstractDescr, maxCharacters, charactersAfterEllipsis);
			}catch (Exception e) {
				return get(ABSTRACT_DESCRIPTION);
			}
		}

		return abstractDescr;
	}


	/**
	 * Gets the abstract description.
	 *
	 * @param ellipsis the ellipsis
	 * @return the abstract description
	 */
	public String getAbstractDescription(boolean ellipsis) {

		String abstractDescr = get(ABSTRACT_DESCRIPTION);

		System.out.println("abstractDescr: "+abstractDescr);

		if(abstractDescr != null && ellipsis){
			try{
				abstractDescr = ellipsize(abstractDescr, MAX_CHARS, CHARS_AFTER_ELLIPSE);

				System.out.println("after ellipses abstractDescr: "+abstractDescr);
			}catch (Exception e) {
				return get(ABSTRACT_DESCRIPTION);
			}
		}



		return abstractDescr;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return get(LAYER_NAME);
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		set(LAYER_NAME, name);
	}

	/**
	 * Sets the checks if is internal layer.
	 *
	 * @param isInternalLayer
	 *            the new checks if is internal layer
	 */
	public void setIsInternalLayer(boolean isInternalLayer) {

		set(IS_INTERNAL_LAYER, isInternalLayer);
	}

	/**
	 * Checks if is internal layer.
	 *
	 * @return true, if is internal layer
	 */
	public boolean isInternalLayer() {
		return (Boolean) get(IS_INTERNAL_LAYER);
	}

	/**
	 * Gets the layer.
	 *
	 * @return the layer
	 */
	public String getLayer() {
		return get(LAYER);
	}

	/**
	 * Sets the layer.
	 *
	 * @param layer the new layer
	 */
	public void setLayer(String layer) {
		set(LAYER, layer);
	}

	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	public void setTitle(String title) {
		set(LAYER_TITLE, title);
		// this.title = title;
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return get(LAYER_TITLE);
		// return title;
	}

	/**
	 * Sets the wms service url.
	 *
	 * @param wmsService the new wms service url
	 */
	public void setWmsServiceUrl(String wmsService) {
		set(WMS_SERVICE_URL, wmsService);
	}

	/**
	 * Sets the WMS request.
	 *
	 * @param requestWms the new WMS request
	 */
	public void setWMSRequest(String requestWms) {
		set(FULL_REQUEST_WMS, requestWms);
	}

	/**
	 * Gets the geoserver url.
	 *
	 * @return the geoserverUrl
	 */
	public String getGeoserverUrl() {
		return get(GEOSERVER_URL);
	}

	/**
	 * Sets the geoserver url.
	 *
	 * @param geoserverUrl            the geoserverUrl to set
	 */
	public void setGeoserverUrl(String geoserverUrl) {
		set(GEOSERVER_URL, geoserverUrl);
	}

	/**
	 * Sets the bounds map.
	 *
	 * @param boundsMap the new bounds map
	 */
	public void setBoundsMap(BoundsMap boundsMap) {
		this.boundsMap = boundsMap;
	}

	/**
	 * Gets the bounds map.
	 *
	 * @return the boundsMap
	 */
	public BoundsMap getBoundsMap() {
		return boundsMap;
	}

	/**
	 * Gets the uuid.
	 *
	 * @return the uuid
	 */
	public String getUuid() {
		return get(LAYER_UUID);
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return get("description");
	}

	// NEW

	/**
	 * Gets the topic category.
	 *
	 * @return the topic category
	 */
	public String getTopicCategory() {
		return get(TOPIC_CATEGORY);
	}

	/**
	 * Gets the publication date.
	 *
	 * @return the publication date
	 */
	public Date getPublicationDate() {
		return get(PUBLICATION_DATE);
	}

	/**
	 * Gets the scope code.
	 *
	 * @return the scope code
	 */
	public String getScopeCode() {
		return get(SCOPE_CODE);
	}

	/**
	 * Gets the WMS request.
	 *
	 * @return the WMS request
	 */
	public String getWMSRequest() {
		return get(FULL_REQUEST_WMS);
	}

	/**
	 * Gets the wms service url.
	 *
	 * @return the wms service url
	 */
	public String getWmsServiceUrl() {
		return get(WMS_SERVICE_URL);
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
	 * Sets the styles.
	 *
	 * @param styles the new styles
	 */
	@Deprecated
	public void setStyles(List<String> styles) {
		this.styles = styles;
	}

	/**
	 * Gets the version wms.
	 *
	 * @return the version wms
	 */
	public String getVersionWMS() {
		return versionWMS;
	}

	/**
	 * Sets the version wms.
	 *
	 * @param versioWMS the new version wms
	 */
	public void setVersionWMS(String versioWMS) {
		this.versionWMS = versioWMS;
	}

	/**
	 * Sets the crs wms.
	 *
	 * @param crs the new crs wms
	 */
	public void setCrsWMS(String crs) {
		this.crs = crs;

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
	 * Puts ellipses in input strings that are longer than than maxCharacters. Shorter strings or
	 * null is returned unchanged.
	 *
	 * @param input the input string that may be subjected to shortening
	 * @param maxCharacters the maximum characters that are acceptable for the unshortended string. Must be at least 3, otherwise a string with ellipses is too long already.
	 * @param charactersAfterEllipsis the characters after ellipsis
	 * @return the string
	 * @throws Exception the exception
	 */
	public String ellipsize(String input, int maxCharacters, int charactersAfterEllipsis) throws Exception{
	  if(maxCharacters < 3) {
	    throw new IllegalArgumentException("maxCharacters must be at least 3 because the ellipsis already take up 3 characters");
	  }
	  if(maxCharacters - 3 > charactersAfterEllipsis) {
	    throw new IllegalArgumentException("charactersAfterEllipsis must be less than maxCharacters");
	  }
	  if (input == null || input.length() < maxCharacters) {
	    return input;
	  }
	  return input.substring(0, maxCharacters - 3 - charactersAfterEllipsis) + "..." + input.substring(input.length() - charactersAfterEllipsis);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LayerItem [boundsMap=");
		builder.append(boundsMap);
		builder.append(", styles=");
		builder.append(styles);
		builder.append(", versionWMS=");
		builder.append(versionWMS);
		builder.append(", crs=");
		builder.append(crs);
		builder.append(", getKeywords()=");
		builder.append(getKeywords());
		builder.append(", getName()=");
		builder.append(getName());
		builder.append(", isInternalLayer()=");
		builder.append(isInternalLayer());
		builder.append(", getLayer()=");
		builder.append(getLayer());
		builder.append(", getTitle()=");
		builder.append(getTitle());
		builder.append(", getGeoserverUrl()=");
		builder.append(getGeoserverUrl());
		builder.append(", getBoundsMap()=");
		builder.append(getBoundsMap());
		builder.append(", getUuid()=");
		builder.append(getUuid());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append(", getTopicCategory()=");
		builder.append(getTopicCategory());
		builder.append(", getPublicationDate()=");
		builder.append(getPublicationDate());
		builder.append(", getScopeCode()=");
		builder.append(getScopeCode());
		builder.append(", getWMSRequest()=");
		builder.append(getWMSRequest());
		builder.append(", getWmsServiceUrl()=");
		builder.append(getWmsServiceUrl());
		builder.append(", getStyles()=");
		builder.append(getStyles());
		builder.append(", getVersionWMS()=");
		builder.append(getVersionWMS());
		builder.append(", getCrs()=");
		builder.append(getCrs());
		builder.append("]");
		return builder.toString();
	}
}

/**
 *
 */
package org.gcube.datatransfer.resolver.gis.entity;

import java.util.Date;
import java.util.List;


/**
 * The Class GisLayerItem.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 16, 2017
 */
public class GisLayerItem {

	private String uuid;
	private String citationTitle;
	private String layerName;
	private String topicCategory;
	private Date publicationDate;
	private String scopeCode;
	private String geoserverBaseUrlOnLineResource;
	private String baseWmsServiceUrl;
	private String fullWmsUrlRequest;
	private List<String> styles;
	private String metaAbstract;
	private List<String> listKeywords;
	private String versionWMS;
	private String setCrsWMS;


	/**
	 * Instantiates a new gis layer item.
	 *
	 * @param uuid the uuid
	 * @param citationTitle the citation title
	 * @param layerName the layer name
	 * @param baseWmsServiceUrl the base wms service url
	 * @param fullWmsUrlRequest the full wms url request
	 */
	public GisLayerItem(String uuid, String citationTitle, String layerName, String baseWmsServiceUrl, String fullWmsUrlRequest) {
		this.uuid = uuid;
		this.citationTitle = citationTitle;
		this.layerName = layerName;
		this.baseWmsServiceUrl = baseWmsServiceUrl;
		this.fullWmsUrlRequest = fullWmsUrlRequest;
	}

	/**
	 * Instantiates a new gis layer item.
	 *
	 * @param uuid the uuid
	 * @param citationTitle the citation title
	 * @param layerName the layer name
	 * @param topicCategory the topic category
	 * @param publicationDate the publication date
	 * @param scopeCode the scope code
	 * @param geoserverBaseUrlOnlineResource the geoserver base url online resource
	 * @param baseWmsServiceUrl the base wms service url
	 * @param fullWmsUrlRequest the full wms url request
	 * @param styles the styles
	 * @param metaAbstract the meta abstract
	 * @param listKeywords the list keywords
	 */
	public GisLayerItem(String uuid, String citationTitle, String layerName,
			String topicCategory, Date publicationDate, String scopeCode,
			String geoserverBaseUrlOnlineResource, String baseWmsServiceUrl,
			String fullWmsUrlRequest, List<String> styles,
			String metaAbstract, List<String> listKeywords) {
		this.uuid = uuid;
		this.citationTitle = citationTitle;
		this.layerName = layerName;
		this.topicCategory = topicCategory;
		this.publicationDate = publicationDate;
		this.scopeCode = scopeCode;
		this.geoserverBaseUrlOnLineResource = geoserverBaseUrlOnlineResource;
		this.baseWmsServiceUrl = baseWmsServiceUrl;
		this.fullWmsUrlRequest = fullWmsUrlRequest;
		this.styles = styles;
		this.metaAbstract = metaAbstract;
		this.listKeywords = listKeywords;
	}

	/**
	 * Sets the version wms.
	 *
	 * @param versionWms the new version wms
	 */
	public void setVersionWMS(String versionWms) {
		this.versionWMS = versionWms;

	}

	/**
	 * Sets the crs wms.
	 *
	 * @param crs the new crs wms
	 */
	public void setCrsWMS(String crs) {
		this.setCrsWMS =crs;
	}

	/**
	 * Gets the uuid.
	 *
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * Gets the citation title.
	 *
	 * @return the citation title
	 */
	public String getCitationTitle() {
		return citationTitle;
	}

	/**
	 * Gets the layer name.
	 *
	 * @return the layer name
	 */
	public String getLayerName() {
		return layerName;
	}

	/**
	 * Gets the topic category.
	 *
	 * @return the topic category
	 */
	public String getTopicCategory() {
		return topicCategory;
	}

	/**
	 * Gets the publication date.
	 *
	 * @return the publication date
	 */
	public Date getPublicationDate() {
		return publicationDate;
	}

	/**
	 * Gets the scope code.
	 *
	 * @return the scope code
	 */
	public String getScopeCode() {
		return scopeCode;
	}

	/**
	 * Gets the geoserver base url on line resource.
	 *
	 * @return the geoserver base url on line resource
	 */
	public String getGeoserverBaseUrlOnLineResource() {
		return geoserverBaseUrlOnLineResource;
	}

	/**
	 * Gets the base wms service url.
	 *
	 * @return the base wms service url
	 */
	public String getBaseWmsServiceUrl() {
		return baseWmsServiceUrl;
	}

	/**
	 * Gets the full wms url request.
	 *
	 * @return the full wms url request
	 */
	public String getFullWmsUrlRequest() {
		return fullWmsUrlRequest;
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
	 * Gets the meta abstract.
	 *
	 * @return the meta abstract
	 */
	public String getMetaAbstract() {
		return metaAbstract;
	}

	/**
	 * Gets the list keywords.
	 *
	 * @return the list keywords
	 */
	public List<String> getListKeywords() {
		return listKeywords;
	}

	/**
	 * Sets the uuid.
	 *
	 * @param uuid the new uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * Sets the citation title.
	 *
	 * @param citationTitle the new citation title
	 */
	public void setCitationTitle(String citationTitle) {
		this.citationTitle = citationTitle;
	}

	/**
	 * Sets the layer name.
	 *
	 * @param layerName the new layer name
	 */
	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	/**
	 * Sets the topic category.
	 *
	 * @param topicCategory the new topic category
	 */
	public void setTopicCategory(String topicCategory) {
		this.topicCategory = topicCategory;
	}

	/**
	 * Sets the publication date.
	 *
	 * @param publicationDate the new publication date
	 */
	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	/**
	 * Sets the scope code.
	 *
	 * @param scopeCode the new scope code
	 */
	public void setScopeCode(String scopeCode) {
		this.scopeCode = scopeCode;
	}

	/**
	 * Sets the geoserver base url on line resource.
	 *
	 * @param geoserverBaseUrlOnLineResource the new geoserver base url on line resource
	 */
	public void setGeoserverBaseUrlOnLineResource(
			String geoserverBaseUrlOnLineResource) {
		this.geoserverBaseUrlOnLineResource = geoserverBaseUrlOnLineResource;
	}

	/**
	 * Sets the base wms service url.
	 *
	 * @param baseWmsServiceUrl the new base wms service url
	 */
	public void setBaseWmsServiceUrl(String baseWmsServiceUrl) {
		this.baseWmsServiceUrl = baseWmsServiceUrl;
	}

	/**
	 * Sets the full wms url request.
	 *
	 * @param fullWmsUrlRequest the new full wms url request
	 */
	public void setFullWmsUrlRequest(String fullWmsUrlRequest) {
		this.fullWmsUrlRequest = fullWmsUrlRequest;
	}

	/**
	 * Sets the styles.
	 *
	 * @param styles the new styles
	 */
	public void setStyles(List<String> styles) {
		this.styles = styles;
	}

	/**
	 * Sets the meta abstract.
	 *
	 * @param metaAbstract the new meta abstract
	 */
	public void setMetaAbstract(String metaAbstract) {
		this.metaAbstract = metaAbstract;
	}

	/**
	 * Sets the list keywords.
	 *
	 * @param listKeywords the new list keywords
	 */
	public void setListKeywords(List<String> listKeywords) {
		this.listKeywords = listKeywords;
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
	 * Gets the sets the crs wms.
	 *
	 * @return the sets the crs wms
	 */
	public String getSetCrsWMS() {
		return setCrsWMS;
	}

	/**
	 * Sets the sets the crs wms.
	 *
	 * @param setCrsWMS the new sets the crs wms
	 */
	public void setSetCrsWMS(String setCrsWMS) {
		this.setCrsWMS = setCrsWMS;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LayerItem [uuid=");
		builder.append(uuid);
		builder.append(", citationTitle=");
		builder.append(citationTitle);
		builder.append(", layerName=");
		builder.append(layerName);
		builder.append(", topicCategory=");
		builder.append(topicCategory);
		builder.append(", publicationDate=");
		builder.append(publicationDate);
		builder.append(", scopeCode=");
		builder.append(scopeCode);
		builder.append(", geoserverBaseUrlOnLineResource=");
		builder.append(geoserverBaseUrlOnLineResource);
		builder.append(", baseWmsServiceUrl=");
		builder.append(baseWmsServiceUrl);
		builder.append(", fullWmsUrlRequest=");
		builder.append(fullWmsUrlRequest);
		builder.append(", styles=");
		builder.append(styles);
		builder.append(", metaAbstract=");
		builder.append(metaAbstract);
		builder.append(", listKeywords=");
		builder.append(listKeywords);
		builder.append(", versionWMS=");
		builder.append(versionWMS);
		builder.append(", setCrsWMS=");
		builder.append(setCrsWMS);
		builder.append("]");
		return builder.toString();
	}



}

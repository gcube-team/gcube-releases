/**
 * 
 */
package org.gcube.datatransfer.resolver.gis.entity;

import java.util.Date;
import java.util.List;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Oct 7, 2014
 *
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
	 * @param uuid
	 * @param citationTitle
	 * @param layerName
	 * @param topicCategory
	 * @param publicationDate
	 * @param scopeCode
	 * @param geoserverBaseUrlOnlineResource
	 * @param baseWmsServiceUrl
	 * @param fullWmsUrlRequest
	 * @param b
	 * @param styles
	 * @param metaAbstract
	 * @param listKeywords
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
	 * @param versionWms
	 */
	public void setVersionWMS(String versionWms) {
		this.versionWMS = versionWms;
		
	}

	/**
	 * @param crs
	 */
	public void setCrsWMS(String crs) {
		this.setCrsWMS =crs;
	}

	public String getUuid() {
		return uuid;
	}

	public String getCitationTitle() {
		return citationTitle;
	}

	public String getLayerName() {
		return layerName;
	}

	public String getTopicCategory() {
		return topicCategory;
	}

	public Date getPublicationDate() {
		return publicationDate;
	}

	public String getScopeCode() {
		return scopeCode;
	}

	public String getGeoserverBaseUrlOnLineResource() {
		return geoserverBaseUrlOnLineResource;
	}

	public String getBaseWmsServiceUrl() {
		return baseWmsServiceUrl;
	}

	public String getFullWmsUrlRequest() {
		return fullWmsUrlRequest;
	}

	public List<String> getStyles() {
		return styles;
	}

	public String getMetaAbstract() {
		return metaAbstract;
	}

	public List<String> getListKeywords() {
		return listKeywords;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setCitationTitle(String citationTitle) {
		this.citationTitle = citationTitle;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public void setTopicCategory(String topicCategory) {
		this.topicCategory = topicCategory;
	}

	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	public void setScopeCode(String scopeCode) {
		this.scopeCode = scopeCode;
	}

	public void setGeoserverBaseUrlOnLineResource(
			String geoserverBaseUrlOnLineResource) {
		this.geoserverBaseUrlOnLineResource = geoserverBaseUrlOnLineResource;
	}

	public void setBaseWmsServiceUrl(String baseWmsServiceUrl) {
		this.baseWmsServiceUrl = baseWmsServiceUrl;
	}

	public void setFullWmsUrlRequest(String fullWmsUrlRequest) {
		this.fullWmsUrlRequest = fullWmsUrlRequest;
	}

	public void setStyles(List<String> styles) {
		this.styles = styles;
	}

	public void setMetaAbstract(String metaAbstract) {
		this.metaAbstract = metaAbstract;
	}

	public void setListKeywords(List<String> listKeywords) {
		this.listKeywords = listKeywords;
	}

	public String getVersionWMS() {
		return versionWMS;
	}

	public String getSetCrsWMS() {
		return setCrsWMS;
	}

	public void setSetCrsWMS(String setCrsWMS) {
		this.setCrsWMS = setCrsWMS;
	}

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

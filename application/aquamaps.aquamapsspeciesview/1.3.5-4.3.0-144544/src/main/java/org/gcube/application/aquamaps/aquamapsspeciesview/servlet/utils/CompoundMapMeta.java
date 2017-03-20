package org.gcube.application.aquamaps.aquamapsspeciesview.servlet.utils;

import java.util.Date;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("AquaMapMeta")
public class CompoundMapMeta {
	
	@XStreamAlias("Map_Title")
	private String title;
	@XStreamAlias("Author")	
	private String author;
	@XStreamAlias("Map_Type")
	private String mapType;
	@XStreamAlias("Static_Images_Count")
	private Integer imageCount;
	@XStreamAlias("GIS_Enabled")
	private Boolean gis;
	@XStreamAlias("Customized_Envelopes")
	private Boolean custom;
	
	@XStreamAlias("Creation_Time")
	private Date creationDate;	
	@XStreamAlias("HSPEC_Source_ID")
	private Integer resourceId;
	@XStreamAlias("HSPEC_Generation_Algorithm")
	private String algorithm;
	@XStreamAlias("HSPEC_Generation_Time")
	private Date dataGenerationTime;
	
	@XStreamAlias("GeoServer_URL")
	private String layerUrl;
	@XStreamAlias("Layer_ID")
	private String layerId;
	
	@XStreamAlias("Species_Coverage")	
	private List<String> speciesList;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @return the mapType
	 */
	public String getMapType() {
		return mapType;
	}

	/**
	 * @param mapType the mapType to set
	 */
	public void setMapType(String mapType) {
		this.mapType = mapType;
	}

	/**
	 * @return the imageCount
	 */
	public Integer getImageCount() {
		return imageCount;
	}

	/**
	 * @param imageCount the imageCount to set
	 */
	public void setImageCount(Integer imageCount) {
		this.imageCount = imageCount;
	}

	/**
	 * @return the gis
	 */
	public Boolean getGis() {
		return gis;
	}

	/**
	 * @param gis the gis to set
	 */
	public void setGis(Boolean gis) {
		this.gis = gis;
	}

	/**
	 * @return the custom
	 */
	public Boolean getCustom() {
		return custom;
	}

	/**
	 * @param custom the custom to set
	 */
	public void setCustom(Boolean custom) {
		this.custom = custom;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the resourceId
	 */
	public Integer getResourceId() {
		return resourceId;
	}

	/**
	 * @param resourceId the resourceId to set
	 */
	public void setResourceId(Integer resourceId) {
		this.resourceId = resourceId;
	}

	/**
	 * @return the algorithm
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * @param algorithm the algorithm to set
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * @return the dataGenerationTime
	 */
	public Date getDataGenerationTime() {
		return dataGenerationTime;
	}

	/**
	 * @param dataGenerationTime the dataGenerationTime to set
	 */
	public void setDataGenerationTime(Date dataGenerationTime) {
		this.dataGenerationTime = dataGenerationTime;
	}

	/**
	 * @return the layerUrl
	 */
	public String getLayerUrl() {
		return layerUrl;
	}

	/**
	 * @param layerUrl the layerUrl to set
	 */
	public void setLayerUrl(String layerUrl) {
		this.layerUrl = layerUrl;
	}

	/**
	 * @return the layerId
	 */
	public String getLayerId() {
		return layerId;
	}

	/**
	 * @param layerId the layerId to set
	 */
	public void setLayerId(String layerId) {
		this.layerId = layerId;
	}

	/**
	 * @return the speciesList
	 */
	public List<String> getSpeciesList() {
		return speciesList;
	}

	/**
	 * @param speciesList the speciesList to set
	 */
	public void setSpeciesList(List<String> speciesList) {
		this.speciesList = speciesList;
	}

	
	
}

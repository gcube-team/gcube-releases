package org.gcube.common.geoserverinterface.bean;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface;
import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeonetworkCategory;

public class MetadataInfo implements Serializable {
	
	private static final long serialVersionUID = 5417552348382837941L;
	//Mandatory
	/**
	 * @uml.property name="name"
	 */
	private String name = "";
	
	/**
	 * @uml.property name="title"
	 */
	private String title = "";
	private String fileIdentifier = "";
	private String url = "";
	//End Mandatory

	private GeonetworkCategory category = GeonetworkCategory.ANY; //Default
	
	private String westBoundLongitude = "-180";
	private String eastBoundLongitude = "180";
	private String southBoundLongitude = "-90";
	private String northBoundLongitude = "90";
	private String language = "eng";
	private String abst = "";
	private String categoryCode = "Biota"; // TODO change in enum

	/**
	 * @uml.property name="nativeCRS"
	 */
	private String nativeCRS = "";
	/**
	 * @uml.property name="srs"
	 */
	
	private String description = "";
	
	private String srs = "";
	/**
	 * @uml.property name="nativeBoundingBox"
	 * @uml.associationEnd
	 */
//	private BoundsRest nativeBoundingBox;
//	/**
//	 * @uml.property name="latLonBoundingBox"
//	 * @uml.associationEnd
//	 */
//	private BoundsRest latLonBoundingBox;
//	/**
//	 * @uml.property name="projectionPolicy"
//	 */
	private String projectionPolicy = "";
	/**
	 * @uml.property name="maxFeatures"
	 */
	private int maxFeatures = 0;
	/**
	 * @uml.property name="numDecimals"
	 */
	private int numDecimals = 0;
	/**
	 * @uml.property name="enabled"
	 */
	private boolean enabled = false;
	/**
	 * @uml.property name="metadata"
	 * @uml.associationEnd qualifier="key:java.lang.String java.lang.String"
	 */
	private Map<String, String> metadata = new HashMap<String, String>();
	/**
	 * @uml.property name="workspace"
	 */
	private String workspace = "";
	/**
	 * @uml.property name="datastore"
	 */
	private String datastore = "";

	/**
	 * @return
	 * @uml.property name="workspace"
	 */
	public String getWorkspace() {
		return workspace;
	}

	/**
	 * @param workspace
	 * @uml.property name="workspace"
	 */
	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	/**
	 * @return
	 * @uml.property name="datastore"
	 */
	public String getDatastore() {
		return datastore;
	}

	/**
	 * @param datastore
	 * @uml.property name="datastore"
	 */
	public void setDatastore(String datastore) {
		this.datastore = datastore;
	}

	/**
	 * @return
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 * @uml.property name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return
	 * @uml.property name="title"
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 * @uml.property name="title"
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return
	 * @uml.property name="nativeCRS"
	 */
	public String getNativeCRS() {
		return nativeCRS;
	}

	/**
	 * @param nativeCRS
	 * @uml.property name="nativeCRS"
	 */
	public void setNativeCRS(String nativeCRS) {
		this.nativeCRS = nativeCRS;
	}

	/**
	 * @return
	 * @uml.property name="srs"
	 */
	public String getSrs() {
		return srs;
	}

	/**
	 * @param srs
	 * @uml.property name="srs"
	 */
	public void setSrs(String srs) {
		this.srs = srs;
	}

//	/**
//	 * @return
//	 * @uml.property name="nativeBoundingBox"
//	 */
//	public BoundsRest getNativeBoundingBox() {
//		return nativeBoundingBox;
//	}
//
//	/**
//	 * @param nativeBoundingBox
//	 * @uml.property name="nativeBoundingBox"
//	 */
//	public void setNativeBoundingBox(BoundsRest nativeBoundingBox) {
//		this.nativeBoundingBox = nativeBoundingBox;
//	}

//	/**
//	 * @return
//	 * @uml.property name="latLonBoundingBox"
//	 */
//	public BoundsRest getLatLonBoundingBox() {
//		return latLonBoundingBox;
//	}
//
//	/**
//	 * @param latLonBoundingBox
//	 * @uml.property name="latLonBoundingBox"
//	 */
//	public void setLatLonBoundingBox(BoundsRest latLonBoundingBox) {
//		this.latLonBoundingBox = latLonBoundingBox;
//	}

	/**
	 * @return
	 * @uml.property name="projectionPolicy"
	 */
	public String getProjectionPolicy() {
		return projectionPolicy;
	}

	/**
	 * @param projectionPolicy
	 * @uml.property name="projectionPolicy"
	 */
	public void setProjectionPolicy(String projectionPolicy) {
		this.projectionPolicy = projectionPolicy;
	}

	/**
	 * @return
	 * @uml.property name="maxFeatures"
	 */
	public int getMaxFeatures() {
		return maxFeatures;
	}

	/**
	 * @param maxFeatures
	 * @uml.property name="maxFeatures"
	 */
	public void setMaxFeatures(int maxFeatures) {
		this.maxFeatures = maxFeatures;
	}

	/**
	 * @return
	 * @uml.property name="numDecimals"
	 */
	public int getNumDecimals() {
		return numDecimals;
	}

	/**
	 * @param i
	 * @uml.property name="numDecimals"
	 */
	public void setNumDecimals(int i) {
		this.numDecimals = i;
	}

	/**
	 * @return
	 * @uml.property name="enabled"
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled
	 * @uml.property name="enabled"
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public void setMetadata(String key, String value) {
		this.metadata.put(key, value);
	}

	public void setFileIdentifier(String fileIdentifier) {
		this.fileIdentifier = fileIdentifier;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setAbst(String abst) {
		this.abst = abst;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public void setWestBoundLongitude(String westBoundLongitude) {
		this.westBoundLongitude = westBoundLongitude;
	}

	public void setEastBoundLongitude(String eastBoundLongitude) {
		this.eastBoundLongitude = eastBoundLongitude;
	}

	public void setSouthBoundLongitude(String southBoundLongitude) {
		this.southBoundLongitude = southBoundLongitude;
	}

	public void setNorthBoundLongitude(String northBoundLongitude) {
		this.northBoundLongitude = northBoundLongitude;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFileIdentifier() {
		return fileIdentifier;
	}

	public String getUrl() {
		return url;
	}

	public String getAbst() {
		return abst;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public String getWestBoundLongitude() {
		return westBoundLongitude;
	}

	public String getEastBoundLongitude() {
		return eastBoundLongitude;
	}

	public String getSouthBoundLongitude() {
		return southBoundLongitude;
	}

	public String getNorthBoundLongitude() {
		return northBoundLongitude;
	}

	public String getLanguage() {
		return language;
	}

	public String getDescription() {
		return description;
	}

	public GeonetworkCategory getCategory() {
		return category;
	}

	public void setCategory(GeonetworkCategory category) {
		this.category = category;
	}
}

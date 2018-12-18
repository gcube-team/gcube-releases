package org.gcube.common.geoserverinterface.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FeatureTypeRest implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 684504866121223653L;
	/**
	 * @uml.property  name="name"
	 */
	private String name = "";
	/**
	 * @uml.property  name="nativeName"
	 */
	private String nativeName = "";
	/**
	 * @uml.property  name="title"
	 */
	private String title = "";
	/**
	 * @uml.property  name="nativeCRS"
	 */
	private String nativeCRS = "";
	/**
	 * @uml.property  name="srs"
	 */
	private String srs = "";
	/**
	 * @uml.property  name="nativeBoundingBox"
	 * @uml.associationEnd  
	 */
	private BoundsRest nativeBoundingBox;
	/**
	 * @uml.property  name="latLonBoundingBox"
	 * @uml.associationEnd  
	 */
	private BoundsRest latLonBoundingBox;
	/**
	 * @uml.property  name="projectionPolicy"
	 */
	private String projectionPolicy = ""; 
	/**
	 * @uml.property  name="maxFeatures"
	 */
	private int maxFeatures = 0;
	/**
	 * @uml.property  name="numDecimals"
	 */
	private int numDecimals = 0;
	/**
	 * @uml.property  name="enabled"
	 */
	private boolean enabled = false;
	/**
	 * @uml.property  name="metadata"
	 * @uml.associationEnd  qualifier="key:java.lang.String java.lang.String"
	 */
	private Map<String, String> metadata = new HashMap<String, String>();
	/**
	 * @uml.property  name="workspace"
	 */
	private String workspace = "";
	/**
	 * @uml.property  name="datastore"
	 */
	private String datastore = "";
	
	/**
	 * @return
	 * @uml.property  name="workspace"
	 */
	public String getWorkspace() {
		return workspace;
	}
	/**
	 * @param workspace
	 * @uml.property  name="workspace"
	 */
	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}
	/**
	 * @return
	 * @uml.property  name="datastore"
	 */
	public String getDatastore() {
		return datastore;
	}
	/**
	 * @param datastore
	 * @uml.property  name="datastore"
	 */
	public void setDatastore(String datastore) {
		this.datastore = datastore;
	}
	/**
	 * @return
	 * @uml.property  name="name"
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name
	 * @uml.property  name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return
	 * @uml.property  name="nativeName"
	 */
	public String getNativeName() {
		return nativeName;
	}
	/**
	 * @param nativeName
	 * @uml.property  name="nativeName"
	 */
	public void setNativeName(String nativeName) {
		this.nativeName = nativeName;
	}
	/**
	 * @return
	 * @uml.property  name="title"
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title
	 * @uml.property  name="title"
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return
	 * @uml.property  name="nativeCRS"
	 */
	public String getNativeCRS() {
		return nativeCRS;
	}
	/**
	 * @param nativeCRS
	 * @uml.property  name="nativeCRS"
	 */
	public void setNativeCRS(String nativeCRS) {
		this.nativeCRS = nativeCRS;
	}
	/**
	 * @return
	 * @uml.property  name="srs"
	 */
	public String getSrs() {
		return srs;
	}
	/**
	 * @param srs
	 * @uml.property  name="srs"
	 */
	public void setSrs(String srs) {
		this.srs = srs;
	}
	/**
	 * @return
	 * @uml.property  name="nativeBoundingBox"
	 */
	public BoundsRest getNativeBoundingBox() {
		return nativeBoundingBox;
	}
	/**
	 * @param nativeBoundingBox
	 * @uml.property  name="nativeBoundingBox"
	 */
	public void setNativeBoundingBox(BoundsRest nativeBoundingBox) {
		this.nativeBoundingBox = nativeBoundingBox;
	}
	/**
	 * @return
	 * @uml.property  name="latLonBoundingBox"
	 */
	public BoundsRest getLatLonBoundingBox() {
		return latLonBoundingBox;
	}
	/**
	 * @param latLonBoundingBox
	 * @uml.property  name="latLonBoundingBox"
	 */
	public void setLatLonBoundingBox(BoundsRest latLonBoundingBox) {
		this.latLonBoundingBox = latLonBoundingBox;
	}
	/**
	 * @return
	 * @uml.property  name="projectionPolicy"
	 */
	public String getProjectionPolicy() {
		return projectionPolicy;
	}
	/**
	 * @param projectionPolicy
	 * @uml.property  name="projectionPolicy"
	 */
	public void setProjectionPolicy(String projectionPolicy) {
		this.projectionPolicy = projectionPolicy;
	}
	/**
	 * @return
	 * @uml.property  name="maxFeatures"
	 */
	public int getMaxFeatures() {
		return maxFeatures;
	}
	/**
	 * @param maxFeatures
	 * @uml.property  name="maxFeatures"
	 */
	public void setMaxFeatures(int maxFeatures) {
		this.maxFeatures = maxFeatures;
	}
	/**
	 * @return
	 * @uml.property  name="numDecimals"
	 */
	public int getNumDecimals() {
		return numDecimals;
	}
	/**
	 * @param i
	 * @uml.property  name="numDecimals"
	 */
	public void setNumDecimals(int i) {
		this.numDecimals = i;
	}
	/**
	 * @return
	 * @uml.property  name="enabled"
	 */
	public boolean isEnabled() {
		return enabled;
	}
	/**
	 * @param enabled
	 * @uml.property  name="enabled"
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
}

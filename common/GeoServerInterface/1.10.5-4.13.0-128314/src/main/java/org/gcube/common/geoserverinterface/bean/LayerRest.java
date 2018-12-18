package org.gcube.common.geoserverinterface.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class LayerRest implements Serializable{
	
	private static final long serialVersionUID = -8461452424579459383L;
	/**
	 * @uml.property  name="name"
	 */
	private String name = "";
	/**
	 * @uml.property  name="type"
	 */
	private String type = "";
	/**
	 * @uml.property  name="defaultStyle"
	 */
	private String defaultStyle = "";
	/**
	 * @uml.property  name="styles"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
	 */
	private ArrayList<String> styles = new ArrayList<String>();
	/**
	 * @uml.property  name="enabled"
	 */
	private boolean enabled = false;
	/**
	 * @uml.property  name="resource"
	 */
	private String resource = "";
	/**
	 * @uml.property  name="resourceName"
	 */
	private String resourceName = "";
	/**
	 * @uml.property  name="featureTypeLink"
	 */
	private String featureTypeLink = "";
	/**
	 * @uml.property  name="datastore"
	 */
	private String datastore = "";
	/**
	 * @uml.property  name="workspace"
	 */
	private String workspace = "";
	/**
	 * @uml.property  name="coveragestore"
	 */
	private String coveragestore = "";
	
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
	public LayerRest() {
		super();
		// TODO Auto-generated constructor stub
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
	 * @uml.property  name="type"
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type
	 * @uml.property  name="type"
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return
	 * @uml.property  name="defaultStyle"
	 */
	public String getDefaultStyle() {
		return defaultStyle;
	}
	/**
	 * @param defaultStyle
	 * @uml.property  name="defaultStyle"
	 */
	public void setDefaultStyle(String defaultStyle) {
		this.defaultStyle = defaultStyle;
	}
	public ArrayList<String> getStyles() {
		return styles;
	}
	public void setStyles(ArrayList<String> styles) {
		this.styles = styles;
	}
	public void addStyle(String style) {
		this.styles.add(style);
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
	/**
	 * @return
	 * @uml.property  name="resource"
	 */
	public String getResource() {
		return resource;
	}
	/**
	 * @param resource
	 * @uml.property  name="resource"
	 */
	public void setResource(String resource) {
		this.resource = resource;
	}
	/**
	 * @return
	 * @uml.property  name="resourceName"
	 */
	public String getResourceName() {
		return resourceName;
	}
	/**
	 * @param resourceName
	 * @uml.property  name="resourceName"
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	/**
	 * @param featureTypeLink
	 * @uml.property  name="featureTypeLink"
	 */
	public void setFeatureTypeLink(String featureTypeLink) {
		this.featureTypeLink = featureTypeLink;
	}
	/**
	 * @return
	 * @uml.property  name="featureTypeLink"
	 */
	public String getFeatureTypeLink() {
		return featureTypeLink;
	}
	/**
	 * @param coveragestore
	 * @uml.property  name="coveragestore"
	 */
	public void setCoveragestore(String coveragestore) {
		this.coveragestore = coveragestore;
	}
	/**
	 * @return
	 * @uml.property  name="coveragestore"
	 */
	public String getCoveragestore() {
		return coveragestore;
	}
}

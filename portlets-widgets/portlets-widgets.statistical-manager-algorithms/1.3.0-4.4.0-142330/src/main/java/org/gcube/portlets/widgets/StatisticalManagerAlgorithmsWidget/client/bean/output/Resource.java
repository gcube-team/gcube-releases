/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.output;

import java.io.Serializable;

/**
 * @author ceras
 *
 */
public class Resource implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1417885805472591661L;

	public enum ResourceType {OBJECT, FILE, TABULAR, MAP, IMAGES};
	
	private String resourceId, description, name;
	private ResourceType resourceType;
	
	/**
	 * 
	 */
	public Resource() {
		super();
	}
	
	/**
	 * @param resourceId
	 * @param description
	 * @param name
	 * @param resourceType
	 */
	public Resource(String resourceId, String description, String name, ResourceType resourceType) {
		super();
		this.resourceId = resourceId;
		this.description = description;
		this.name = name;
		this.resourceType = resourceType;
	}

	/**
	 * @return the resourceId
	 */
	public String getResourceId() {
		return resourceId;
	}

	/**
	 * @param resourceId the resourceId to set
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the resourceType
	 */
	public ResourceType getResourceType() {
		return resourceType;
	}

	/**
	 * @param resourceType the resourceType to set
	 */
	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}
	
	public boolean isTabular() {
		return this.resourceType == ResourceType.TABULAR;
	}
	
	public boolean isObject() {
		return this.resourceType == ResourceType.OBJECT;
	}
	
	public boolean isFile() {
		return this.resourceType == ResourceType.FILE;
	}
	
	public boolean isMap() {
		return this.resourceType == ResourceType.MAP;
	}
	
	public boolean isImages() {
		return this.resourceType == ResourceType.IMAGES;
	}
	
}

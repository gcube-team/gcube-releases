package org.gcube.portlets.admin.vredefinition.shared;

import java.io.Serializable;
import java.util.List;

/**
 * A functionality class element.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
@SuppressWarnings("serial")
public class Functionality implements Serializable{
	
	private List<Functionality> subFunctionalities; // For Base Functionalities: Authorization..
	private int id;
	private List<ResourceCategory> resourceCategories; // For Occurrence and Taxonomic (under BiolCube): Data Discovery/BrazilianFlora for instance
	private String description;
	private String name;
	private boolean selected; // if this object has subFunctionalities and it is selected, they are automatically checked, but 
							  // resources must be manually checked.
	
	/**
	 * needed for serialization
	 */
	public Functionality() {
		super();
	}
	
	/**
	 * @param subFunctionalities
	 * @param description
	 * @param name
	 */
	public Functionality(int id, String description, String name) {
		super();
		this.id = id;
		this.description = description;
		this.name = name;
	}
	
	public Functionality(int id, String name, String description, boolean selected) {
		super();
		this.id = id;
		this.description = description;
		this.name = name;
		this.selected = selected;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public List<Functionality> getSubFunctionalities() {
		return subFunctionalities;
	}
	public void setSubFunctionalities(List<Functionality> subFunctionalities) {
		this.subFunctionalities = subFunctionalities;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ResourceCategory> getResources() {
		return resourceCategories;
	}
	public void setResources(List<ResourceCategory> resourceCategories) {
		this.resourceCategories = resourceCategories;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	@Override
	public String toString() {
		return "Functionality [subFunctionalities=" + subFunctionalities
				+ ", id=" + id + ", resources=" + resourceCategories + ", description="
				+ description + ", name=" + name + ", selected=" + selected
				+ "]";
	}
}

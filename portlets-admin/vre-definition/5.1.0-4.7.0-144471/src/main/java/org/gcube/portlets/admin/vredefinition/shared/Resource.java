package org.gcube.portlets.admin.vredefinition.shared;

import java.io.Serializable;

/**
 * A resource class element.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 *
 */
@SuppressWarnings("serial")
public class Resource implements Serializable{

	private String id;
	private String name;
	private String description;
	private boolean selected;
	
	/**
	 * needed for serialization
	 */
	public Resource() {
		super();
	}
	
	/**
	 * @param id
	 * @param name
	 * @param description
	 * @param selected
	 */
	public Resource(String id, String name, String description, boolean selected) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.selected = selected;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "Resource [id=" + id + ", name=" + name + ", description="
				+ description + ", selected=" + selected + "]";
	}
}

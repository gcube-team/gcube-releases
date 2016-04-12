package org.gcube.portlets.admin.vredefinition.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ResourceCategoryItem implements Serializable {
	
	private String id;
	private String name;
	private String description;
	private boolean isSelected;

	public ResourceCategoryItem() {
		super();
	}

	public ResourceCategoryItem(String id, String name, String description,
			boolean isSelected) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.isSelected = isSelected;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	

}

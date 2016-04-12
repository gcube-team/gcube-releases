package org.gcube.portlets.admin.vredefinition.shared;

import com.extjs.gxt.ui.client.data.BaseModel;

@SuppressWarnings("serial")
public class ExternalResourceModel extends BaseModel {

	public ExternalResourceModel() {		
	}
	
	public ExternalResourceModel(String id, String name, String description, boolean isSelected, String categoryName, String categoryId) {
		set("id", id);
		set("name", name);
		set("description", description);
		set("category", categoryName);
		set("categoryId", categoryId);
		set("isSelected", isSelected);
	}
	

	public String getId() {
		return (String) get("id");
	}

	public String getName() {
		return (String) get("name");
	}

	public String getDescription() {
		return (String) get("description");
	}

	public String getCategoryName() {
		return (String) get("category");
	}
	
	public String getCategoryId() {
		return (String) get("categoryId");
	}

	public boolean isSelected() {
		return ((Boolean) get("isSelected")).booleanValue();
	}
}

package org.gcube.portlets.admin.vredefinition.shared;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModel;

public class VRECollectionBean extends BaseModel {
	private static final long serialVersionUID = 1L; 
	
	public VRECollectionBean() {
	}
	
	public VRECollectionBean(String id, String name, String description, int numberOfMembers, Date creationTime, Date lastUpdateTime, boolean isSelected) {
		
		set("name", name);
		set("description", description);
		set("numberOfMembers", numberOfMembers);
		set("creationTime", creationTime);
		set("lastUpdateTime", lastUpdateTime);
		set("isSelected", isSelected);
		set("id", id);

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

	public int getNumberOfMembers() {
		return ((Integer) get("numberOfMembers")).intValue();
	}

	public Date getCreationTime() {
		return (Date) get("creationTime");
	}


	public Date getLastUpdateTime() {
		return (Date) get("lastUpdateTime");
	}

	public boolean isSelected() {
		return ((Boolean) get("isSelected")).booleanValue();
	}
}

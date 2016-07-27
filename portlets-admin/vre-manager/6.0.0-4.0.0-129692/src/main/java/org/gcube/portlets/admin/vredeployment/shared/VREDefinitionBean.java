package org.gcube.portlets.admin.vredeployment.shared;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModel;

@SuppressWarnings("serial")
public class VREDefinitionBean extends BaseModel {

	public VREDefinitionBean() {
	}
	
	
	public VREDefinitionBean(String id, String name, String description, String designer,
			String manager, Date startingDate, Date endingDate, String status, String summary) {
		set("id", id);
		set("name", name);
		set("description", description);
		set("designer", designer);
		set("manager", manager);
		set("startingDate", startingDate);
		set("endingDate", endingDate);
		set("status", status);
		set("summary", summary);		
	}
	
	public String getId() { return (String) get("id");	}

	public String getName() {return (String) get("name");	}
	
	public String getDescription() {return (String) get("description");	}
	
	public String getDesigner() {return (String) get("designer");	}
	
	public String getManager() {return (String) get("manager");	}
	
	public Date getStartingDate() {	return (Date) get("startingDate");	}
	
	public Date getEndingDate() {	return (Date) get("endingDate");	}	

	public String getStatus() {	return (String) get("status");	}
	
	public String getSummary() {	return (String) get("summary");	}
}

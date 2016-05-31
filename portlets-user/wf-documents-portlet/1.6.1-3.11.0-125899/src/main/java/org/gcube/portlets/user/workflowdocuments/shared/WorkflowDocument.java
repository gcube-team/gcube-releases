package org.gcube.portlets.user.workflowdocuments.shared;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModel;


@SuppressWarnings("serial")
public class WorkflowDocument extends BaseModel {

	public WorkflowDocument() {
	}

	public WorkflowDocument(String id, String name, String status, String statusDesc, String workflowid, 
			String curRole, String lastAction, Date lastDate, Date dateCreated, boolean hasComments, 
			boolean view, boolean update, boolean delete, boolean addcomments, boolean editcomments, boolean deletecomments, boolean isLocked, String lockedBy, String lockExpiration, boolean isOwner) {
		set("id", id);
		set("name", name);
		set("status", status);
		set("statusDesc", statusDesc);
		set("workflowid", workflowid);
		set("curRole", curRole);
		set("lastAction", lastAction);
		set("lastDate", lastDate);	
		set("dateCreated", dateCreated);	
		set("hasComments", hasComments);
		set("view", view);
		set("update", update);
		set("delete", delete);
		set("addcomments", addcomments);
		set("editcomments", editcomments);
		set("deletecomments", deletecomments);
		set("isLocked", isLocked);
		set("lockedBy", lockedBy);
		set("lockExpiration", lockExpiration);
		set("isOwner", isOwner);
	}
	
	public String getId() { return (String) get("id");	}

	public String getName() {return (String) get("name");	}

	public String getStatus() {	return (String) get("status");	}
	
	public String getStatusDesc() {	return (String) get("statusDesc");	}
	
	public String getWorkflowId() {	return (String) get("workflowid");	}
	
	public String getCurRole() {return (String) get("curRole");	}
	
	public String getLastAction() {	return (String) get("lastAction");	}
	
	public Date getLastDate() {	return (Date) get("lastDate");	}
	
	public Date getCreatedDate() {	return (Date) get("dateCreated");	}
	
	public boolean hasComments() {	return (Boolean) get("hasComments"); }
	
	public boolean hasView() {	return (Boolean) get("view");	}
	
	public boolean hasUpdate() { return (Boolean) get("update");	}
	
	public boolean hasDelete() {return (Boolean) get("delete"); 	}	
	
	public boolean hasAddComments() { return (Boolean) get("addcomments"); 	}
	
	public boolean hasUpdateComments() { return (Boolean) get("editcomments");	}
	
	public boolean hasDeleteComments() { return (Boolean) get("deletecomments");	}	
	
	public boolean isLocked() { return (Boolean) get("isLocked");	}	
	
	public String getLockedBy() {	return (String) get("lockedBy");	}
	
	public String getLockExpiration() {	return (String) get("lockExpiration");	}
	
	public boolean isOwner() { return (Boolean) get("isOwner");	}	
}

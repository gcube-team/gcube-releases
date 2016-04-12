package org.gcube.portlets.admin.wfdocviewer.shared;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModel;


@SuppressWarnings("serial")
public class WfDocumentBean extends BaseModel {

	public WfDocumentBean() {
	}

	public WfDocumentBean(String id, String displayName, String status, String homelibraryid, Date datecreated, Date lastChangeTime, String lastAction) {
		set("id", id);
		set("displayName", displayName);
		set("status", status);
		set("homelibraryid", homelibraryid);
		set("datecreated", datecreated);
		set("lastChangeTime", lastChangeTime);
		set("lastAction", lastAction);
		
	}

	public String getId() {
		return (String) get("id");
	}

	public String getName() {
		return (String) get("displayName");
	}

	public String getStatus() {
		return (String) get("status");
	}
	
	public String getHomeLibraryId() {
		return (String) get("homelibraryid");
	}
	public Date getDatecreated() {
		return (Date) get("datecreated");
	}
	public Date getlastChangeTime() {
		return (Date) get("lastChangeTime");
	}
	public String getLastAction() {
		return (String) get("lastAction");
	}
}

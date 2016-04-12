package org.gcube.portlets.admin.wfdocviewer.shared;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModel;

@SuppressWarnings("serial")
public class ActionLogBean extends BaseModel {
	
	public ActionLogBean(){
	}
	
	public ActionLogBean(String workflowid, Date date, String author, String actiontype) {
		set("workflowid", workflowid);
		set("date", date);
		set("author", author);
		set("actiontype", actiontype);
	}
	
	public String getWorkflowid() {
		return (String) get("workflowid");
	}

	public Date getDate() {
		return (Date) get("date");
	}

	public String getAuthor() {
		return (String) get("author");
	}

	public String getAction() {
		return (String) get("actiontype");
	}
}

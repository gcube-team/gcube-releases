package org.gcube.portlets.admin.wfdocslibrary.shared;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class LogAction implements Serializable {

	private String workflowid;
	private Date date;
	private String author;
	private String actiontype;
	
	public LogAction() {}

	public LogAction(String workflowid, Date date, String author,
			String actiontype) {
		super();
		this.workflowid = workflowid;
		this.date = date;
		this.author = author;
		this.actiontype = actiontype;
	}

	public String getWorkflowid() {	return workflowid;
	}
	public void setWorkflowid(String workflowid) {	this.workflowid = workflowid;
	}
	public Date getDate() {	return date;
	}
	public void setDate(Date date) { this.date = date;
	}
	public String getAuthor() {	return author;
	}
	public void setAuthor(String author) {	this.author = author;
	}
	public String getActiontype() {	return actiontype;
	}
	public void setActiontype(String actiontype) {	this.actiontype = actiontype;
	}
}

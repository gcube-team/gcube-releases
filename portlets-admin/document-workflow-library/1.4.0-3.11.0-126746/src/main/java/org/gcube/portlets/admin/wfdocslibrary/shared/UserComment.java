package org.gcube.portlets.admin.wfdocslibrary.shared;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class UserComment implements Serializable {

	private String workflowid;
	private Date date;
	private String author;
	private String comment;
	
	public UserComment() {	}

	public UserComment(String workflowid, Date date, String author,
			String comment) {
		super();
		this.workflowid = workflowid;
		this.date = date;
		this.author = author;
		this.comment = comment;
	}

	public String getWorkflowid() {
		return workflowid;
	}

	public void setWorkflowid(String workflowid) {
		this.workflowid = workflowid;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}

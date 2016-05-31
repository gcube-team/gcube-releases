package org.gcube.portlets.admin.wfdocslibrary.shared;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class ActionChange implements Serializable {

	private String actionid;
	private Date date;
	private String author;
	private String sectionChangeType;
	private int sectionId;
	private String componentType;
	private int componentId;
	private String previousContent;
	
	
	public ActionChange() {	}


	public ActionChange(String actionid, Date date, String author,
			String sectionChangeType, int sectionId, String componentType,
			int componentId, String previousContent) {
		super();
		this.actionid = actionid;
		this.date = date;
		this.author = author;
		this.sectionChangeType = sectionChangeType;
		this.sectionId = sectionId;
		this.componentType = componentType;
		this.componentId = componentId;
		this.previousContent = previousContent;
	}


	public String getActionid() { return actionid;
	}
	public void setActionid(String actionid) {	this.actionid = actionid;
	}
	public Date getDate() {	return date;
	}
	public void setDate(Date date) {	this.date = date;
	}
	public String getAuthor() {	return author;
	}
	public void setAuthor(String author) {	this.author = author;
	}
	public String getSectionChangeType() {	return sectionChangeType;
	}
	public void setSectionChangeType(String sectionChangeType) { this.sectionChangeType = sectionChangeType;
	}
	public int getSectionId() {	return sectionId;
	}
	public void setSectionId(int sectionId) {	this.sectionId = sectionId;
	}
	public String getComponentType() {	return componentType;
	}
	public void setComponentType(String componentType) {	this.componentType = componentType;
	}
	public int getComponentId() {	return componentId;
	}
	public void setComponentId(int componentId) {	this.componentId = componentId;
	}
	public String getPreviousContent() {return previousContent;
	}
	public void setPreviousContent(String previousContent) {	this.previousContent = previousContent;
	}		
}

package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TaskInfoUIElement implements IsSerializable{
	
	private Date date;
	
	private String message;
	
	private  String level;
	
	public TaskInfoUIElement() {}
	
	public TaskInfoUIElement(String level, String message, Date date) {
		this.message = message;
		this.level = level;
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	public String getLevel() {
		return level;
	}

	public Date getDate() {
		return date;
	}
}

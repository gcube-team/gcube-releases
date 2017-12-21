package org.gcube.portlets.admin.vredeployer.shared;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("serial")
public class VREDescrBean implements Serializable {

	private String name;

	private String description;

	private String designer;

	private String manager;

	private Date startTime;

	private Date endTime;

	public VREDescrBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public VREDescrBean(String name, String description, String designer,
			String manager, Date startTime, Date endTime) {
		super();
		this.name = name;
		this.description = description;
		this.designer = designer;
		this.manager = manager;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDesigner() {
		return designer;
	}

	public void setDesigner(String designer) {
		this.designer = designer;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}


}

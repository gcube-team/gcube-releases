package org.gcube.portlets.user.td.monitorwidget.client.details.tree;

import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class MonitorFolderDto extends MonitorBaseDto {

	private static final long serialVersionUID = 4644048540524701598L;
	protected String description;
	protected ArrayList<MonitorBaseDto> childrens;
	protected String type;
	protected String state;
	protected String humanReadableStatus;
	protected float progress;
	protected String startTime;
	protected String endTime;
	
	public MonitorFolderDto(){
		
	}
	
	public MonitorFolderDto(String type,String id,String description, String state, 
			String humanReadableStatus, float progress,  ArrayList<MonitorBaseDto> childrens){
		super(id);
		this.type=type;
		this.description=description;
		this.state=state;
		this.humanReadableStatus=humanReadableStatus;
		this.progress=progress;
		this.childrens=childrens;
		
	}
	
	
	
	public ArrayList<MonitorBaseDto> getChildrens() {
		return childrens;
	}

	public void setChildrens(ArrayList<MonitorBaseDto> childrens) {
		this.childrens = childrens;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getProgress() {
		return progress;
	}

	public void setProgress(float progress) {
		this.progress = progress;
	}
	

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	

	public String getHumanReadableStatus() {
		return humanReadableStatus;
	}

	public void setHumanReadableStatus(String humanReadableStatus) {
		this.humanReadableStatus = humanReadableStatus;
	}
	
	

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		return description;
	}

}

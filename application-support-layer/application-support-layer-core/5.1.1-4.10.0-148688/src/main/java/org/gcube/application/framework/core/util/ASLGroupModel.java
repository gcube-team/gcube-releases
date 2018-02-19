package org.gcube.application.framework.core.util;

public class ASLGroupModel {
	
	long groupId;
	String name; 
	String description;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ASLGroupModel() {
		
	}
	
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	
	public void setGroupName(String groupName) {
		this.name = groupName;
	}
	
	public long getGroupId() {
		return groupId;
	}
	
	public String getGroupName() {
		return name;
	}

}

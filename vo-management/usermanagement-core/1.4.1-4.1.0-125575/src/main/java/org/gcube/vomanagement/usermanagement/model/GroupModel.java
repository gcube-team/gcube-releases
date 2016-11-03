package org.gcube.vomanagement.usermanagement.model;


public class GroupModel {
	
	String groupId;
	String parentGroupId;
	String groupName;
	String description;
	long logoId;

	public GroupModel() {
		this("","","","",0L);
	}
	
	public GroupModel(String groupId,String parentGroupId, String groupName, String description, long logoId){
		this.groupId = groupId;
		this.parentGroupId = parentGroupId;
		this.groupName = groupName;
		this.description = description;
		this.logoId = logoId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getParentGroupId() {
		return parentGroupId;
	}

	public void setParentGroupId(String parentGroupId) {
		this.parentGroupId = parentGroupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getLogoId() {
		return logoId;
	}

	public void setLogoId(long logoId) {
		this.logoId = logoId;
	}


	
}

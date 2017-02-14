package it.eng.rdlab.soa3.um.rest.bean;

/**
 * This class models group information
 *  
 * @author Ermanno Travaglino
 * @version 1.0
 *
 */
public class GroupModel {
	
	private String groupId;
	private String groupName;
	private String description;
	
	
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
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

}

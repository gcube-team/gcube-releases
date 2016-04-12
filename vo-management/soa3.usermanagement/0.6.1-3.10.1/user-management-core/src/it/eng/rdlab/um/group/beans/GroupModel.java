package it.eng.rdlab.um.group.beans;

import it.eng.rdlab.um.beans.GenericModel;


public class GroupModel extends GenericModel 
{
	
	private String parentGroupId;
	private String groupName;
	private String description;

	public GroupModel() 
	{
		super ();
		this.parentGroupId = "";
		this.groupName = "";
		this.description = "";
	}
	
	public GroupModel(String groupId,String parentGroupId, String groupName, String description)
	{
		super (groupId);
		this.parentGroupId = parentGroupId;
		this.groupName = groupName;
		this.description = description;
	}
	
	public GroupModel(GroupModel groupModel)
	{
		super (groupModel);
		this.parentGroupId = groupModel.getParentGroupId();
		this.groupName = groupModel.getGroupName();
		this.description = groupModel.getDescription();
	}
	

	public String getGroupId() 
	{
		String groupId = super.getId();
		return groupId != null ? groupId : "";
	}

	public void setGroupId(String groupId) 
	{
		setId(groupId);
	}

	public String getParentGroupId() 
	{
		return parentGroupId;
	}

	public void setParentGroupId(String parentGroupId) 
	{
		this.parentGroupId = parentGroupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) 
	{
		this.groupName = groupName;
	}


	public String getDescription() 
	{
		return description;
	}

	public void setDescription(String description) 
	{
		this.description = description;
	}



	
}

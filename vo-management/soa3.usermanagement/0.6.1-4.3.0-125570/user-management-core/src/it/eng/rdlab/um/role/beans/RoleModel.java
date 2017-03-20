package it.eng.rdlab.um.role.beans;

import it.eng.rdlab.um.beans.GenericModel;


public class RoleModel extends GenericModel 
{

	private String roleName;
	private String description;

	public RoleModel() 
	{
		super ();
		this.roleName = "";
		this.description = "";
	}

	public RoleModel(String roleId, String roleName, String description)
	{
		super (roleId);
		this.roleName = roleName;
		this.description = description;
	}

	public RoleModel(RoleModel roleModel)
	{
		super (roleModel);
		this.roleName = roleModel.getRoleName();
		this.description = roleModel.getDescription();
	}

	public String getRoleId() 
	{
		String roleId = super.getId();
		return roleId != null ? roleId : "";
	}

	public void setRoleId(String roleId) 
	{
		setId(roleId);
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) 
	{
		this.roleName = roleName;
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

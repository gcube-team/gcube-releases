package org.gcube.vomanagement.usermanagement.model;

public class RoleModel {

	String roleName;
	String roleId;
	String description;
	String completeName;
	

	public RoleModel(){
		
	}
	public RoleModel(String roleName,String roleId, String description){
		this.roleName = roleName;
		this.roleId = roleId;
		this.description = description;
	}
	
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCompleteName() {
		return completeName;
	}
	public void setCompleteName(String completeName) {
		this.completeName = completeName;
	}

	
	
	
}

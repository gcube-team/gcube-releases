package it.eng.rdlab.soa3.um.rest.bean;

/**
 * This class models role information
 * 
 * @author Ermanno Travaglino
 * @version 1.0
 * 
 */
public class RoleModel {
	private String roleId;
	private String roleName;
	private String description;
	private String organization;
	
	
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	
	
	
	
	

}

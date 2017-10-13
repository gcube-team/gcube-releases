package org.gcube.vomanagement.usermanagement.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class GCubeRole implements Serializable {
	private long roleId;
	private	String roleName;	
	private	String description;
	
	public final static String VRE_MANAGER_LABEL = "VRE-Manager";
	public final static String VRE_DESIGNER_LABEL = "VRE-Designer";
	public final static String VO_ADMIN_LABEL = "VRE-Designer";
	public final static String INFRA_MANAGER_LABEL = "Infrastructure-Manager";
	public final static String DATA_MANAGER_LABEL = "Data-Manager";
		
	public GCubeRole(){
		
	}
	public GCubeRole(long roleId, String roleName, String description){
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
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "GCubeRole [roleName=" + roleName + ", roleId=" + roleId
				+ ", description=" + description + "]";
	}

	
}

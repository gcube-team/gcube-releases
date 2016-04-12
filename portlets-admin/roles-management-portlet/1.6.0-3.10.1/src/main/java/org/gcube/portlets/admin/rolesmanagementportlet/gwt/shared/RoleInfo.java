package org.gcube.portlets.admin.rolesmanagementportlet.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RoleInfo implements IsSerializable{

	private String roleName;
	
	private String roleDescription;
	
	public RoleInfo() {};
	
	public RoleInfo(String name, String description) {
		this.roleName = name;
		this.roleDescription = description;
	}

	public String getRoleName() {
		return roleName;
	}

	public String getRoleDescription() {
		return roleDescription;
	}
}

package org.gcube.portlets.admin.policydefinition.services.restful;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="roles")
public class UserMgmtRolesBean {
	
	private List<String> roles;
	
	public UserMgmtRolesBean() {
	}
	
	public UserMgmtRolesBean(List<String> roles){
		this.roles = roles;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}


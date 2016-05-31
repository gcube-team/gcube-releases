package org.gcube.common.authorization.library;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthorizationEntry {

	private String userName;
	private List<String> roles;
	private String scope;
	private List<BannedService> bannedServices = new ArrayList<BannedService>();
	
	protected AuthorizationEntry(){}
	
	public AuthorizationEntry(String userName, List<String> roles, String scope) {
		super();
		this.userName = userName;
		this.roles = roles;
		this.scope = scope;
	}
	
	public AuthorizationEntry(String userName, List<String> roles, String scope, List<BannedService> bannedServices) {
		this(userName, roles, scope);
		this.bannedServices = bannedServices;
	}

	public String getUserName() {
		return userName;
	}

	public List<String> getRoles() {
		return roles;
	}

	public String getScope() {
		return scope;
	}
	
	public List<BannedService> getBannedServices() {
		return bannedServices;
	}

	public void setBannedServices(List<BannedService> bannedServices) {
		this.bannedServices = bannedServices;
	}

	@Override
	public String toString() {
		return "AuthorizationEntry [userName=" + userName + ", roles=" + roles
				+ ", scope=" + scope + " bannedServices "+ bannedServices+"]";
	}
	
	
	
}

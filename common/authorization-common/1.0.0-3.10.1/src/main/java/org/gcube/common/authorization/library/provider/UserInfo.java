package org.gcube.common.authorization.library.provider;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.authorization.library.BannedService;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserInfo {

	private String userName;
	private List<String> roles;
	private List<BannedService> bannedServices;
	
	protected UserInfo(){}
	
	public UserInfo(String userName, List<String> roles, List<BannedService> bannedServices) {
		super();
		this.userName = userName;
		this.roles = roles;
		this.bannedServices = bannedServices;
	}
	
	public String getUserName() {
		return userName;
	}
	public List<String> getRoles() {
		return roles;
	}
	
	public List<BannedService> getBannedServices() {
		return bannedServices;
	}

	public boolean isTokenBannedForService(BannedService service){
		return (bannedServices.contains(service));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		return result;
	}
		
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserInfo other = (UserInfo) obj;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserInfo [userName=" + userName + ", roles=" + roles + "]";
	}

	
	
}

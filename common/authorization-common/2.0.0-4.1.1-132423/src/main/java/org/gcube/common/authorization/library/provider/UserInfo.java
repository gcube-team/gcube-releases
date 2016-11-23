package org.gcube.common.authorization.library.provider;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserInfo extends ClientInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String clientId;
	private List<String> roles = new ArrayList<String>();
	
	protected UserInfo(){}
	
	public UserInfo(String clientId, List<String> roles) {
		super();
		this.clientId = clientId;
		this.roles = roles;
	}
	

	@Override
	public String getId() {
		return clientId;
	}

	@Override
	public List<String> getRoles() {
		return roles;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((clientId == null) ? 0 : clientId.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
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
		if (clientId == null) {
			if (other.clientId != null)
				return false;
		} else if (!clientId.equals(other.clientId))
			return false;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserInfo [clientId=" + clientId + ", roles=" + roles + "]";
	}
		
}

/**
 * 
 */
package org.cotrix.gcube.stubs;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author "Federico De Faveri federico.defaveri@fao.org"
 *
 */
@XmlRootElement
public class PortalUser {
	
	@XmlElement
	private String username;
	
	@XmlElement
	private String fullname;
	
	@XmlElement
	private String email;
	
	@XmlElement
	private Set<String> roles;

	@SuppressWarnings("unused")  //JAXB only
	private PortalUser(){
		roles = new HashSet<String>();
	}
	
	public PortalUser(String name, String fullName, String email, Collection<String> roles) {
		this.username=name;
		this.fullname=fullName;
		this.email=email;
		this.roles = new HashSet<String>(roles);
	}

	public String fullName() {
		return fullname;
	}

	public String userName() {
		return username;
	}

	public String email() {
		return email;
	}

	public Collection<String> roles() {
		return roles;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [username=");
		builder.append(username);
		builder.append(", fullname=");
		builder.append(fullname);
		builder.append(", email=");
		builder.append(email);
		builder.append(", roles=");
		builder.append(roles);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((fullname == null) ? 0 : fullname.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		PortalUser other = (PortalUser) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (fullname == null) {
			if (other.fullname != null)
				return false;
		} else if (!fullname.equals(other.fullname))
			return false;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
	
}

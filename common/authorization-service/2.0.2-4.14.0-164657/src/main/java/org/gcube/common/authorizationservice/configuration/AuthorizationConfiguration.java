package org.gcube.common.authorizationservice.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthorizationConfiguration {

	@XmlElement(name="Rule")
	private List<AuthorizationRule> authorizationRules = new ArrayList<AuthorizationRule>();
	
	@XmlElement(name="AllowedContainerIp")
	private List<String> allowedContainerIps = new ArrayList<String>();
	
	protected AuthorizationConfiguration(){}
	
	public List<AuthorizationRule> getAuthorizationRules() {
		return Collections.unmodifiableList(authorizationRules);
	}

	protected void setAuthorizationRules(List<AuthorizationRule> rules) {
		this.authorizationRules = rules;
	}

	public List<String> getAllowedContainerIps() {
		return allowedContainerIps;
	}

	protected void setAllowedContainerIps(
			List<String> allowedContainerIps) {
		this.allowedContainerIps = allowedContainerIps;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((allowedContainerIps == null) ? 0 : allowedContainerIps
						.hashCode());
		result = prime
				* result
				+ ((authorizationRules == null) ? 0 : authorizationRules
						.hashCode());
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
		AuthorizationConfiguration other = (AuthorizationConfiguration) obj;
		if (allowedContainerIps == null) {
			if (other.allowedContainerIps != null)
				return false;
		} else if (!allowedContainerIps.equals(other.allowedContainerIps))
			return false;
		if (authorizationRules == null) {
			if (other.authorizationRules != null)
				return false;
		} else if (!authorizationRules.equals(other.authorizationRules))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AuthorizationConfiguration [authorizationRules="
				+ authorizationRules + ", allowedContainerIps="
				+ allowedContainerIps + "]";
	}

	

}

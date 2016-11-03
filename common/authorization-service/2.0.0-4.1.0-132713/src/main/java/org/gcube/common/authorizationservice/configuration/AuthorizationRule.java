package org.gcube.common.authorizationservice.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Rule")
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthorizationRule {

	
	@XmlAttribute(name="path")
	private String servletPath;
	
	@XmlAttribute(name="requiresToken")
	private boolean requiresToken = true;
	
	@XmlElement(name="Entity")
	private List<AllowedEntity> entities= new ArrayList<AllowedEntity>();

	protected AuthorizationRule(){}
	
	public AuthorizationRule(String servletPath, List<AllowedEntity> entities, boolean requiresToken) {
		super();
		this.servletPath = servletPath;
		this.entities = entities;
		this.requiresToken = requiresToken;
	}

	public String getServletPath() {
		return servletPath;
	}

	public List<AllowedEntity> getEntities() {
		return entities;
	}
	
	
	public boolean isTokenRequired() {
		return requiresToken;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((entities == null) ? 0 : entities.hashCode());
		result = prime * result
				+ ((servletPath == null) ? 0 : servletPath.hashCode());
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
		AuthorizationRule other = (AuthorizationRule) obj;
		if (entities == null) {
			if (other.entities != null)
				return false;
		} else if (!entities.equals(other.entities))
			return false;
		if (servletPath == null) {
			if (other.servletPath != null)
				return false;
		} else if (!servletPath.equals(other.servletPath))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AuthorizationRule [servletPath=" + servletPath + ", entities="
				+ entities + " requiresToken= "+requiresToken+"]";
	}

	
	
}

package org.gcube.common.authorization.library.enpoints;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="authorization-endpoint")
@XmlAccessorType(XmlAccessType.FIELD)
public final class AuthorizationEndpoint implements Comparable<AuthorizationEndpoint>{

	@XmlAttribute
	private int priority = 0;

	@XmlAttribute
	private String infrastructure;
	
	@XmlAttribute(name="cacheValidityInMillis") 
	long clientCacheValidity = 10*60*1000; //10 minutes
			
	@XmlElement
	private String host;
	
	@XmlElement
	private int port;
	
	@XmlElement(name="secure")
	private boolean secureConnection = false;
	
	protected AuthorizationEndpoint() {}

	public AuthorizationEndpoint(String infrastructure, int priority, String host, int port) {
		super();
		this.infrastructure = infrastructure;
		this.host = host;
		this.priority = priority;
		this.port = port;
	}
		
	protected String getInfrastructure() {
		return infrastructure;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
	
	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public long getClientCacheValidity() {
		return clientCacheValidity;
	}
	
	public boolean isSecureConnection() {
		return secureConnection;
	}
	
	public void setSecureConnection(boolean secureConnection) {
		this.secureConnection = secureConnection;
	}

	@Override
	public int compareTo(AuthorizationEndpoint o) {
		return this.priority-o.priority;
	}

	@Override
	public String toString() {
		return "AuthorizationEndpoint [infrastructure= "+infrastructure+" priority=" + priority + ", host=" + host
				+ ", port=" + port + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result
				+ ((infrastructure == null) ? 0 : infrastructure.hashCode());
		result = prime * result + port;
		result = prime * result + priority;
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
		AuthorizationEndpoint other = (AuthorizationEndpoint) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (infrastructure == null) {
			if (other.infrastructure != null)
				return false;
		} else if (!infrastructure.equals(other.infrastructure))
			return false;
		if (port != other.port)
			return false;
		if (priority != other.priority)
			return false;
		return true;
	}
			
}

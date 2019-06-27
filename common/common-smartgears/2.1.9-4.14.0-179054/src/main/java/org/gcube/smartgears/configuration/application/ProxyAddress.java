package org.gcube.smartgears.configuration.application;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.validator.annotations.NotNull;

@XmlRootElement(name="proxy")
public class ProxyAddress {


	@XmlAttribute
	String protocol = "http";
		
	@XmlElement
	@NotNull
	String hostname;
	
	@XmlElement
	Integer port;

	public String hostname() {
		return hostname;
	}

	public ProxyAddress hostname(String hostname) {
		this.hostname = hostname;
		return this;
	}

	public Integer port() {
		return port;
	}

	public ProxyAddress port(int port) {
		this.port = port;
		return this;
	}

	public String protocol() {
		return protocol;
	}

	public ProxyAddress protocol(String protocol) {
		this.protocol = protocol;
		return this;
	}
		
	
	@Override
	public String toString() {
		return "ProxyAddress [protocol=" + protocol + ", hostname=" + hostname + ", port=" + port + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
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
		ProxyAddress other = (ProxyAddress) obj;
		if (hostname == null) {
			if (other.hostname != null)
				return false;
		} else if (!hostname.equals(other.hostname))
			return false;
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!port.equals(other.port))
			return false;
		if (protocol == null) {
			if (other.protocol != null)
				return false;
		} else if (!protocol.equals(other.protocol))
			return false;
		return true;
	}

	
	
	
	
}

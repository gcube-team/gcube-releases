package org.gcube.smartgears.configuration.container;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.validator.annotations.NotNull;

@XmlRootElement(name="proxy")
public class ProxyAddress {
	
	@XmlAttribute
	boolean secure = false;
	
	@XmlElement
	@NotNull
	String hostname;
	
	@XmlElement
	@NotNull
	int port;

	public String hostname() {
		return hostname;
	}

	public ProxyAddress hostname(String hostname) {
		this.hostname = hostname;
		return this;
	}

	public int port() {
		return port;
	}

	public ProxyAddress port(int port) {
		this.port = port;
		return this;
	}

	public boolean secure() {
		return secure;
	}

	public ProxyAddress secure(boolean secure) {
		this.secure = secure;
		return this;
	}
		
	
	
}

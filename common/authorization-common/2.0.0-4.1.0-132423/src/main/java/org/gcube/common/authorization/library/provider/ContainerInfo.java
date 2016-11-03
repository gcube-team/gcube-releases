package org.gcube.common.authorization.library.provider;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ContainerInfo extends ClientInfo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7940935464438497662L;
	
	private String host;
	private int port;
	
	protected ContainerInfo() {}
	
	public ContainerInfo(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	@Override
	public String getId() {
		return String.format("%s:%s", host, port);
	}

	@Override
	public List<String> getRoles() {
		return Collections.emptyList();
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	
	
}

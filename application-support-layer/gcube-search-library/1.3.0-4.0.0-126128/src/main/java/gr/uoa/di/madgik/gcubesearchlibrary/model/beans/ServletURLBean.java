package gr.uoa.di.madgik.gcubesearchlibrary.model.beans;

import java.io.Serializable;

public class ServletURLBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6796273986484915424L;

	private String protocol = null;
	
	private String host = null;
	
	private String port = null;
	

	public ServletURLBean(String protocol, String host, String port) {
		this.protocol = protocol.trim();
		this.host = host.trim();
		this.port = port.trim();
	}


	public String getProtocol() {
		return protocol;
	}


	public void setProtocol(String protocol) {
		this.protocol = protocol.trim();
	}


	public String getHost() {
		return host;
	}


	public void setHost(String host) {
		this.host = host.trim();
	}


	public String getPort() {
		return port;
	}


	public void setPort(String port) {
		this.port = port.trim();
	}
	
}

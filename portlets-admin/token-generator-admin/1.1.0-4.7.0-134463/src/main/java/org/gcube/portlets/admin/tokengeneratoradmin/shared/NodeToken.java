package org.gcube.portlets.admin.tokengeneratoradmin.shared;

import java.io.Serializable;

/**
 * A node token bean.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public class NodeToken implements Serializable {
	
	private static final long serialVersionUID = -5900548112460825894L;
	private String token;
	private String context;
	private String nodeIp;
	private int port;
	private String error;

	public NodeToken() {
		super();
	}

	/**
	 * @param token
	 * @param context
	 * @param nodeIp
	 * @param port
	 */
	public NodeToken(String token, String context, String nodeIp, int port) {
		super();
		this.token = token;
		this.context = context;
		this.nodeIp = nodeIp;
		this.port = port;
	}
	
	/**
	 * @param token
	 * @param context
	 * @param nodeIp
	 * @param port
	 */
	public NodeToken(String error) {
		super();
		this.error = error;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getNodeIp() {
		return nodeIp;
	}

	public void setNodeIp(String nodeIp) {
		this.nodeIp = nodeIp;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "NodeToken [token=" + token + ", context=" + context
				+ ", nodeIp=" + nodeIp + ", port=" + port + ", error=" + error
				+ "]";
	}
}

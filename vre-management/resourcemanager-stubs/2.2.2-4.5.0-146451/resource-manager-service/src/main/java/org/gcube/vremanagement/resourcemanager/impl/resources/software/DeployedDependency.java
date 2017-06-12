package org.gcube.vremanagement.resourcemanager.impl.resources.software;

/**
 * A package deployed by a Deployer service
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class DeployedDependency extends Dependency {
	
	protected String host = "";
	
	protected String status = "";
	
	protected String message = "";

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}

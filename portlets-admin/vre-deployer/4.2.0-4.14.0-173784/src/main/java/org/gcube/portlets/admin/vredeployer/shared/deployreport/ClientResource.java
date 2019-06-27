package org.gcube.portlets.admin.vredeployer.shared.deployreport;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * 
 * @author massi
 *
 */
public class ClientResource implements IsSerializable {
	/**
	 * 
	 */
	private String resourceid;
	/**
	 * 
	 */
	private String resourceType;
	/**
	 * 
	 */
	private DeployStatus status;
	/**
	 * 
	 */
	public ClientResource() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 * @param resourceid - 
	 * @param resourceType -
	 * @param status -
	 */
	public ClientResource(String resourceid, String resourceType,
			DeployStatus status) {
		super();
		this.resourceid = resourceid;
		this.resourceType = resourceType;
		this.status = status;
	}
	/**
	 * 
	 * @return -
	 */
	public String getResourceid() {
		return resourceid;
	}
	/**
	 * 
	 * @param resourceid- 
	 */
	public void setResourceid(String resourceid) {
		this.resourceid = resourceid;
	}
	/**
	 * 
	 * @return -
	 */
	public String getResourceType() {
		return resourceType;
	}
	/**
	 * 
	 * @param resourceType -
	 */
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	/**
	 * 
	 * @return -
	 */
	public DeployStatus getStatus() {
		return status;
	}
	/**
	 * 
	 * @param status -
	 */
	public void setStatus(DeployStatus status) {
		this.status = status;
	}
	
}

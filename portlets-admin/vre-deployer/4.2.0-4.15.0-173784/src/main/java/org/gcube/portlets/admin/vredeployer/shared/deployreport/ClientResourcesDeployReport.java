package org.gcube.portlets.admin.vredeployer.shared.deployreport;

import java.io.Serializable;
import java.util.List;

public class ClientResourcesDeployReport implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private DeployStatus status;
	
	/**
	 * 
	 */
	private List<ClientResource> resources;
	/**
	 * 
	 */
	public ClientResourcesDeployReport() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 * @param globalstatus -
	 * @param resources - 
	 */
	public ClientResourcesDeployReport(DeployStatus globalstatus, List<ClientResource> resources) {
		super();
		this.status = globalstatus;
		this.resources = resources;
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
	 * @param globalstatus -
	 */
	public void setStatus(DeployStatus globalstatus) {
		this.status = globalstatus;
	}
	/**
	 * 
	 * @return -
	 */
	public List<ClientResource> getResources() {
		return resources;
	}
	/**
	 * 
	 * @param resources -
	 */
	public void setResources(List<ClientResource> resources) {
		this.resources = resources;
	}
	
}

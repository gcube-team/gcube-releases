package org.gcube.portlets.admin.vredeployer.shared.deployreport;

import java.io.Serializable;

public class ClientFunctionalityReport implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String id;
	/**
	 * 
	 */
	private String name;
	/**
	 * 
	 */
	private DeployStatus status;
	public ClientFunctionalityReport() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 * @param id
	 * @param name
	 * @param status
	 */
	public ClientFunctionalityReport(int id, String name, DeployStatus status) {
		super();
		this.id = id+"";
		this.name = name;
		this.status = status;
	}
	/**
	 * 
	 * @return id
	 */
	public String getId() {
		return id;
	}
	/**
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}
	/**
	 * 
	 * @param name a name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 
	 * @return
	 */
	public DeployStatus getStatus() {
		return status;
	}
	/**
	 * 
	 * @param status
	 */
	public void setStatus(DeployStatus status) {
		this.status = status;
	}
}

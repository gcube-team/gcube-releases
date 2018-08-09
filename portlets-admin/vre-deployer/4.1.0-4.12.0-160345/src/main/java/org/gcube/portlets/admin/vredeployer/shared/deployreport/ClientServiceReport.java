package org.gcube.portlets.admin.vredeployer.shared.deployreport;

import java.io.Serializable;

public class ClientServiceReport implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String name;
	/**
	 * 
	 */
	private String serviceClass;
	/**
	 * 
	 */
	private String version;
	/**
	 * 
	 */
	public ClientServiceReport() {
		super();
	}
	/**
	 * 
	 * @param name service name
	 * @param serviceClass service class
	 * @param version its version
	 */
	public ClientServiceReport(String name, String serviceClass, String version) {
		super();
		this.name = name;
		this.serviceClass = serviceClass;
		this.version = version;
	}
	/**
	 * 
	 * @return .
	 */
	public String getName() {
		return name;
	}
	/**
	 * 
	 * @param name .
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 
	 * @return .
	 */
	public String getServiceClass() {
		return serviceClass;
	}
	/**
	 * 
	 * @param serviceClass .
	 */
	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}
	/**
	 * 
	 * @return .
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * 
	 * @param version .
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
}

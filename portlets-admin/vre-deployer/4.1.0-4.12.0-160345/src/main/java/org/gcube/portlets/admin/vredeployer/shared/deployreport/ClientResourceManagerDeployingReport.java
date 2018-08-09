package org.gcube.portlets.admin.vredeployer.shared.deployreport;

import java.io.Serializable;

/**
 * 
 * @author massi
 *
 */
public class ClientResourceManagerDeployingReport implements Serializable {
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
	private String reportXML;
	
	public ClientResourceManagerDeployingReport() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ClientResourceManagerDeployingReport(DeployStatus status,
			String reportXML) {
		super();
		this.status = status;
		this.reportXML = reportXML;
	}

	public DeployStatus getStatus() {
		return status;
	}

	public void setStatus(DeployStatus status) {
		this.status = status;
	}

	public String getReportXML() {
		return reportXML;
	}

	public void setReportXML(String reportXML) {
		this.reportXML = reportXML;
	}
	
}

package org.gcube.portlets.admin.vredeployer.shared.deployreport;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author massi
 *
 */
public class ClientFunctionalityDeployReport implements Serializable {
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
	/**
	 * 
	 */
	private HashMap<ClientFunctionalityReport, List<ClientServiceReport>> funTable;
	/**
	 * 
	 */
	public ClientFunctionalityDeployReport() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 * @param globalstatus
	 * @param reportXML
	 * @param funTable
	 */
	public ClientFunctionalityDeployReport(
			DeployStatus globalstatus,
			String reportXML,
			HashMap<ClientFunctionalityReport, List<ClientServiceReport>> funTable) {
		super();
		this.status = globalstatus;
		this.reportXML = reportXML;
		this.funTable = funTable;
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
	 * @param globalstatus
	 */
	public void setStatus(DeployStatus globalstatus) {
		this.status = globalstatus;
	}
	/**
	 * 
	 * @return
	 */
	public HashMap<ClientFunctionalityReport, List<ClientServiceReport>> getFunTable() {
		return funTable;
	}
	
	/**
	 * 
	 * @param funTable
	 */
	public void setFunTable(
			HashMap<ClientFunctionalityReport, List<ClientServiceReport>> funTable) {
		this.funTable = funTable;
	}
	/**
	 * 
	 * @return
	 */
	public String getReportXML() {
		return reportXML;
	}
	/**
	 * 
	 * @param reportXML
	 */
	public void setReportXML(String reportXML) {
		this.reportXML = reportXML;
	}

}

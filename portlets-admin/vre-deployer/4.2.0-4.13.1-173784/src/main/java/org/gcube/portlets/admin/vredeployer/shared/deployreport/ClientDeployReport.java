package org.gcube.portlets.admin.vredeployer.shared.deployreport;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ClientDeployReport implements Serializable {
	/**
	 * 
	 */
	private DeployStatus globalStatus;
	/**
	 * 
	 */
	private ClientCloudReport cloudReport;
	/**
	 * 
	 */
	private ClientResourceManagerDeployingReport resourceManagerReport;
	/**
	 * 
	 */
	private ClientFunctionalityDeployReport functionalityReport;
	/**
	 * 
	 */
	private ClientResourcesDeployReport resourcesReport;
	/**
	 * 
	 */
	public ClientDeployReport() {
		super();
	}
	/**
	 * 
	 * @param status status
	 * @param cloudReport cloudReport
	 * @param resourceManagerReport resourceManagerReport
	 * @param functionalityReport functionalityReport
	 * @param resourcesReport resourcesReport
	 */
	public ClientDeployReport(DeployStatus status,
			ClientCloudReport cloudReport,
			ClientResourceManagerDeployingReport resourceManagerReport,
			ClientFunctionalityDeployReport functionalityReport,
			ClientResourcesDeployReport resourcesReport) {
		super();
		this.globalStatus = status;
		this.cloudReport = cloudReport;
		this.resourceManagerReport = resourceManagerReport;
		this.functionalityReport = functionalityReport;
		this.resourcesReport = resourcesReport;
	}
	/**
	 * @return
	 */
	public DeployStatus getGlobalsStatus() {
		return globalStatus;
	}
	/**
	 * 
	 * @param status
	 */
	public void setGlobalStatus(DeployStatus status) {
		this.globalStatus = status;
	}
	/**
	 * 
	 * @return
	 */
	public ClientCloudReport getCloudReport() {
		return cloudReport;
	}
	/**
	 * 
	 * @param cloudReport
	 */
	public void setCloudReport(ClientCloudReport cloudReport) {
		this.cloudReport = cloudReport;
	}
	/**
	 * 
	 * @return
	 */
	public ClientResourceManagerDeployingReport getResourceManagerReport() {
		return resourceManagerReport;
	}
	/**
	 * 
	 * @param resourceManagerReport
	 */
	public void setResourceManagerReport(
			ClientResourceManagerDeployingReport resourceManagerReport) {
		this.resourceManagerReport = resourceManagerReport;
	}
	/**
	 * 
	 * @return
	 */
	public ClientFunctionalityDeployReport getFunctionalityReport() {
		return functionalityReport;
	}
	/**
	 * 
	 * @param functionalityReport
	 */
	public void setFunctionalityReport(
			ClientFunctionalityDeployReport functionalityReport) {
		this.functionalityReport = functionalityReport;
	}
	/**
	 * 
	 * @return
	 */
	public ClientResourcesDeployReport getResourcesReport() {
		return resourcesReport;
	}
	/**
	 * 
	 * @param resourcesReport
	 */
	public void setResourcesReport(ClientResourcesDeployReport resourcesReport) {
		this.resourcesReport = resourcesReport;
	}
	

}

package org.gcube.vremanagement.vremodeler.utils.reports;

import java.io.Serializable;

/**
 * 
 * @author lucio
 *
 */
public class DeployReport implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7295105471156172674L;
	private Status status;
	private GHNonCloudReport cloudDeployingReport;
	private FunctionalityDeployingReport functionalityDeployingReport;
	private ResourceDeployingReport resourceDeployingReport;
	
	
	/**
	 * 
	 */
	public DeployReport(){
		this.status= Status.Waiting;
		this.cloudDeployingReport= new GHNonCloudReport();
		this.cloudDeployingReport.setStatus(Status.Waiting);
		this.functionalityDeployingReport= new FunctionalityDeployingReport();
		this.functionalityDeployingReport.setStatus(Status.Waiting);
		this.resourceDeployingReport= new ResourceDeployingReport();
		this.resourceDeployingReport.setStatus(Status.Waiting);
	}
	
	/**
	 * 
	 * @return
	 */
	public Status getStatus() {
		return status;
	}
	
	/**
	 * 
	 * @param state
	 */
	public void setStatus(Status status) {
		this.status = status;
	}
	
	/**
	 * 
	 * @return
	 */
	public GHNonCloudReport getCloudDeployingReport() {
		return cloudDeployingReport;
	}
	
	/**
	 * 
	 * @param cloudDeployingReport
	 */
	public void setCloudDeployingReport(GHNonCloudReport cloudDeployingReport) {
		this.cloudDeployingReport = cloudDeployingReport;
	}
	
	/**
	 * 
	 * @return
	 */
	public FunctionalityDeployingReport getFunctionalityDeployingReport() {
		return functionalityDeployingReport;
	}
	
	/**
	 * 
	 * @param functionalityDeployingReport
	 */
	public void setFunctionalityDeployingReport(
			FunctionalityDeployingReport functionalityDeployingReport) {
		this.functionalityDeployingReport = functionalityDeployingReport;
	}

	public ResourceDeployingReport getResourceDeployingReport() {
		return resourceDeployingReport;
	}

	public void setResourceDeployingReport(
			ResourceDeployingReport resourceDeployingReport) {
		this.resourceDeployingReport = resourceDeployingReport;
	}
}

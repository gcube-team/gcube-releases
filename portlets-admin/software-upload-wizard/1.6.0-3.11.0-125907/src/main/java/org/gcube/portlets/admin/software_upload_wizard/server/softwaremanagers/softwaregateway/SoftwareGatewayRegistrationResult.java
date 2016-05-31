/**
 * 
 */
package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.softwaregateway;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class SoftwareGatewayRegistrationResult {
	
	public static enum RegistrationStatus {ERROR, OK};
	
	protected RegistrationStatus status;
	protected String report;
	protected String id;
	
	

	/**
	 * @param id
	 */
	public SoftwareGatewayRegistrationResult(String id) {
		this.id = id;
		status = RegistrationStatus.OK;
	}

	/**
	 * @param status
	 * @param report
	 */
	public SoftwareGatewayRegistrationResult(RegistrationStatus status, String report) {
		this.status = status;
		this.report = report;
	}

	/**
	 * @return the status
	 */
	public RegistrationStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(RegistrationStatus status) {
		this.status = status;
	}

	/**
	 * @return the report
	 */
	public String getReport() {
		return report;
	}

	/**
	 * @param report the report to set
	 */
	public void setReport(String report) {
		this.report = report;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "SoftwareRepositoryResult [status=" + status + ", report="
				+ report + ", id=" + id + "]";
	}

}

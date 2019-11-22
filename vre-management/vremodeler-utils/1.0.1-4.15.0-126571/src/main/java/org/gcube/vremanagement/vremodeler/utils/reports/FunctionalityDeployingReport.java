package org.gcube.vremanagement.vremodeler.utils.reports;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;

public class FunctionalityDeployingReport implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5667226186772008010L;

	private Hashtable<FunctionalityReport, List<ServiceReport>> functionalityTable= new Hashtable<FunctionalityReport, List<ServiceReport>>();
	
	private String resourceManagerReport;
	private Status status;
	
	public FunctionalityDeployingReport(){
		this.status= Status.Waiting;
		this.functionalityTable= new Hashtable<FunctionalityReport, List<ServiceReport>>();
	}
	
	
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Hashtable<FunctionalityReport, List<ServiceReport>> getFunctionalityTable() {
		return functionalityTable;
	}

	public void setFunctionalityTable(
			Hashtable<FunctionalityReport, List<ServiceReport>> functionalityTable) {
		this.functionalityTable = functionalityTable;
	}
	
	public String getResourceManagerReport() {
		return resourceManagerReport;
	}

	public void setResourceManagerReport(String resourceManagerReport) {
		this.resourceManagerReport = resourceManagerReport;
	}

}

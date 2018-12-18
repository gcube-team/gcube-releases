package org.gcube.vremanagement.vremodeler.utils.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResourceDeployingReport implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1692792289962987059L;
	private Status status;
	private List<Resource> resources;
	
	public ResourceDeployingReport(){
		this.resources=new ArrayList<Resource>();
	}
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}
}

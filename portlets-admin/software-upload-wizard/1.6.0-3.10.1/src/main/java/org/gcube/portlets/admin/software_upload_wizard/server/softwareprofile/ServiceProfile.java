package org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile;

import java.io.Serializable;

public class ServiceProfile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3882225080770396403L;
	private Service service = new Service();
	private boolean thirdPartySoftware = false;
	
	public ServiceProfile(){
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public boolean isThirdPartySoftware() {
		return thirdPartySoftware;
	}

	public void setThirdPartySoftware(boolean isThirdPartySoftware) {
		this.thirdPartySoftware = isThirdPartySoftware;
	}

	
}

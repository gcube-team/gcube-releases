package org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile;

import java.io.Serializable;

public class TargetService implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -399464763684629313L;
	
	String serviceClass = "";
	String serviceName = "";
	Version serviceVersion = new Version();
	String packagename = "";
	String packageVersion = "1.0.0";
	
	public TargetService() {
		// Serialization only
	}
	
	public TargetService(String serviceClass, String serviceName,
			Version serviceVersion, String packagename, String packageVersion) {
		super();
		this.serviceClass = serviceClass;
		this.serviceName = serviceName;
		this.serviceVersion = serviceVersion;
		this.packagename = packagename;
		this.packageVersion = packageVersion;
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public String getServiceName() {
		return serviceName;
	}

	public Version getServiceVersion() {
		return serviceVersion;
	}

	public String getPackageName() {
		return packagename;
	}

	public String getPackageVersion() {
		return packageVersion;
	}	
	
}

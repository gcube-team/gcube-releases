package org.gcube.resources.federation.fhnmanager.api.type;


public class Software extends FHNResource {
	
	private String version;
	
	private String serviceClass;
	
	private String serviceName;
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Software() {

	}

	public Software(String id) {
		super(id);
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
}

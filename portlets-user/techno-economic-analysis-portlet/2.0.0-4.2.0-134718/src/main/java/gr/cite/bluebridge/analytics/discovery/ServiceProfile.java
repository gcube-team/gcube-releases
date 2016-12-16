package gr.cite.bluebridge.analytics.discovery;

public class ServiceProfile {

	private String serviceClass;
	private String serviceName;
	private String pathEndsWith;
	private String pathNotEndsWith;	
	private String pathContains;	
	
	public String getPathContains() {
		return pathContains;
	}

	public void setPathContains(String pathContains) {
		this.pathContains = pathContains;
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

	public String getPathEndsWith() {
		return pathEndsWith;
	}

	public void setPathEndsWith(String pathEndsWith) {
		this.pathEndsWith = pathEndsWith;
	}

	public String getPathNotEndsWith() {
		return pathNotEndsWith;
	}

	public void setPathNotEndsWith(String pathNotEndsWith) {
		this.pathNotEndsWith = pathNotEndsWith;
	}	
}

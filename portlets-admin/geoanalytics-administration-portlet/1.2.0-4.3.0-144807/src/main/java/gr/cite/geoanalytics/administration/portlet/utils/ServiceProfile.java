package gr.cite.geoanalytics.administration.portlet.utils;

public class ServiceProfile {
	
	private String serviceClass = null;
	private String serviceName = null;
	private String pathEndsWith = null;
	private String pathNotEndsWith = null;	
	private String pathContains = null;	
	
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
	
	public static ServiceProfile createGeoanalyticsProfile(){
		ServiceProfile serviceProfile = new ServiceProfile();
		serviceProfile.setServiceClass("geoanalytics");
		serviceProfile.setServiceName("geoanalytics-main-service");
		serviceProfile.setPathEndsWith("/");
		return serviceProfile;
	}
}

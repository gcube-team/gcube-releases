package gr.uoa.di.madgik.visualization.service;

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

	public boolean hasPathEndsWith() {
		return pathEndsWith != null;
	}

	public boolean hasPathNotEndsWith() {
		return pathNotEndsWith != null;
	}

	public boolean hasPathContains() {
		return pathContains != null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null) {
			return false;
		}

		if (getClass() != o.getClass()) {
			return false;
		}

		ServiceProfile other = (ServiceProfile) o;

		if (this.getServiceName() == null) {
			if (other.getServiceName() != null) {
				return false;
			}
		} else if (!this.getServiceName().equals(other.getServiceName())) {
			return false;
		}

		if (this.getServiceClass() == null) {
			if (other.getServiceClass() != null) {
				return false;
			}
		} else if (!this.getServiceClass().equals(other.getServiceClass())) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((serviceClass == null) ? 0 : serviceClass.hashCode());
		result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
		return result;
	}
}
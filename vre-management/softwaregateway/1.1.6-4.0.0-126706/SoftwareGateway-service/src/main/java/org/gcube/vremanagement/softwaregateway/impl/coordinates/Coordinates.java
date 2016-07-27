package org.gcube.vremanagement.softwaregateway.impl.coordinates;

import org.gcube.vremanagement.softwaregateway.impl.exceptions.BadCoordinatesException;
/**
 * Defines a set of coordinates
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public abstract class Coordinates  implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String groupId;
	protected String artifactId;
	protected String version;
	protected String serviceName;
	protected String serviceClass;
	protected String serviceVersion;
	protected String packageName;
	protected String packageVersion;
	
	
	public abstract Coordinates convert() throws BadCoordinatesException;

	
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	public String getServiceVersion() {
		return serviceVersion;
	}

	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPackageVersion() {
		return packageVersion;
	}

	public void setPackageVersion(String packageVersion) {
		this.packageVersion = packageVersion;
	}
	
}

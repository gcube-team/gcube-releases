package org.gcube.common.vremanagement.deployer.impl.resources;

import java.io.Serializable;

/**
 * Package Key
 *  
 * @author Manuele Simi (CNR)
 *
 */
public class KeyData  implements Serializable {
	
	private static final long serialVersionUID = -7149894250146039521L;
	/** the package name */
	private String packagename;
	/** the package version */
	private String packageVersion;
	/** the service class*/
	private String serviceClass;
	/** the service name */
	private String serviceName;
	/** the service version*/
	private String serviceVersion;

	//hiding default constructor
	@SuppressWarnings("unused")
	private KeyData() {}
	
	public KeyData(String serviceClass, String serviceName, String serviceVersion, String packagename, String packageVersion) {
		this.setServiceClass(serviceClass);
		this.setServiceName(serviceName);
		this.setServiceVersion(serviceVersion);
		this.setPackageName(packagename);
		this.setPackageVersion(packageVersion);
	}


	public String getPackageName() {
		return packagename;
	}

	private void setPackageName(String packagename) {
		this.packagename = packagename;
	}

	public String getPackageVersion() {
		return packageVersion;
	}

	private void setPackageVersion(String packageVersion) {
		try {
			this.packageVersion = packageVersion; 
		} catch (IllegalArgumentException iae) {
			//the input packageVersion is a range, maybe because we are shaping a dependency
			//by assigning an empty string, every instance package will match
			this.packageVersion = "";
		}
	}

	public String getServiceClass() {
		return serviceClass;
	}

	private void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	public String getServiceName() {
		return serviceName;
	}

	private void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceVersion() {
		return serviceVersion;
	}

	private void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion; 
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) { 
		if (obj == null)
			return false;
		else if (! obj.getClass().isAssignableFrom(this.getClass())) 
			return false;				
		else 
			return (this.toString().equals(obj.toString())); 
	 }

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return this.toString().hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		//the package version is not part of the key
		//this implies that we make the strong assumption that only one version of a package is deployed on the node  
//		//this is because of otherwise we cannot manage dependency ranges 
		//(i.e. how to compare a range with a fixed version? we will end up to behave like Maven, forget it)
		return "PackageData [packagename=" + packagename + ", serviceClass="
				+ serviceClass + ", serviceName=" + serviceName
				+ ", serviceVersion=" + serviceVersion + "]";
	}


}
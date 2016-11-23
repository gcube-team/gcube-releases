package org.gcube.vremanagement.resourcemanager.impl.resources.software;

import org.gcube.vremanagement.resourcemanager.stubs.binder.PackageItem;

/**
 * A GCUBE service
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class GCUBEPackage {

	protected String clazz;
	
	protected String name;
	
	protected String version;
	
	protected String packagename;
	
	protected String packageversion;
	
	/** the gHN on which the servie is expected to be deployed */
	protected String GHN = null;
	
	
	public GCUBEPackage(String serviceClass, String serviceName, String serviceVersion,
			String packageName, String packageVersion, String hostedOn) {
		this.setClazz(serviceClass);
		this.setName(serviceName);
		if (serviceVersion != null)
			this.setVersion(serviceVersion);
		else 
			this.setVersion("1.0.0");
		this.setPackageName(packageName);
		this.setPackageVersion(packageVersion);
		this.setGHNName(hostedOn);
	}

	public GCUBEPackage() {}
	
	public static GCUBEPackage fromServiceItem(PackageItem item) {
		GCUBEPackage s = new GCUBEPackage();
		s.setClazz(item.getServiceClass());
		s.setName(item.getServiceName());
		s.setVersion(item.getServiceVersion());
		s.setPackageName(item.getPackageName());
		s.setPackageVersion(item.getPackageVersion());
		if ((item.getTargetGHNName() != null) && (!item.getTargetGHNName().equals("")))
			s.setGHNName(item.getTargetGHNName());
		return s;
	}
	
	/**
	 * @return the clazz
	 */
	public String getClazz() {
		return clazz;
	}

	/**
	 * @param clazz the class to set
	 */
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the packagename
	 */
	public String getPackageName() {
		return packagename;
	}

	/**
	 * @param packagename the packagename to set
	 */
	public void setPackageName(String packagename) {
		this.packagename = packagename;
	}

	/**
	 * @return the packageversion
	 */
	public String getPackageVersion() {
		return packageversion;
	}

	/**
	 * @param packageversion the packageversion to set
	 */
	public void setPackageVersion(String packageversion) {
		this.packageversion = packageversion;
	}

	/**
	 * @return the gHN name
	 */
	public String getGHNName() {
		return GHN;
	}

	/**
	 * @param ghn the gHN to set
	 */
	public void setGHNName(String ghn) {
		GHN = ghn;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		result = prime * result + ((packagename == null) ? 0 : packagename.hashCode());
		result = prime * result + ((packageversion == null) ? 0 : packageversion.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final GCUBEPackage other = (GCUBEPackage) obj;
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.equals(other.clazz))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		if (packageversion == null) {
			if (other.packageversion != null)
				return false;
		} else if (!packageversion.equals(other.packageversion))
			return false;
		if (packagename == null) {
			if (other.packagename != null)
				return false;
		} else if (!packagename.equals(other.packagename))
			return false;
		if (GHN == null) {
			if (other.GHN != null)
				return false;
		} else if (!GHN.equals(other.GHN))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Package [class=" + clazz 
				+ ", name=" + name 
				+ ", version=" + version 
				+ ", packagename=" + packagename 
				+ ", packageversion=" + packageversion 
				+ ", hostedOn=" + GHN 
				+ "]";
	}

	
	public String getID() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getClazz());
		builder.append('-');
		builder.append(this.getName());
		builder.append('-');
		builder.append(this.getVersion());
		//the following two may be null in case of service deployments
		if (this.getPackageName() != null) {
			builder.append('-');
			builder.append(this.getPackageName());
		}
		if (this.getPackageVersion() != null) {
			builder.append('-');
			builder.append(this.getPackageVersion());
		}
		return builder.toString();
	}
	
}

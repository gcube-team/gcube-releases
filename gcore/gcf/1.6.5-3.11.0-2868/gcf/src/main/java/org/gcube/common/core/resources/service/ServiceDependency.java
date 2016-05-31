package org.gcube.common.core.resources.service;


/**
 * Describe the VRE Component element
 * 
 * @author Andrea Manzi, Manuele Simi (CNR)
 *
 */
public class ServiceDependency {
	
	protected String clazz;	
	protected String name;
	protected String version;
	
	/**
	 * Gets the name
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name 
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the class
	 * @return the class
	 */
	public String getClazz() {
		return clazz;
	}
	
	/**
	 * Sets the class
	 * @param clazz the class
	 */
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	
	
	/**
	 * Gets the version
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * Sets the version
	 * @param version the version
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		final ServiceDependency other = (ServiceDependency) obj;
		
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (! clazz.equals(other.clazz))
			return false;
		
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (! name.equals(other.name))
			return false;
		
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (! version.equals(other.version))
			return false;
		
		
		return true;
	}

	public static class DescriptiveParametersValue {}
}

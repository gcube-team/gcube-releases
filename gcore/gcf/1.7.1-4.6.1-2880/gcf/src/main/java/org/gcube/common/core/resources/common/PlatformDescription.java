package org.gcube.common.core.resources.common;

/**
 * A platform hosting any entity in gCube
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class PlatformDescription {

	protected String name;
	protected short version = 1;
	protected short minorversion = 0; //optional in the schema
	protected short revisionversion = 0; //optional in the schema
	protected short buildversion = 0; //optional in the schema

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
	public short getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(short version) {
		this.version = version;
	}
	/**
	 * @return the minor version
	 */
	public short getMinorVersion() {
		return minorversion;
	}
	/**
	 * @param minorversion the minor version to set
	 */
	public void setMinorVersion(short minorversion) {
		this.minorversion = minorversion;
	}
	/**
	 * @param revision the revision version to set
	 */
	public void setRevisionVersion(Short revision) {
		this.revisionversion = revision;		
	}
	/**
	 * @param build the build version to set
	 */
	public void setBuildVersion(Short build) {
		this.buildversion = build;
	}
	/**
	 * 
	 * @return the revision version
	 */
	public short getRevisionVersion() {
		return this.revisionversion;
	}
	
	/**
	 * 
	 * @return the build version
	 */
	public short getBuildVersion() {
		return this.buildversion;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + minorversion;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + version;
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
		PlatformDescription other = (PlatformDescription) obj;
		if (minorversion != other.minorversion)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (version != other.version)
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[name=" + name + ", version=" + version
				+ ", minorversion=" + minorversion 
				+ ", revisionversion=" + revisionversion 
				+ ", buildversion=" + buildversion 
				+ "]";
	}

}

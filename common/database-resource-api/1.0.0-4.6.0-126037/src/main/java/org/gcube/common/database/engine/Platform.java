package org.gcube.common.database.engine;

public class Platform {
	
	private String name = null;
	
	private Short version = null;
	
	private Short minorVersion = null;
	
	private Short revisionVersion = null;
	
	private Short buildVersion = null;
	
	public Platform() {}

	public Platform(String name, Short version, Short minorVersion, Short revisionVersion, Short buildVersion) {
		super();
		this.name = name;
		this.version = version;
		this.minorVersion = minorVersion;
		this.revisionVersion = revisionVersion;
		this.buildVersion = buildVersion;
	}

	public String getName() {
		return name;
	}

	public short getVersion() {
		return version;
	}

	public short getMinorVersion() {
		return minorVersion;
	}

	public short getRevisionVersion() {
		return revisionVersion;
	}

	public short getBuildVersion() {
		return buildVersion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((buildVersion == null) ? 0 : buildVersion.hashCode());
		result = prime * result + ((minorVersion == null) ? 0 : minorVersion.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((revisionVersion == null) ? 0 : revisionVersion.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Platform other = (Platform) obj;
		if (buildVersion == null) {
			if (other.buildVersion != null)
				return false;
		} else if (!buildVersion.equals(other.buildVersion))
			return false;
		if (minorVersion == null) {
			if (other.minorVersion != null)
				return false;
		} else if (!minorVersion.equals(other.minorVersion))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (revisionVersion == null) {
			if (other.revisionVersion != null)
				return false;
		} else if (!revisionVersion.equals(other.revisionVersion))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
	

}

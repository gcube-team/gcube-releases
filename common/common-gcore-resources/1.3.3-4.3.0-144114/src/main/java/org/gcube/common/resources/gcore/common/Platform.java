package org.gcube.common.resources.gcore.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"name","version","minorVersion","revisionVersion","buildVersion"})
@XmlRootElement(name = "Platform")
public class Platform {

	@XmlTransient
	public boolean isEmpty=true;
	
	@XmlElement(name="Name")
	private String name;
	
	@XmlElement(name="Version")
	private Short version;
	
	@XmlElement(name="MinorVersion")
	private Short minorVersion;
	
	@XmlElement(name="RevisionVersion")
	private Short revisionVersion;
	
	@XmlElement(name="BuildVersion")
	private Short buildVersion;

	public String name() {
		return name;
	}
	
	public Platform name(String name) {
		this.name = name;
		this.isEmpty=false;
		return this;
	}

	public short version() {
		return version;
	}
	
	public Platform version(short version) {
		this.version = version;
		this.isEmpty=false;
		return this;
	}

	public Short minorVersion() {
		return minorVersion;
	}
	
	public Platform minorVersion(short minorVersion) {
		this.minorVersion = minorVersion;
		this.isEmpty=false;
		return this;
	}

	public Short revisionVersion() {
		return revisionVersion;
	}
	
	public Platform revisionVersion(short revisionVersion) {
		this.revisionVersion = revisionVersion;
		this.isEmpty=false;
		return this;
	}

	public Short buildVersion() {
		return buildVersion;
	}
	
	public Platform buildVersion(short buildVersion) {
		this.buildVersion = buildVersion;
		this.isEmpty=false;
		return this;
	}

	@Override
	public String toString() {
		return "[name=" + name + ", version=" + version + ", minorVersion=" + minorVersion
				+ ", revisionVersion=" + revisionVersion + ", buildVersion=" + buildVersion + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((buildVersion == null) ? 0 : buildVersion.hashCode());
		result = prime * result + (isEmpty ? 1231 : 1237);
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
		if (isEmpty != other.isEmpty)
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

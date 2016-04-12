package org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile;

import java.io.Serializable;

public class Version implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9089935952961111926L;

	private static final String VERSION_SEPARATOR = ".";

	protected int majorVersion = 1;
	protected int minorVersion = 0;
	protected int ageVersion = 0;

	public Version() {
	}

	public Version(int major, int minor, int age) {
		this.majorVersion = major;
		this.minorVersion = minor;
		this.ageVersion = age;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}

	public int getAgeVersion() {
		return ageVersion;
	}

	public void setAgeVersion(int ageVersion) {
		this.ageVersion = ageVersion;
	}

	public boolean equals(Version version) {
		if (getMajorVersion() == version.getMajorVersion()
				&& getMinorVersion() == version.getMinorVersion()
				&& getAgeVersion() == version.getAgeVersion())
			return true;
		else
			return false;
	}

	@Override
	public String toString() {
		return getMajorVersion() + VERSION_SEPARATOR + getMinorVersion()
				+ VERSION_SEPARATOR + getAgeVersion();
	}
	
	public static Version valueOf(String string) {
		if (string == null) return null;
		String tmp = string.trim();
		if (tmp.matches("[0-9]{1,2}\\.[0-9]{1,2}\\.[0-9]{1,2}")) return null;
		String[] tmpString = tmp.split("\\.");
		return new Version(Integer.valueOf(tmpString[0]), Integer.valueOf(tmpString[1]), Integer.valueOf(tmpString[2]));
	}

}

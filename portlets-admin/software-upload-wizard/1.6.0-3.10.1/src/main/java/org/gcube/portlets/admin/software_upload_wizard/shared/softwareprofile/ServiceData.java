package org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile;

import java.io.Serializable;

public class ServiceData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2572500926726546604L;

	private String name = "";
	private String description = "";
	private Version version = new Version();
	private String class_ = "";

	public ServiceData() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

	public String getClazz() {
		return class_;
	}

	public void setClazz(String class_) {
		this.class_ = class_;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String NEW_LINE = "\n";

		result.append(this.getClass().getName() + " Object {" + NEW_LINE);
		result.append(" Name: " + getName() + NEW_LINE);
		result.append(" Description: " + getDescription() + NEW_LINE);
		result.append(" Version: " + getVersion() + NEW_LINE);
		result.append(" Class: " + getClazz() + NEW_LINE);
		result.append("}" + NEW_LINE);

		return result.toString();
	}

}

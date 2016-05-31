package org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile;

import java.io.Serializable;
import java.util.ArrayList;

public class PackageData implements Serializable {

	public TargetService getTargetService() {
		return targetService;
	}

	public void setTargetService(TargetService targetService) {
		this.targetService = targetService;
	}

	public enum PackageType {
		Main, Software, Plugin
	};

	/**
	 * 
	 */
	private static final long serialVersionUID = 574213217911586583L;

	private String name = "";
	private String description = "";
	private Version version = new Version();
	private PackageType packageType;
	
	private TargetService targetService = new TargetService();

	private ArrayList<String> entrypoints = new ArrayList<String>();
	private ArrayList<PortType> portTypes = new ArrayList<PortType>();

	@SuppressWarnings("unused")
	private PackageData() {
		// Serialization only
	}

	public PackageData(PackageType packageType) {
		this.setPackageType(packageType);
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

	public PackageType getPackageType() {
		return packageType;
	}

	public void setPackageType(PackageType packageType) {
		this.packageType = packageType;
	}

	public ArrayList<String> getEntrypoints() {
		return entrypoints;
	}

	public void setEntrypoints(ArrayList<String> entrypoints) {
		this.entrypoints = entrypoints;
	}

	public ArrayList<PortType> getPortTypes() {
		return portTypes;
	}

	public void setPortTypes(ArrayList<PortType> portTypes) {
		this.portTypes = portTypes;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String NEW_LINE = "\n";

		result.append(this.getClass().getName() + " Object {" + NEW_LINE);
		result.append(" Package Type: " + getPackageType() + NEW_LINE);
		result.append(" Name: " + getName() + NEW_LINE);
		result.append(" Description: " + getDescription() + NEW_LINE);
		result.append(" Version: " + getVersion() + NEW_LINE);
		result.append("}" + NEW_LINE);

		return result.toString();
	}

}

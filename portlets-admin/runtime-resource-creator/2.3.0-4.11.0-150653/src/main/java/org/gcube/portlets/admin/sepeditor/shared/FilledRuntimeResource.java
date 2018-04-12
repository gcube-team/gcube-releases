package org.gcube.portlets.admin.sepeditor.shared;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class FilledRuntimeResource implements Serializable{

	private ArrayList<RRAccessPoint> rRAccessPoints;

	private String resourceId;
	private String resourceName;
	private String type = "RuntimeResource";
	private String version;
	private String category;
	private String description;
	private String platformName;
	private String platformVersion;
	private String platformMinorVersion;
	private String platformRevisionVersion;
	private String platformBuildVersion;
	private String runtimeHostedOn;
	private String runtimeStatus;
	private String runtimegHNUniqueID;
	
	public FilledRuntimeResource() {
		super();
	}
		
	public FilledRuntimeResource(String resourceId, ArrayList<RRAccessPoint> rRAccessPoints,
			String resourceName, String version, String category,
			String description, String platformName, String platformVersion,
			String platformMinorVersion, String platformRevisionVersion,
			String platformBuildVersion, String runtimeHostedOn,
			String runtimeStatus, String runtimegHNUniqueID) {
		super();
		this.resourceId = resourceId;
		this.rRAccessPoints = rRAccessPoints;
		this.resourceName = resourceName;
		this.version = version;
		this.category = category;
		this.description = description;
		this.platformName = platformName;
		this.platformVersion = platformVersion;
		this.platformMinorVersion = platformMinorVersion;
		this.platformRevisionVersion = platformRevisionVersion;
		this.platformBuildVersion = platformBuildVersion;
		this.runtimeHostedOn = runtimeHostedOn;
		this.runtimeStatus = runtimeStatus;
		this.runtimegHNUniqueID = runtimegHNUniqueID;
	}

	
	
	public String getResourceId() {
		return resourceId;
	}


	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}


	public ArrayList<RRAccessPoint> getRRAccessPoints() {
		return rRAccessPoints;
	}

	public void setRRAccessPoints(ArrayList<RRAccessPoint> rRAccessPoints) {
		this.rRAccessPoints = rRAccessPoints;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPlatformName() {
		return platformName;
	}

	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}

	public String getPlatformVersion() {
		return platformVersion;
	}

	public void setPlatformVersion(String platformVersion) {
		this.platformVersion = platformVersion;
	}

	public String getPlatformMinorVersion() {
		return platformMinorVersion;
	}

	public void setPlatformMinorVersion(String platformMinorVersion) {
		this.platformMinorVersion = platformMinorVersion;
	}

	public String getPlatformRevisionVersion() {
		return platformRevisionVersion;
	}

	public void setPlatformRevisionVersion(String platformRevisionVersion) {
		this.platformRevisionVersion = platformRevisionVersion;
	}

	public String getPlatformBuildVersion() {
		return platformBuildVersion;
	}

	public void setPlatformBuildVersion(String platformBuildVersion) {
		this.platformBuildVersion = platformBuildVersion;
	}

	public String getRuntimeHostedOn() {
		return runtimeHostedOn;
	}

	public void setRuntimeHostedOn(String runtimeHostedOn) {
		this.runtimeHostedOn = runtimeHostedOn;
	}

	public String getRuntimeStatus() {
		return runtimeStatus;
	}

	public void setRuntimeStatus(String runtimeStatus) {
		this.runtimeStatus = runtimeStatus;
	}

	public String getRuntimegHNUniqueID() {
		return runtimegHNUniqueID;
	}

	public void setRuntimegHNUniqueID(String runtimegHNUniqueID) {
		this.runtimegHNUniqueID = runtimegHNUniqueID;
	}

	public String getType() {
		return type;
	}
	
	
	
}

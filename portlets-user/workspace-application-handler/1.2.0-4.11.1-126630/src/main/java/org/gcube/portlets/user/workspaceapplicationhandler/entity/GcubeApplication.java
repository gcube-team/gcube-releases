package org.gcube.portlets.user.workspaceapplicationhandler.entity;

import java.io.Serializable;

public class GcubeApplication implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4583979480764085921L;
	
	private String keyOID = null;
	private ApplicationProfile appProfile;
	private String type = null;
	private String name;
	private String appId;
	
	public String getName() {
		return name;
	}

	public String getAppId() {
		return appId;
	}

	public GcubeApplication(String type, String keyOID, String appId, ApplicationProfile appProfile) {
		super();
		this.type = type;
		this.keyOID = keyOID;
		this.appId = appId;
		this.appProfile = appProfile;
	}

	public GcubeApplication() {
	}

	public GcubeApplication(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public String getId() {
		return keyOID;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setId(String id) {
		this.keyOID = id;
	}
	
	public String getKeyOID() {
		return keyOID;
	}

	public ApplicationProfile getAppProfile() {
		return appProfile;
	}

	public void setKeyOID(String keyOID) {
		this.keyOID = keyOID;
	}

	public void setAppProfile(ApplicationProfile appProfile) {
		this.appProfile = appProfile;
	}

	public void setName(String name) {
		this.name = name;	
	}

	public void setAppId(String appId) {
		this.appId = appId;
		
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GcubeApplication [keyOID=");
		builder.append(keyOID);
		builder.append(", appProfile=");
		builder.append(appProfile);
		builder.append(", type=");
		builder.append(type);
		builder.append(", name=");
		builder.append(name);
		builder.append(", appId=");
		builder.append(appId);
		builder.append("]");
		return builder.toString();
	}
	
}

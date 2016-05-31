package org.gcube.portlets.user.workspace.shared;

import java.io.Serializable;


/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 21, 2014
 *
 */
public class WorkspaceACL implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1682851324671600049L;
	
	private String id;
	private String label;
	private boolean defaultValue;
	private USER_TYPE userType;

	private String description;


	public enum USER_TYPE{ADMINISTRATOR, GROUP, OWNER, OTHER};
	
	public WorkspaceACL(String id, String label, boolean defaultValue, USER_TYPE userType, String description) {
		super();
		this.id = id;
		this.label = label;
		this.defaultValue = defaultValue;
		this.userType = userType;
		this.description = description;
	}

	
	public WorkspaceACL() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	public USER_TYPE getUserType() {
		return userType;
	}

	public void setUserType(USER_TYPE userType) {
		this.userType = userType;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WorkspaceACL [id=");
		builder.append(id);
		builder.append(", label=");
		builder.append(label);
		builder.append(", defaultValue=");
		builder.append(defaultValue);
		builder.append(", userType=");
		builder.append(userType);
		builder.append(", description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}
}

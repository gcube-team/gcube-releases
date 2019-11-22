package org.gcube.portlets.widgets.workspacesharingwidget.shared;

import java.io.Serializable;



/**
 * 
 * @author Francesco Mangiacrapa 
 * Feb 21, 2014
 *
 */
public class WorkspaceACL implements Serializable{


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 707825511378778432L;
	
	private String id;
	private String label;
	private boolean defaultValue;
	private USER_TYPE userType;

	private String description;

	private ACL_TYPE aclType;


	public enum USER_TYPE{ADMINISTRATOR, GROUP, OWNER, OTHER};
	
	public WorkspaceACL(String serverId, ACL_TYPE aclType, String label, boolean defaultValue, USER_TYPE userType, String description) {
		super();
		this.id = serverId;
		this.label = label;
		this.defaultValue = defaultValue;
		this.userType = userType;
		this.description = description;
		this.aclType = aclType;
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

	public ACL_TYPE getAclType() {
		return aclType;
	}

	public void setAclType(ACL_TYPE aclType) {
		this.aclType = aclType;
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
		builder.append(", aclType=");
		builder.append(aclType);
		builder.append("]");
		return builder.toString();
	}

}

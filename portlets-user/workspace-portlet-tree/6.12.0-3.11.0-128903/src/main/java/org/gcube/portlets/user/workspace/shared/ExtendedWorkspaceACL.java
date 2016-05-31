package org.gcube.portlets.user.workspace.shared;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 21, 2014
 *
 */
public class ExtendedWorkspaceACL extends WorkspaceACL{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5272059977475806564L;
	
	
	private String loginOwner;
	private String workspaceItemId;


	private boolean isBaseSharedFolder;
	
	/**
	 * 
	 */
	public ExtendedWorkspaceACL() {
	}

	/**
	 * @param id
	 * @param label
	 * @param defaultValue
	 * @param userType
	 * @param description
	 * @param loginOwner
	 * @param workspaceItemId
	 */
	public ExtendedWorkspaceACL(String id, String label, boolean defaultValue, USER_TYPE userType, String description, String loginOwner, String workspaceItemId, boolean isBaseSharedFolder) {
		super(id, label, defaultValue, userType, description);
		this.loginOwner = loginOwner;
		this.workspaceItemId = workspaceItemId;
		this.isBaseSharedFolder = isBaseSharedFolder;
	}
	
	


	public String getLoginOwner() {
		return loginOwner;
	}

	public String getWorkspaceItemId() {
		return workspaceItemId;
	}

	public void setLoginOwner(String loginOwner) {
		this.loginOwner = loginOwner;
	}

	public void setWorkspaceItemId(String workspaceItemId) {
		this.workspaceItemId = workspaceItemId;
	}
	
	public boolean isBaseSharedFolder() {
		return isBaseSharedFolder;
	}

	public void setBaseSharedFolder(boolean isBaseSharedFolder) {
		this.isBaseSharedFolder = isBaseSharedFolder;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExtendedWorkspaceACL [loginOwner=");
		builder.append(loginOwner);
		builder.append(", workspaceItemId=");
		builder.append(workspaceItemId);
		builder.append(", isBaseSharedFolder=");
		builder.append(isBaseSharedFolder);
		builder.append("]");
		return builder.toString();
	}
}

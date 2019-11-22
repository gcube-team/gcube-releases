package org.gcube.portlets.widgets.workspacesharingwidget.shared;

import org.gcube.portlets.widgets.workspacesharingwidget.shared.ACL_TYPE;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.WorkspaceACL;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it Feb 21, 2014
 *
 */
public class ExtendedWorkspaceACL extends WorkspaceACL {

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

	public ExtendedWorkspaceACL(String id, ACL_TYPE aclType, String label, boolean defaultValue, USER_TYPE userType,
			String description, String loginOwner, String workspaceItemId, boolean isBaseSharedFolder) {
		super(id, aclType, label, defaultValue, userType, description);
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
		return "ExtendedWorkspaceACL [loginOwner=" + loginOwner + ", workspaceItemId=" + workspaceItemId
				+ ", isBaseSharedFolder=" + isBaseSharedFolder + ", getId()=" + getId() + ", getLabel()=" + getLabel()
				+ ", getDefaultValue()=" + getDefaultValue() + ", getUserType()=" + getUserType()
				+ ", getDescription()=" + getDescription() + ", getAclType()=" + getAclType() + "]";
	}

}

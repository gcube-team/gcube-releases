package org.gcube.portlets.user.questions.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GroupDTO implements IsSerializable{
	private boolean isManager;
	private String groupName;
	private String groupDescription;
	private String viewGroupURL;
	public GroupDTO(boolean isManager, String groupName, String groupDescription, String viewGroupURL) {
		super();
		this.isManager = isManager;
		this.groupName = groupName;
		this.groupDescription = groupDescription;
		this.viewGroupURL = viewGroupURL;
	}
	public GroupDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public boolean isManager() {
		return isManager;
	}
	public void setManager(boolean isManager) {
		this.isManager = isManager;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupDescription() {
		return groupDescription;
	}
	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}
	public String getViewGroupURL() {
		return viewGroupURL;
	}
	public void setViewGroupURL(String viewGroupURL) {
		this.viewGroupURL = viewGroupURL;
	}
	@Override
	public String toString() {
		return "GroupDTO [isManager=" + isManager + ", groupName=" + groupName + ", groupDescription="
				+ groupDescription + ", viewGroupURL=" + viewGroupURL + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupDescription == null) ? 0 : groupDescription.hashCode());
		result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
		result = prime * result + (isManager ? 1231 : 1237);
		result = prime * result + ((viewGroupURL == null) ? 0 : viewGroupURL.hashCode());
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
		GroupDTO other = (GroupDTO) obj;
		if (groupDescription == null) {
			if (other.groupDescription != null)
				return false;
		} else if (!groupDescription.equals(other.groupDescription))
			return false;
		if (groupName == null) {
			if (other.groupName != null)
				return false;
		} else if (!groupName.equals(other.groupName))
			return false;
		if (isManager != other.isManager)
			return false;
		if (viewGroupURL == null) {
			if (other.viewGroupURL != null)
				return false;
		} else if (!viewGroupURL.equals(other.viewGroupURL))
			return false;
		return true;
	}
	
}

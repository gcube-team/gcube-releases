package org.gcube.vomanagement.usermanagement.model;

import java.io.Serializable;
import java.util.List;


@SuppressWarnings("serial")
public class GCubeGroup implements Serializable {
	
	long groupId;
	long parentGroupId;
	String groupName;
	String description;
	String friendlyURL;
	long logoId;
	List<GCubeGroup> children;
	GroupMembershipType membershipType;
	
	public GCubeGroup() {
		super();
	}
	
	public GCubeGroup(long groupId, long parentGroupId, String groupName,
			String description, String friendlyURL, long logoId,
			List<GCubeGroup> children) {
		super();
		this.groupId = groupId;
		this.parentGroupId = parentGroupId;
		this.groupName = groupName;
		this.description = description;
		this.friendlyURL = friendlyURL;
		this.logoId = logoId;
		this.children = children;
		this.membershipType = GroupMembershipType.RESTRICTED;
	}
	
	public GCubeGroup(long groupId, long parentGroupId, String groupName,
			String description, String friendlyURL, long logoId,
			List<GCubeGroup> children, GroupMembershipType membershipType) {
		super();
		this.groupId = groupId;
		this.parentGroupId = parentGroupId;
		this.groupName = groupName;
		this.description = description;
		this.friendlyURL = friendlyURL;
		this.logoId = logoId;
		this.children = children;
		this.membershipType = membershipType;
	}


	public long getGroupId() {
		return groupId;
	}


	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}


	public long getParentGroupId() {
		return parentGroupId;
	}


	public void setParentGroupId(long parentGroupId) {
		this.parentGroupId = parentGroupId;
	}


	public String getGroupName() {
		return groupName;
	}
	/**
	 * use getGroupName
	 * @return
	 */
	@Deprecated
	public String getName() {
		return groupName;
	}


	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getFriendlyURL() {
		return friendlyURL;
	}


	public void setFriendlyURL(String friendlyURL) {
		this.friendlyURL = friendlyURL;
	}


	public long getLogoId() {
		return logoId;
	}


	public void setLogoId(long logoId) {
		this.logoId = logoId;
	}


	public List<GCubeGroup> getChildren() {
		return children;
	}


	public void setChildren(List<GCubeGroup> children) {
		this.children = children;
	}
	
	
	
	public GroupMembershipType getMembershipType() {
		return membershipType;
	}

	public void setMembershipType(GroupMembershipType membershipType) {
		this.membershipType = membershipType;
	}

	@Override
	public String toString() {
		return "GCubeGroup [groupId=" + groupId + ", parentGroupId=" + parentGroupId + ", groupName=" + groupName
				+ ", description=" + description + ", friendlyURL=" + friendlyURL + ", logoId=" + logoId + ", children="
				+ children + ", membershipType=" + membershipType + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GCubeGroup other = (GCubeGroup) obj;
		if (groupId != other.groupId)
			return false;
		if (groupName == null) {
			if (other.groupName != null)
				return false;
		} else if (!groupName.equals(other.groupName))
			return false;
		return true;
	}

	
}

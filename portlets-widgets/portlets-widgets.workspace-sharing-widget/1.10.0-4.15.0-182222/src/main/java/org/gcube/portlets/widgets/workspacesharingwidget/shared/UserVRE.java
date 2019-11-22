package org.gcube.portlets.widgets.workspacesharingwidget.shared;

import java.io.Serializable;
import java.util.Comparator;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class UserVRE extends BaseModelData implements Serializable, Comparable<UserVRE> {

	private static final long serialVersionUID = 1L;

	protected static final String GROUP_ID = "groupId";
	public static final String PARENT_GROUP_ID = "parentGroupId";
	public static final String GROUP_NAME = "groupName";
	public static final String GROUP_DESCRIPTION = "groupDescription";

	public UserVRE() {
		super();
	}

	public UserVRE(long groupId, long parentGroupId, String groupName, String description) {
		super();
		setGroupId(groupId);
		setParentGroupId(parentGroupId);
		setGroupName(groupName);
		setDescription(description);
	}

	public long getGroupId() {
		return get(GROUP_ID);
	}

	public void setGroupId(long groupId) {
		set(GROUP_ID, groupId);
	}

	public long getParentGroupId() {
		return get(PARENT_GROUP_ID);
	}

	public void setParentGroupId(long parentGroupId) {
		set(PARENT_GROUP_ID, parentGroupId);
	}

	public String getGroupName() {
		return get(GROUP_NAME);
	}

	public void setGroupName(String groupName) {
		set(GROUP_NAME, groupName);
	}

	public String getDescription() {
		return get(GROUP_DESCRIPTION);
	}

	public void setDescription(String description) {
		set(GROUP_DESCRIPTION, description);
	}

	public static Comparator<UserVRE> COMPARATOR_USER_VRE = new Comparator<UserVRE>() {

		public int compare(UserVRE o1, UserVRE o2) {
			return (o1.getGroupId() < o2.getGroupId()) ? -1 : ((o1.getGroupId() == o2.getGroupId()) ? 0 : 1);
		}
	};

	@Override
	public int compareTo(UserVRE o) {
		return UserVRE.COMPARATOR_USER_VRE.compare(this, o);
	}

	@Override
	public boolean equals(Object obj) {
		int compare = compareTo((UserVRE) obj);
		return compare == 0 ? true : false;
	}

	@Override
	public String toString() {
		return "UserGroup [getGroupId()=" + getGroupId() + ", getParentGroupId()=" + getParentGroupId()
				+ ", getGroupName()=" + getGroupName() + ", getDescription()=" + getDescription() + "]";
	}

}

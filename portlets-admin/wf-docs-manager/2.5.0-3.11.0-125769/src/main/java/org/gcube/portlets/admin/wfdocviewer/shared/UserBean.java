package org.gcube.portlets.admin.wfdocviewer.shared;

import com.extjs.gxt.ui.client.data.BaseModel;

@SuppressWarnings("serial")
public class UserBean extends BaseModel {

	public UserBean() {
	}

	public UserBean(String id, String displayName, String screeName) {
		set("id", id);
		set("displayName", displayName);
		set("screeName", screeName);
	}

	public String getId() {
		return (String) get("id");
	}

	public String getDysplayName() {
		return (String) get("displayName");
	}

	public String getScreenName() {
		return (String) get("screeName");
	}

	public String toString() {
		return getDysplayName();
	}
}
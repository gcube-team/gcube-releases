package org.gcube.data.access.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "UserGroupList")
public class UserGroupList {

	private List<UserGroups> userGroups;

	@XmlElement(name = "UserGroup")
	public List<UserGroups> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(List<UserGroups> userGroups) {
		this.userGroups = userGroups;
	}

	@XmlRootElement(name = "UserGroup")
	public static class UserGroups {

		private String id;
		private String name;
		private boolean enabled;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@XmlAttribute(name="enabled")
		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}
}

package org.gcube.data.access.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "UserList")
public class UserList {

	private List<Users> users;

	@XmlElement(name = "User")
	public List<Users> getUsers() {
		return users;
	}

	public void setUsers(List<Users> users) {
		this.users = users;
	}

	@XmlRootElement(name = "User")
	public static class Users {
		private String id;
		private String extId;
		private String userName;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getExtId() {
			return extId;
		}

		public void setExtId(String extId) {
			this.extId = extId;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}
	}
}

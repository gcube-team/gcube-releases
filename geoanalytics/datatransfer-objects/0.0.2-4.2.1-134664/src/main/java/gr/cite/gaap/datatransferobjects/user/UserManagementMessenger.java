package gr.cite.gaap.datatransferobjects.user;

import java.util.ArrayList;
import java.util.List;

public class UserManagementMessenger {
	List<UserMessenger> users = new ArrayList<UserMessenger>();
	List<RoleMessenger> allRoles = new ArrayList<RoleMessenger>();
	
	public List<UserMessenger> getUsers() {
		return users;
	}
	public void setUsers(List<UserMessenger> users) {
		this.users = users;
	}

	public List<RoleMessenger> getAllRoles() {
		return allRoles;
	}
	public void setAllRoles(List<RoleMessenger> allRoles) {
		this.allRoles = allRoles;
	}
}
package gr.cite.gaap.datatransferobjects.user;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.gaap.datatransferobjects.GeoLocationTag;

public class UserManagementMessenger {
	private static Logger logger = LoggerFactory.getLogger(UserManagementMessenger.class);

	List<UserMessenger> users = new ArrayList<UserMessenger>();
	List<RoleMessenger> allRoles = new ArrayList<RoleMessenger>();
	
	public UserManagementMessenger() {
		super();
		logger.trace("Initialized default contructor for UserManagementMessenger");
	}
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
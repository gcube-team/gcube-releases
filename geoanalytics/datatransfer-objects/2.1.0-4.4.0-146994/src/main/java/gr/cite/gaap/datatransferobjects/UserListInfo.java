package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserListInfo {
	private static Logger logger = LoggerFactory.getLogger(UserListInfo.class);

	public String name;
	public String tenant;
	public boolean active;

	public UserListInfo() {
		logger.trace("Initialized default contructor for UserListInfo");

	}

	public UserListInfo(String name, String customer, boolean active) {
		logger.trace("Initializing UserListInfo...");

		this.name = name;
		this.tenant = customer;
		this.active = active;
		logger.trace("Initialized UserListInfo");

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String customer) {
		this.tenant = customer;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}

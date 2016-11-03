package gr.cite.gaap.datatransferobjects;

public class UserListInfo {
	public String name;
	public String tenant;
	public boolean active;

	public UserListInfo() {
	}

	public UserListInfo(String name, String customer, boolean active) {
		this.name = name;
		this.tenant = customer;
		this.active = active;
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

package gr.cite.gaap.datatransferobjects.user;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.gaap.datatransferobjects.GeoLocationTag;

public class UserMessenger {
	private static Logger logger = LoggerFactory.getLogger(UserMessenger.class);

	private String name = null;
	private UUID id = null;
	private List<RoleMessenger> roles = null;
	
	
	
	public UserMessenger() {
		super();
		logger.trace("Initialized default contructor for UserMessenger");
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	
	public List<RoleMessenger> getRoles() {
		return roles;
	}
	public void setRoles(List<RoleMessenger> roles) {
		this.roles = roles;
	}
}
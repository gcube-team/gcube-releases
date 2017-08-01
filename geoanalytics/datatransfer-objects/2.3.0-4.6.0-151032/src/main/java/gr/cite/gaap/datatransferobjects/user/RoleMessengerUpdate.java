package gr.cite.gaap.datatransferobjects.user;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.gaap.datatransferobjects.GeoLocationTag;

public class RoleMessengerUpdate {
	private static Logger logger = LoggerFactory.getLogger(RoleMessengerUpdate.class);

	private String name = null;
	private String nameNew = null;
	private URI uri = null;
	private String id = null;
	private List<String> roles = new ArrayList<>();
	
	
	public RoleMessengerUpdate() {
		super();
		logger.trace("Initialized default contructor for RoleMessengerUpdate");

	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getNameNew() {
		return nameNew;
	}
	public void setNameNew(String nameNew) {
		this.nameNew = nameNew;
	}
	
	public URI getUri() {
		return uri;
	}
	public void setUri(URI uri) {
		this.uri = uri;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}
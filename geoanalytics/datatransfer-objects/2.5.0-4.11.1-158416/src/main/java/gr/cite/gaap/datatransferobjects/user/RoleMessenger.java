package gr.cite.gaap.datatransferobjects.user;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.gaap.datatransferobjects.GeoLocationTag;

public class RoleMessenger {

	private static Logger logger = LoggerFactory.getLogger(RoleMessenger.class);

	private String name = null;
	private UUID id = null;
	
	public RoleMessenger() {
		super();
		logger.trace("Initialized default contructor for RoleMessenger");
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
}
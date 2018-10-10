package gr.cite.gaap.datatransferobjects;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectGroupMessenger {
	private static Logger logger = LoggerFactory.getLogger(ProjectGroupMessenger.class);

	private String[] users;
	private UserinfoObject uio;
	private UUID projectGroupID;
	private UUID[] usersUUIDs;
	
	public ProjectGroupMessenger() {
		super();
		logger.trace("Initialized default contructor for ProjectGroupMessenger");
	}
	public String[] getUsers() {
		return users;
	}
	public void setUsers(String[] users) {
		this.users = users;
	}
	public UserinfoObject getUio() {
		return uio;
	}
	public void setUio(UserinfoObject uio) {
		this.uio = uio;
	}
	public UUID getProjectGroupID() {
		return projectGroupID;
	}
	public void setProjectGroupID(UUID projectGroupID) {
		this.projectGroupID = projectGroupID;
	}
	public UUID[] getUsersUUIDs() {
		return usersUUIDs;
	}
	public void setUsersUUIDs(UUID[] usersUUIDs) {
		this.usersUUIDs = usersUUIDs;
	}
	
}

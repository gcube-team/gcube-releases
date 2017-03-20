package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectGroupMessenger {
	private static Logger logger = LoggerFactory.getLogger(ProjectGroupMessenger.class);

	String[] users;
	UserinfoObject uio;
	
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
	
}

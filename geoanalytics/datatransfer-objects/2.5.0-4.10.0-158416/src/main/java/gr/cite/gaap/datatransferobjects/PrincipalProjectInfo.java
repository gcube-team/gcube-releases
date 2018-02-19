package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrincipalProjectInfo {
	private static Logger logger = LoggerFactory.getLogger(PrincipalProjectInfo.class);

	private String name = "";
	private String email = "";
	private int numOfProjects = 0;
	
	PrincipalProjectInfo(String name, String email, int numOfProjects){
		logger.trace("Initializing PrincipalProjectInfo...");
		this.name = name;
		this.email = email;
		this.numOfProjects = numOfProjects;
		logger.trace("Initialized PrincipalProjectInfo");
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getNumOfProjects() {
		return numOfProjects;
	}
	public void setNumOfProjects(int numOfProjects) {
		this.numOfProjects = numOfProjects;
	}
	
}

package gr.cite.gaap.datatransferobjects;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserinfoObject {
	private static Logger logger = LoggerFactory.getLogger(UserinfoObject.class);

	private String tenant = "";
	private String fullname = "";
	private String initials = "";
	private String email = "";
	private String projectName = "";
	private String groupName = "";
	private UUID projectId = null;
	private boolean editMode = false;
	
	public UserinfoObject() {
		super();
		logger.trace("Initialized default contructor for UserinfoObject");
	}
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public String getInitials() {
		return initials;
	}
	public void setInitials(String initials) {
		this.initials = initials;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getTenant() {
		return tenant;
	}
	public void setTenant(String tenant) {
		this.tenant = tenant;
	}
	
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	
	public UUID getProjectId() {
		return projectId;
	}
	
	public void setProjectId(UUID projectId) {
		this.projectId = projectId;
	}
	
	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public String calculateInitials() {
		StringBuilder stringBuilder = new StringBuilder();
		String[] splittedString = this.getFullname().trim().split("\\s+");
		
		for(int i=0;i<splittedString.length;i++){
			if(splittedString[i].length() > 0)
				stringBuilder.append(splittedString[i].charAt(0));
		}
		
		return stringBuilder.toString();
	}
}

package gr.cite.gaap.datatransferobjects;

public class UserinfoObject {
	private String tenant = "";
	private String fullname = "";
	private String initials = "";
	private String email = "";
	private String projectName = "";
	private String groupName = "";
	
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

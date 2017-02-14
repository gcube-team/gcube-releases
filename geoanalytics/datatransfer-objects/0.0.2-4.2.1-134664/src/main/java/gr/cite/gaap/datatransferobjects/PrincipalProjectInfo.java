package gr.cite.gaap.datatransferobjects;

public class PrincipalProjectInfo {
	
	private String name = "";
	private String email = "";
	private int numOfProjects = 0;
	
	PrincipalProjectInfo(String name, String email, int numOfProjects){
		this.name = name;
		this.email = email;
		this.numOfProjects = numOfProjects;
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

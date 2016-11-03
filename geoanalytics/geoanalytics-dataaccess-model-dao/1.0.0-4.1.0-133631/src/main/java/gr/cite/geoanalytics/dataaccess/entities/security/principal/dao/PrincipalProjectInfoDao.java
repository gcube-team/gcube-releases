package gr.cite.geoanalytics.dataaccess.entities.security.principal.dao;

public class PrincipalProjectInfoDao {
	
	private String name = "";
	private String email = "";
	private long numOfProjects = 0L;
	
	public PrincipalProjectInfoDao(String name, String email){
		this.name = name;
		this.email = email;
		this.numOfProjects = 0L;
	}
	
	public PrincipalProjectInfoDao(String name, String email, long numOfProjects){
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
	public long getNumOfProjects() {
		return numOfProjects;
	}
	public void setNumOfProjects(int numOfProjects) {
		this.numOfProjects = numOfProjects;
	}
	
}

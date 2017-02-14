package gr.cite.geoanalytics.dataaccess.entities.security.principal.dao;

public class PrincipalProjectInfoDao {
	
	private String name = "";
	private String email = "";
	private long numOfProjects = 0L;
	private String principalname = "";

	public PrincipalProjectInfoDao(String name, String email){
		this.name = name;
		this.email = email;
		this.numOfProjects = 0L;
	}
	
	public PrincipalProjectInfoDao(String name, long numOfProjects){
		this.name = name;
		this.numOfProjects = numOfProjects;
	}
	
	public PrincipalProjectInfoDao(String name, String email, long numOfProjects){
		this.name = name;
		this.email = email;
		this.numOfProjects = numOfProjects;
	}
	
	public PrincipalProjectInfoDao(String name, long numOfProjects, String principalname){
		this.name = name;
		this.principalname = principalname;
		this.numOfProjects = numOfProjects;
	}
	
	public PrincipalProjectInfoDao(String name, String email, long numOfProjects, String principalname){
		this.name = name;
		this.email = email;
		this.numOfProjects = numOfProjects;
		this.principalname = principalname;
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

	public void setNumOfProjects(long numOfProjects) {
		this.numOfProjects = numOfProjects;
	}
	
	public String getPrincipalname() {
		return principalname;
	}

	public void setPrincipalname(String principalname) {
		this.principalname = principalname;
	}
	
}

package gr.cite.geoanalytics.dataaccess.entities.security.principal.dao;

import java.util.UUID;

public class PrincipalProjectInfoDao {
	
	private String name = "";
	private String email = "";
	private long numOfProjects = 0L;
	private String principalname = "";
	private short read = 0;
	private short edit = 0;
	private short delete = 0;
	private UUID id = null;

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
	
	public PrincipalProjectInfoDao(String name, String email, long numOfProjects, UUID id){
		this.name = name;
		this.email = email;
		this.numOfProjects = numOfProjects;
		this.id = id;
	}
	
	public PrincipalProjectInfoDao(String name, long numOfProjects, String principalname){
		this.name = name;
		this.principalname = principalname;
		this.numOfProjects = numOfProjects;
	}
	
	public PrincipalProjectInfoDao(String name, long numOfProjects,
			String principalname, UUID id){
		this.name = name;
		this.principalname = principalname;
		this.numOfProjects = numOfProjects;
		this.id = id;
	}
	
	public PrincipalProjectInfoDao(String name, String email, long numOfProjects, String principalname){
		this.name = name;
		this.email = email;
		this.numOfProjects = numOfProjects;
		this.principalname = principalname;
	}

	public PrincipalProjectInfoDao(String name, String email, long numOfProjects, String principalname, short read,
			short edit, short delete, UUID id) {
		super();
		this.name = name;
		this.email = email;
		this.numOfProjects = numOfProjects;
		this.principalname = principalname;
		this.read = read;
		this.edit = edit;
		this.delete = delete;
		this.id = id;
	}

	public short getRead() {
		return read;
	}

	public void setRead(short read) {
		this.read = read;
	}

	public short getEdit() {
		return edit;
	}

	public void setEdit(short edit) {
		this.edit = edit;
	}

	public short getDelete() {
		return delete;
	}

	public void setDelete(short delete) {
		this.delete = delete;
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

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
	
}

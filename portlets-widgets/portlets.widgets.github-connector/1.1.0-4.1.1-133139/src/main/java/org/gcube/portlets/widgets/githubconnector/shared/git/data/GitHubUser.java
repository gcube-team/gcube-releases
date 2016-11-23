package org.gcube.portlets.widgets.githubconnector.shared.git.data;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class GitHubUser implements Serializable {
	private static final long serialVersionUID = -2781665528432877646L;
	private int id;
	private String name;
	private String login;
	private String company;
	private String location;
	private String url;
	private String email;
	
	public GitHubUser(){
		super();
	}

	public GitHubUser(int id, String name, String login, String company,
			String location, String url, String email) {
		super();
		this.id = id;
		this.name = name;
		this.login = login;
		this.company = company;
		this.location = location;
		this.url = url;
		this.email = email;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "GitHubUser [id=" + id + ", name=" + name + ", login=" + login
				+ ", company=" + company + ", location=" + location + ", url="
				+ url + ", email=" + email + "]";
	}
	
	

	
}

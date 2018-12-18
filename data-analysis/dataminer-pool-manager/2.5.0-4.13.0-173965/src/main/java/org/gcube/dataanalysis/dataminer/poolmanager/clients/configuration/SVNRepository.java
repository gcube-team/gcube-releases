package org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration;

public class SVNRepository 
{

	static final String 	REPOSITORY_URL = "svn.repository",
							REPOSITORY_PATH =  "svn.algo.main.repo",
							REPOSITORY_USERNAME = "svn.repository.username",
							REPOSITORY_PASSWORD =  "svn.repository.password";
	
	private String 	baseUrl,
					path,
					username,
					password;
	
	SVNRepository(String baseUrl, String path, String username, String password) {
		this.baseUrl = baseUrl;
		this.path = path;
		this.username = username;
		this.password = password;
	}
	
	SVNRepository(String baseUrl, String path) {
		this (baseUrl, path, null, null);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public String getPath() {
		return path;
	}
	
	
	
}

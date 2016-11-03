package org.gcube.application.aquamaps.publisher;

import java.io.File;

public class PublisherConfiguration {

	//*************** DB access
	
	private String DBHost;
	private String DBUser;
	private String DBPassword;
	
	//Jetty server config
	
	private File persistenceRoot;
	private String httpServerBasePath;
	private int httpServerPort;
	
	public PublisherConfiguration(String dBHost,
			String dBUser, String dBPassword, File persistenceRoot,
			String httpServerBasePath, int httpServerPort) {
		super();
		DBHost = dBHost;
		DBUser = dBUser;
		DBPassword = dBPassword;
		this.persistenceRoot = persistenceRoot;
		this.httpServerBasePath = httpServerBasePath;
		this.httpServerPort = httpServerPort;
	}

	/**
	 * @return the dBHost
	 */
	public String getDBHost() {
		return DBHost;
	}

	/**
	 * @param dBHost the dBHost to set
	 */
	public void setDBHost(String dBHost) {
		DBHost = dBHost;
	}

	/**
	 * @return the dBUser
	 */
	public String getDBUser() {
		return DBUser;
	}

	/**
	 * @param dBUser the dBUser to set
	 */
	public void setDBUser(String dBUser) {
		DBUser = dBUser;
	}

	/**
	 * @return the dBPassword
	 */
	public String getDBPassword() {
		return DBPassword;
	}

	/**
	 * @param dBPassword the dBPassword to set
	 */
	public void setDBPassword(String dBPassword) {
		DBPassword = dBPassword;
	}

	/**
	 * @return the persistenceRoot
	 */
	public File getPersistenceRoot() {
		return persistenceRoot;
	}

	/**
	 * @param persistenceRoot the persistenceRoot to set
	 */
	public void setPersistenceRoot(File persistenceRoot) {
		this.persistenceRoot = persistenceRoot;
	}

	/**
	 * @return the httpServerBasePath
	 */
	public String getHttpServerBasePath() {
		return httpServerBasePath;
	}

	/**
	 * @param httpServerBasePath the httpServerBasePath to set
	 */
	public void setHttpServerBasePath(String httpServerBasePath) {
		this.httpServerBasePath = httpServerBasePath;
	}

	/**
	 * @return the httpServerPort
	 */
	public int getHttpServerPort() {
		return httpServerPort;
	}

	/**
	 * @param httpServerPort the httpServerPort to set
	 */
	public void setHttpServerPort(int httpServerPort) {
		this.httpServerPort = httpServerPort;
	}
	
}

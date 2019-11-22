package org.gcube.portlets.user.workspace.client.model;

import java.util.Date;


/**
 * The Class FileVersionModel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Feb 20, 2017
 */
public class FileVersionModel extends FileModel {


	/**
	 *
	 */
	private static final long serialVersionUID = 4645522942385738974L;
	public static final String PATH = "path";
	public static final String USER_VERSIONING = "user_versioning";
	public static final String CREATED = "created";
	public static final String IS_CURRENT_VERSION = "is_current_version";

	/**
	 * Instantiates a new file model.
	 */
	public FileVersionModel(){
	}

	/**
	 * Instantiates a new file model.
	 *
	 * @param identifier the identifier
	 * @param name the name
	 * @param path the path
	 * @param userFullName the user full name
	 * @param created the created
	 * @param isCurrentVersion the is current version
	 */
	public FileVersionModel(String identifier, String name, String path, String userFullName, Date created, Boolean isCurrentVersion) {
		setIdentifier(identifier);
		setName(name);
		setPath(path);
		setUser(userFullName);
		setCreated(created);
		setIsCurrentVersion(isCurrentVersion);
		super.initDefaultProperties();
	}


	/**
	 * Sets the checks if is current version.
	 *
	 * @param isCurrentVersion the new checks if is current version
	 */
	private void setIsCurrentVersion(Boolean isCurrentVersion) {

		set(IS_CURRENT_VERSION, isCurrentVersion);

	}

	/**
	 * Sets the created.
	 *
	 * @param created2 the new created
	 */
	private void setCreated(Date created2) {
		set(CREATED, created2);
	}


	/**
	 * Gets the created.
	 *
	 * @return the created
	 */
	public Date getCreated(){
		return (Date) get(CREATED);
	}

	/**
	 * Sets the user.
	 *
	 * @param userVersioning the new user
	 */
	private void setUser(String userVersioning) {
		set(USER_VERSIONING, userVersioning);
	}

	/**
	 * Sets the path.
	 *
	 * @param path the new path
	 */
	private void setPath(String path) {
		set(PATH, path);
	}


	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath(){
		return get(PATH);
	}


	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public String getUser(){
		return get(USER_VERSIONING);
	}


	/**
	 * Checks if is current version.
	 *
	 * @return the boolean
	 */
	public Boolean isCurrentVersion(){
		Object obj = get(IS_CURRENT_VERSION);
		if(obj==null)
			return false;

		return (Boolean) get(IS_CURRENT_VERSION);
	}
}

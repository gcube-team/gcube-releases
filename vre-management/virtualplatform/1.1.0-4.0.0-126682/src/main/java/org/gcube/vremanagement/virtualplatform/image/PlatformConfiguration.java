package org.gcube.vremanagement.virtualplatform.image;

import java.io.File;
import java.net.URL;

import org.gcube.vremanagement.virtualplatform.model.TargetPlatform;

/**
 * Configuration for a {@link TargetPlatform}
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public final class PlatformConfiguration {

	private String name,user,password,platformClass;
	private short version,minorVersion=0;
	private URL baseURL;
	private File[] resources;
	private File folder;
	private boolean requireDedicatedClassloader;
	
	protected PlatformConfiguration() {}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	protected void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	protected void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	protected void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the platformClass
	 */
	public String getPlatformClass() {
		return platformClass;
	}

	/**
	 * @param platformCass the platformClass to set
	 */
	protected void setPlatformClass(String platformClass) {
		this.platformClass = platformClass;
	}

	/**
	 * @return the baseURL
	 */
	public URL getBaseURL() {
		return baseURL;
	}

	/**
	 * @param baseURL the baseURL to set
	 */
	protected void setBaseURL(URL baseURL) {
		this.baseURL = baseURL;
	}

	/**
	 * @return the resources
	 */
	public File[] getResources() {
		return resources;
	}

	/**
	 * @param resources the resources to set
	 */
	protected void setResources(File[] resources) {
		this.resources = resources;
	}

	/**
	 * @return the folder
	 */
	public File getFolder() {
		return folder;
	}

	/**
	 * @param folder the folder to set
	 */
	protected void setFolder(File folder) {
		this.folder = folder;
	}

	/**
	 * 
	 * @param requireDedicatedClassloader <tt>true</tt> if the platform requires a dedicated classloader, <tt>false</tt> otherwise
	 */
	public void setRequireDedicatedClassloader(boolean requireDedicatedClassloader) {
		this.requireDedicatedClassloader = requireDedicatedClassloader;
	}

	/**
	 * 
	 * @return <tt>true</tt> if the platform requires a dedicated classloader, <tt>false</tt> otherwise
	 */
	public boolean requireDedicatedClassloader() {
		return requireDedicatedClassloader;
	}

	/**
	 * @return the version
	 */
	public short getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(short version) {
		this.version = version;
	}

	/**
	 * @return the minor version
	 */
	public short getMinorVersion() {
		return minorVersion;
	}

	/**
	 * @param minorVersion the minor version to set
	 */
	public void setMinorVersion(short minorVersion) {
		this.minorVersion = minorVersion;
	}



}

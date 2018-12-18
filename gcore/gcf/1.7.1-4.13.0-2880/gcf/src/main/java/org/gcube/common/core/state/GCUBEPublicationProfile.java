package org.gcube.common.core.state;

import java.io.File;

/**
 * A publication profile for WS-Resources partly instantiated from the JNDI configuration
 * of the port-type.
 * 
 * @see <code>GCUBEPortTypeContext.getProfile()</code>
 *
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
public class GCUBEPublicationProfile {

	/**
	 * The mode of publication.
	 *  
	 */
	String mode = "pull";

	/**
	 *  The name of the publication configuration file.
	 * 
	 */  
	String fileName;
	
	/** 
	 * The publication configuration.
	 */
	String profile;
	
	/**
	 * The path of the publication configuration file.
	 */
	String path;
	
	/**
	 * Returns the name of the publication configuration file.
	 * @return the name.
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * Sets the name of the publication configuration file.
	 * @param fileName the file name.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Returns the mode of publication.
	 * @return the mode.
	 */
	public String getMode() {
		return this.mode;
	}

	/**
	 * Sets the mode of publication.
	 * 
	 * @param mode the mode,
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * Returns the publication profile.
	 * 
	 * @return the profile
	 */
	public String toString() {
		return this.profile;
	}

	/** Sets the publication profile.
	 * 
	 * @param profile the profile.
	 */
	public void setProfile(String profile) {
		this.profile = profile;
	}

	/**
	 * Gets the absolute file name.
	 * @return the absolute file name. 
	 */
	public String getAbsoluteFileName() {
		return path + File.separator + this.getFileName();
	}

	/**
	 * Sets the absolute path.
	 * @param path the path.
	 */
	public void setAbsolutePath(String path) {
		this.path = path;
	}

	/**
	 * Gets the absolute path.
	 * @return path the path.
	 */
	public String getAbsolutePath() {
		return path;
	}


}

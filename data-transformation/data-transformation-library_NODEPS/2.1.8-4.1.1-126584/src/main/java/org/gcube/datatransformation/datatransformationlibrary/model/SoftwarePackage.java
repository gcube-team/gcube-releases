package org.gcube.datatransformation.datatransformationlibrary.model;

import java.net.URL;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * <tt>SoftwarePackage</tt> is a package which contains one or more programs or dependencies of a program.
 * </p>
 */
public class SoftwarePackage {

	private String id;
	
	private URL location;
	
	/**
	 * Returns the id of the package.
	 * @return The id of hte package.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the id of the package.
	 * @param id The id of the package.
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Returns the location of the package.
	 * @return the location of the package.
	 */
	public URL getLocation() {
		return location;
	}
	
	/**
	 * Sets the location of the package.
	 * @param location the location of the package.
	 */
	public void setLocation(URL location) {
		this.location = location;
	}
}

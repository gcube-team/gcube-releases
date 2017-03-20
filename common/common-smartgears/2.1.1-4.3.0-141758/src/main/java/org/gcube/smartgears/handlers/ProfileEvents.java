package org.gcube.smartgears.handlers;

/**
 * Profile lifetime events for container and application.
 * 
 * @author Fabio Simeoni
 *
 */
public class ProfileEvents {

	/**
	 * The event that signals the publication of the profile.
	 */
	public static final String published ="published";

	/**
	 * The event that signals a change to the profile.
	 */
	public static final String changed ="changed";
	
	public static final String addToContext = "addToContext";
	
	public static final String removeFromContext = "removeFromContext";
}

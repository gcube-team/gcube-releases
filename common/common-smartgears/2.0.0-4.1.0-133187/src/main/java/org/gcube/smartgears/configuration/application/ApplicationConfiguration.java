package org.gcube.smartgears.configuration.application;

import java.util.Set;

import org.gcube.smartgears.configuration.Mode;
import org.gcube.smartgears.persistence.Persistence;

/**
 * The configuration of the application.
 * 
 * @author Fabio Simeoni
 *
 */
public interface ApplicationConfiguration {

	
	/**
	 * Returns the management mode of the application.
	 * @return the management mode
	 */
	Mode mode();
	
	/**
	 * Returns true if the application is secure (accessible only in https).
	 * @return secure or not
	 */
	boolean secure();
	
	/**
	 * Returns the context path of the application
	 * @return the context path
	 */
	String context();
	
	
	/**
	 * Sets the context path of the application
	 * @param context the context path
	 * @return this configuration
	 */
	ApplicationConfiguration context(String context);
	
	/**
	 * Sets the management mode of this application.
	 * @param the management mode
	 * @return this configuration
	 */
	ApplicationConfiguration mode(Mode mode);

	
	ApplicationConfiguration secure(boolean value);
	
	/**
	 * Returns the name of the application.
	 * @return the name
	 */
	String name();
	
	
	/**
	 * Sets the name of the application.
	 * @param name the name
	 * @return this configuration
	 */
	ApplicationConfiguration name(String name);

	/**
	 * Returns the class of the application
	 * @return the class
	 */
	String serviceClass();

	/**
	 * Sets the class of the application.
	 * @param serviceClass the class
	 * @return this configuration
	 */
	ApplicationConfiguration serviceClass(String serviceClass);

	/**
	 * Returns the version of the application.
	 * @return the version
	 */
	String version();

	/**
	 * Sets the version of the application.
	 * @param version the version
	 * @return this configuration
	 */
	ApplicationConfiguration version(String version);

	/**
	 * Returns the description of the application.
	 * @return the description
	 */
	String description();

	/**
	 * Sets the description of the application.
	 * @param description the description
	 * @return this configuration
	 */
	ApplicationConfiguration description(String description);
	
	
	/**
	 * Returns the tokens in which the application operates when it first starts.
	 * @return the tokens
	 */
	Set<String> startTokens();

	/**
	 * Sets the tokens in which the application operates when it first starts.
	 * @param scopes the scopes
	 * @return this configuration
	 */
	ApplicationConfiguration startTokens(Set<String> tokens);
	
	
	/**
	 * Returns the persistence manager of the application.
	 * @return the manager
	 */
	Persistence persistence();
	
	
	/**
	 * Returns a set of request paths that should not be subjected to request management.
	 * @return the set of exclude paths.
	 */
	Set<String> excludes();
	

	/**
	 * Sets the persistence manager of the application.
	 * @param manager the manager
	 * @return this configuration
	 */
	ApplicationConfiguration persistence(Persistence manager);


	/**
	 * Validates this configuration.
	 * 
	 * @throws IllegalStateException if the configuration is not valid
	 */
	void validate();

	
	/**
	 * Merges this configuration with another configuration
	 * @param config the other configuration
	 */
	void merge(ApplicationConfiguration config);


}
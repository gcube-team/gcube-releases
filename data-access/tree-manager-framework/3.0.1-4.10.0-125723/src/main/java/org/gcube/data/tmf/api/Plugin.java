package org.gcube.data.tmf.api;

import java.util.List;

/**
 * Entry-point to the information and components of the plugin.
 * <p>
 * Implementations must have a no-arg constructor to be reflectively instantiated by the service.
 * 
 * @author Fabio Simeoni
 *
 */
public interface Plugin {

	/**
	 * Returns the plugin name
	 * @return the name;
	 */
	String name();
	
	
	/**
	 * Returns the plugin description
	 * @return the description
	 */
	String description();
	
	
	/**
	 * Return the descriptive properties of the plugin
	 * @return the properties
	 */
	List<Property> properties();
	
	/**
	 * Returns the {@link SourceBinder} of the plugin.
	 * 
	 * @return the binder
	 */
	SourceBinder binder();
	
	/**
	 * Returns schemas for the requests accepted by the plugin.
	 * <p>
	 * The service will publish these schemas along with other information about
	 * the plugin.
	 * <p>
	 * Plugins may use any schema language, though some clients upstream may
	 * converge on standards such as XML Schema, which is thus recommended.
	 * 
	 * @return an {@link String}-based representation of the request schemas
	 */
	 List<String> requestSchemas();
	 
	/**
	 * Returns <code>true</code> if the plugin does not support replication across services.
	 * @return <code>true</code> if the plugin does not support replication across services, <code>false</code> otherwise
	 */
	boolean isAnchored();
}

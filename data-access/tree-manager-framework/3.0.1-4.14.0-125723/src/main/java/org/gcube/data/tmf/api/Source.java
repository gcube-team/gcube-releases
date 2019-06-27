/**
 * 
 */
package org.gcube.data.tmf.api;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * A model of a data source managed by the plugin.
 * 
 * <p>
 * This model provides information that the service uses to dispatch client requests to
 * the plugin ({@link SourceReader}, {@link SourceWriter}), as well as to manage
 * its lifetime ({@link SourceLifecycle}). <br>
 * It also provides information about the source that the service publishes in
 * the infrastructure on behalf of the plugin (identifier, data type,
 * description, date of creation and last modification, cardinality, etc). <br>
 * This information must be configured by the plugin prior to source
 * initialisation and, when appropriate, managed over the source;s lifetime
 * (e.g. updates to cardinality and last modification dates).
 * 
 * <p>
 * This model also accepts information from the service that the plugin uses to access
 * the local environment ({@link Environment}) and to communicate back to the
 * service events of the relevance about the source ({@link SourceNotifier}). <br>
 * This information is set by the service prior to source initialisation (cf.
 * {@link SourceLifecycle#init()} and the plugin should not use it until then.
 * 
 * @author Fabio Simeoni
 * 
 */
public interface Source extends Serializable {

	QName TREE_TYPE=new QName(Constants.NS,"tree");
	
	/**
	 * Returns the identifier of the source.
	 * 
	 * @return the identifier
	 */
	String id();

	/**
	 * Returns the tree types of the source.
	 * 
	 * @return the types
	 */
	List<QName> types();

	/**
	 * Returns the name of the source.
	 * 
	 * @return the name
	 */
	String name();

	/**
	 * Returns the free-form description of the source.
	 * 
	 * @return the description
	 */
	String description();
	
	/**
	 * Returns the properties of the source.
	 * 
	 * @return the properties
	 */
	List<Property> properties();

	/**
	 * Returns the creation time of the source.
	 * 
	 * @return the creation time
	 */
	Calendar creationTime();

	/**
	 * Returns <code>true</code> if the source is a user-level source,
	 * <code>false</code> if it is instead system-level source.
	 * 
	 * @return <code>true</code> if the source is a user-level source,
	 *         <code>false</code> otherwise
	 */
	boolean isUser();

	/**
	 * Returns the cardinality of the source.
	 * 
	 * @return the cardinality
	 */
	Long cardinality();

	/**
	 * Returns the time in which the source was last modified.
	 * 
	 * @return the last modification time
	 */
	Calendar lastUpdate();

	/**
	 * Returns the {@link SourceLifecycle}.
	 * 
	 * @return the lifecycle
	 */
	SourceLifecycle lifecycle();

	/**
	 * Returns the {@link SourceReader}.
	 * 
	 * @return the reader, or <code>null</code> if the source is write-only
	 */
	SourceReader reader();

	/**
	 * Returns the {@link SourceWriter}.
	 * 
	 * @return the writer, or <code>null</code> if the source is read-only
	 */
	SourceWriter writer();

	/**
	 * Returns the {@link SourceNotifier}.
	 * <p>
	 * This is invoked by the service prior to source initialisation.
	 * The service subscribes to the provider so as to receive notification of
	 * events fired from the plugin.
	 * 
	 * @param notifier
	 *            the notifier
	 */
	void setNotifier(SourceNotifier notifier);

	/**
	 * Returns the {@link SourceNotifier}.
	 * 
	 * @return the notifier
	 */
	SourceNotifier notifier();

	/**
	 * Returns the local {@link Environment} of the plugin.
	 * 
	 * @return the environment
	 */
	Environment environment();

	/**
	 * Sets the local {@link Environment} of the plugin.
	 * <p>
	 * It is invoked by the service prior to source initialisation.
	 * 
	 * @param env the
	 *            environment
	 */
	void setEnvironment(Environment env);

}

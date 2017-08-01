/**
 * 
 */
package org.gcube.data.tmf.api;

import java.io.Serializable;

import org.gcube.data.tmf.api.exceptions.InvalidRequestException;
import org.gcube.data.tmf.impl.LifecycleAdapter;
import org.w3c.dom.Element;

/**
 * A set of callbacks made by the service to the plugin at key points in the
 * lifecycle of a {@link Source}, i.e. when the plugin needs to start, stop,
 * refine, or resume its management of the corresponding data source.
 * <p>
 * Plugins are expected to associate sources with implementations of this
 * interface, before the {@link SourceBinder} returns the bound sources to the
 * service. When they need to implement only a subset of the callbacks, they can
 * conveniently subclass the {@link LifecycleAdapter}.
 * <p>
 * 
 * @author Fabio Simeoni
 * 
 * @see Source#lifecycle()
 * @see SourceBinder
 * @see LifecycleAdapter
 */
public interface SourceLifecycle extends Serializable {

	/**
	 * Tells the plugin to start managing the data source.
	 * <p>
	 * This occurs during the binding the data source, immediately after the
	 * {@link SourceBinder} has returned the corresponding {@link Source} to the
	 * service.
	 * <p>
	 * Plugins may implement this method to initialise the state of the
	 * {@link Source} and schedule any operation that relates to the management
	 * of the data source.
	 * <p>
	 * If this method fails, the binding between the plugin and the data source
	 * also fails.
	 * 
	 * @throws Exception
	 *             if the operation cannot be completed
	 * 
	 * @see SourceBinder#bind(Element)
	 */
	void init() throws Exception;

	/**
	 * Gives the plugin a client request with which it can refine its management
	 * of the data source.
	 * 
	 * <p>
	 * This occurs during the binding of a data source, when the
	 * {@link SourceBinder} returns to the service a {@link Source} with the
	 * same identifier as the {@link Source} associated with this instance. This
	 * indicates that the plugin is already managing the corresponding data
	 * source and that the client request should be used to re-configure the
	 * existing {@link Source} rather than initialise the new one.
	 * <p>
	 * Plugins may implement this method to refine the state of a {@link Source}
	 * or to change otherwise its management of the corresponding data source.
	 * <p>
	 * If this method fails, the client request fails and the plugin retains its
	 * current management regime for the data source.
	 * 
	 * @throws InvalidRequestException
	 *             if the client request is incompatible with the current
	 *             management regime of the data source.
	 * @throws Exception
	 *             if the operation cannot be completed for any other reason
	 * 
	 * @see SourceBinder#bind(Element)
	 */
	void reconfigure(Element request) throws InvalidRequestException, Exception;

	/**
	 * Tells the plugin to resume its management of the data source.
	 * <p>
	 * This occurs when the service is restoring its state from persistent
	 * storage after a restart of the container.
	 * <p>
	 * Plugins may implement this method to restart scheduled operations which
	 * relate to the management of the data source.
	 * <p>
	 * If this method fails, all subsequent client requests about the data
	 * source fail.
	 * 
	 * @throws Exception
	 *             if the operation cannot be completed
	 */
	void resume() throws Exception;

	/**
	 * Tells the plugin to stop its management of the data source until further
	 * notice.
	 * <p>
	 * This may occur when the container is shutting down, or when the service
	 * is serialising its state to persistence storage so as to conserve memory
	 * resources. 
	 * <p>
	 * Plugins may implement this method to gracefully stop any operation that
	 * relates to the management of the data source.
	 * 
	 */
	void stop();

	/**
	 * Tells the plugin to stop its management of the data source for good.
	 * <p>
	 * This occurs when clients indicate that access to the data source is no
	 * longer needed.
	 * <p>
	 * Plugins may implement this method to terminate any scheduled operation
	 * that relates to the management of the data source, or to perform any
	 * other relevant form of state cleanup.
	 * 
	 */
	void terminate();
}

/**
 * 
 */
package org.gcube.vremanagement.executor.persistence;

import java.util.UUID;

import org.gcube.vremanagement.executor.plugin.Plugin;
import org.gcube.vremanagement.executor.plugin.PluginState;
import org.gcube.vremanagement.executor.plugin.PluginStateNotification;

/**
 * Model the connector which create or open the connection to DB.
 * Every implementation MUST take in account to store/query the records
 * on the current scope which is not passed as argument but MUSt be retrieved
 * using {#org.gcube.common.scope.api.ScopeProvider} facilities
 * i.e. ScopeProvider.instance.get()
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public abstract class SmartExecutorPersistenceConnector implements PluginStateNotification {

		/**
	 * Close the connection to DB
	 * @throws Exception if fails
	 */
	public abstract void close() throws Exception;
	
	/**
	 * Retrieve the status of the iterationNumber (passed as parameter) of a running/run {@link Plugin} which is/was identified 
	 * by the UUID passed as parameter
	 * @param uuid the execution identifier of the running/run {@link Plugin}
	 * @param iterationNumber the 
	 * @return the actual/last {@link PluginState} of the Plugin
	 * @throws Exception if fails
	 */
	public abstract PluginState getPluginInstanceState(UUID uuid, int iterationNumber)
			throws Exception;
	/**
	 * Retrieve the status of the iterationNumber of the last running/run {@link Plugin} which is/was identified 
	 * by the UUID passed as parameter
	 * @param uuid the execution identifier of the running/run {@link Plugin}
	 * @return the actual/last {@link PluginState} of the Plugin
	 * @throws Exception if fails
	 */
	public abstract PluginState getLastPluginInstanceState(UUID uuid)
			throws Exception;

}

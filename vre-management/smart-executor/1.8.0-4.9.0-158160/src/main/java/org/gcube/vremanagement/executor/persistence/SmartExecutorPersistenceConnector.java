/**
 * 
 */
package org.gcube.vremanagement.executor.persistence;

import java.util.HashMap;
import java.util.UUID;

import org.gcube.common.clients.exceptions.DiscoveryException;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.smartgears.ContextProvider;
import org.gcube.vremanagement.executor.client.plugins.ExecutorPlugin;
import org.gcube.vremanagement.executor.client.plugins.query.filter.SpecificEndpointDiscoveryFilter;
import org.gcube.vremanagement.executor.client.proxies.SmartExecutorProxy;
import org.gcube.vremanagement.executor.exception.ExecutorException;
import org.gcube.vremanagement.executor.json.ObjectMapperManager;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.gcube.vremanagement.executor.plugin.PluginState;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;
import org.gcube.vremanagement.executor.plugin.PluginStateNotification;
import org.gcube.vremanagement.executor.plugin.RunOn;
import org.gcube.vremanagement.executor.scheduledtask.ScheduledTask;
import org.gcube.vremanagement.executor.scheduledtask.ScheduledTaskPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Model the connector which create or open the connection to DB.
 * @author Luca Frosini (ISTI - CNR)
 */
public abstract class SmartExecutorPersistenceConnector extends PluginStateNotification implements ScheduledTaskPersistence {

	private static final Logger logger = LoggerFactory
			.getLogger(SmartExecutorPersistenceConnector.class);
	
	public SmartExecutorPersistenceConnector() {
		super(new HashMap<String, String>());
	}
	
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
	public abstract PluginStateEvolution getPluginInstanceState(UUID uuid, int iterationNumber) throws Exception;
	
	/**
	 * Retrieve the status of the iterationNumber of the last running/run {@link Plugin} which is/was identified 
	 * by the UUID passed as parameter
	 * @param uuid the execution identifier of the running/run {@link Plugin}
	 * @return the actual/last {@link PluginState} of the Plugin
	 * @throws Exception if fails
	 */
	public abstract PluginStateEvolution getLastPluginInstanceState(UUID uuid) throws Exception;

	
	protected boolean isOrphan(ScheduledTask scheduledTask) throws Exception {
		try {
			UUID uuid = scheduledTask.getUUID();

			RunOn runOn = scheduledTask.getRunOn();
			if(runOn==null){
				return true;
			}
			
			try {
				HostingNode hostingNode = ContextProvider.get().container().profile(HostingNode.class);
				String hnAddress = hostingNode.profile().description().name();
				
				if(runOn.getHostingNode().getAddress().compareTo(hnAddress)==0){
					return true;
				}
			}catch (Exception e) {
				logger.error("Unable to check if current hosting node is the same of the one in ScheduledTask", e);
			}
						
			String address = runOn.getEService().getAddress();
			
			SpecificEndpointDiscoveryFilter specificEndpointDiscoveryFilter = new SpecificEndpointDiscoveryFilter(
					address);

			String pluginName = scheduledTask.getLaunchParameter()
					.getPluginName();

			try {
				SmartExecutorProxy proxy = ExecutorPlugin
						.getExecutorProxy(pluginName, null, null,
								specificEndpointDiscoveryFilter).build();
				proxy.getStateEvolution(uuid.toString());
				logger.trace("{} is not orphan.", ObjectMapperManager
						.getObjectMapper().writeValueAsString(scheduledTask));
				return false;
			} catch (DiscoveryException | ExecutorException e) {
				// The instance was not found or the request failed.
				// The scheduledTask is considered orphan
				logger.trace("{} is considered orphan.", ObjectMapperManager
						.getObjectMapper().writeValueAsString(scheduledTask), e);
				return true;
			} catch (Throwable e) {
				// The scheduledTask is NOT considered orphan
				logger.trace("{} is NOT considered orphan.", ObjectMapperManager
						.getObjectMapper().writeValueAsString(scheduledTask), e);
				return false;
			}
		} catch (Exception e) {
			String string = ObjectMapperManager.getObjectMapper()
					.writeValueAsString(scheduledTask);
			logger.error("Error while checking orphanity of " + string
					+ ". Considering as not orphan.", e);
		}

		return false;
	}
	
}

package org.gcube.vremanagement.executor.persistence;

import java.util.UUID;

import org.gcube.vremanagement.executor.plugin.Plugin;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.gcube.vremanagement.executor.plugin.PluginState;

/**
 * This class is used to persist the execution state
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@Deprecated
public abstract class Persistence<P extends PersistenceConnector> {

	protected final String name;
	protected final P persistenceConnector;
	protected final UUID uuid;
	
	/**
	 * Constructor
	 * @param persistenceConnector the {@link PersistenceConnector} which
	 * created and/or opened the connection to DB
	 * @param name the name get from {@link PluginDeclaration}) related to the 
	 * running {@link Plugin}
	 * @param uuid the execution identifier
	 */
	@Deprecated
	public Persistence(P persistenceConnector, String name, UUID uuid){
		this.name = name;
		this.persistenceConnector = persistenceConnector;
		this.uuid = uuid;
	}
	
	
	/**
	 * Persist the new state of plugin
	 * @param timestamp the time of the new {@link PluginState}
	 * @param pluginState the {@link PluginState} value
	 * @throws Exception if fails
	 */
	@Deprecated
	public abstract void addEvolution(long timestamp, PluginState pluginState) 
			throws Exception;


	/**
	 * @return the actual (or the last temporal) {@link PluginState} value
	 * @throws Exception if fails to retrieve the {@link PluginState} from DB
	 */
	@Deprecated
	public PluginState getState() throws Exception {
		return persistenceConnector.getLastPluginInstanceState(uuid);
	}
	
}

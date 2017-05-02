/**
 * 
 */
package org.gcube.informationsystem.sweeper;

import java.util.HashMap;
import java.util.Map;

import org.gcube.vremanagement.executor.plugin.Plugin;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scheduled Task Sweeper find orphaned scheduled task on SmartExecutor 
 * persistence and make them available. This requires interaction with 
 * SmartExecutor Persistence and with IS t find orphaned Running Instances
 * @author Luca Frosini (ISTI - CNR)
 */
public class ISSweeperPluginDeclaration implements PluginDeclaration {

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(ISSweeperPlugin.class);
	
	/**
	 * Plugin name used by the Executor to retrieve this class
	 */
	public static final String NAME = "ISSweeper";
	public static final String DESCRIPTION = 
		"IS Sweeper find expired (not updated for "
		+ ISSweeperPlugin.DEFAULT_EXPIRING_MINUTES_TIMEOUT + " minutes) Hosting Nodes "
		+ "and set their status to " + Sweeper.UNREACHABLE + ", dead (not "
		+ "updated for " + ISSweeperPlugin.DEFAULT_DEAD_DAYS_TIMEOUT + " days) Hosting "
		+ "Nodes and remove them from IS. Moreover it find orphan Running "
		+ "Instances and remove them from IS.";
	public static final String VERSION = "1.0.0";
	
	/**{@inheritDoc}*/
	@Override
	public void init() {
		logger.debug(String.format("%s initialized", ISSweeperPlugin.class.getSimpleName()));
	}
	
	/**{@inheritDoc}*/
	@Override
	public String getName() {
		return NAME;
	}
	
	/**{@inheritDoc}*/
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	/**{@inheritDoc}*/
	@Override
	public String getVersion() {
		return VERSION;
	}
	
	/**{@inheritDoc}*/
	@Override
	public Map<String, String> getSupportedCapabilities() {
		Map<String, String> discoveredCapabilities = new HashMap<String, String>();
		// No capabilities to discover
		return discoveredCapabilities;
	}

	/**{@inheritDoc}*/
	@Override
	public Class<? extends Plugin<? extends PluginDeclaration>> getPluginImplementation() {
		return ISSweeperPlugin.class;
	}
	
}

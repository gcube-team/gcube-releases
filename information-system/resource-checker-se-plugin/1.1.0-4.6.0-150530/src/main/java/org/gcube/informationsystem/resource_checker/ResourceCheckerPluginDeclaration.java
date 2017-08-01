/**
 * 
 */
package org.gcube.informationsystem.resource_checker;

import java.util.HashMap;
import java.util.Map;

import org.gcube.vremanagement.executor.plugin.Plugin;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The resource-checker-se-plugin declaration class.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ResourceCheckerPluginDeclaration implements PluginDeclaration {

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(ResourceCheckerPluginDeclaration.class);

	/**
	 * Plugin name used by the Executor to retrieve this class
	 */
	public static final String NAME = "resource-checker-se-plugin";
	public static final String DESCRIPTION = "The resource-checker-plugin has the role to check the existence of some resources in all Infrastructure's contexts.";
	public static final String VERSION = "1.1.0";

	/**{@inheritDoc}*/
	@Override
	public void init() {
		logger.debug(String.format("%s initialized", ResourceCheckerPluginDeclaration.class.getSimpleName()));
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
		return ResourceCheckerPlugin.class;
	}

	@Override
	public String toString(){
		return String.format("%s : %s - %s - %s - %s - %s", 
				this.getClass().getSimpleName(), 
				getName(), getVersion(), getDescription(), 
				getSupportedCapabilities(), 
				getPluginImplementation().getClass().getSimpleName());
	}

}

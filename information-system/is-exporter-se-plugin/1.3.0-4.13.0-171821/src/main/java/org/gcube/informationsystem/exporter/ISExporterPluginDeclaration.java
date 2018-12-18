/**
 * 
 */
package org.gcube.informationsystem.exporter;

import java.util.HashMap;
import java.util.Map;

import org.gcube.vremanagement.executor.plugin.Plugin;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ISExporterPluginDeclaration implements PluginDeclaration {

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(ISExporterPlugin.class);
	
	/**
	 * Plugin name used by the Executor to retrieve this class
	 */
	public static final String NAME = "ISExporter";
	public static final String DESCRIPTION = "IS Exporter";
	public static final String VERSION = "1.0.0";
	
	/**{@inheritDoc}*/
	@Override
	public void init() {
		logger.debug(String.format("%s initialized", ISExporterPlugin.class.getSimpleName()));
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
		return ISExporterPlugin.class;
	}
	
}

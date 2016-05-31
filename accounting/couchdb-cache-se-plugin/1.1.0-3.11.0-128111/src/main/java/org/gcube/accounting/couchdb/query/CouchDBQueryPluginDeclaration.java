/**
 * 
 */
package org.gcube.accounting.couchdb.query;

import java.util.HashMap;
import java.util.Map;

import org.gcube.vremanagement.executor.plugin.Plugin;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class CouchDBQueryPluginDeclaration implements PluginDeclaration {
	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(CouchDBQueryPlugin.class);
	
	/**
	 * Plugin name used by the Executor to retrieve this class
	 */
	public static final String NAME = "CouchDBCache";
	
	public static final String DESCRIPTION = "Smart Executor Plugin to Query CouchDB View To Force couchDb to Create Internal Cache (B-Tree)";
	
	public static final String VERSION = "1.1.0";
	
	/**{@inheritDoc}*/
	@Override
	public void init() {
		logger.debug(String.format("%s initialized", CouchDBQueryPlugin.class.getSimpleName()));
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
		return discoveredCapabilities;
	}
	
	/**{@inheritDoc}*/
	@Override
	public Class<? extends Plugin<? extends PluginDeclaration>> getPluginImplementation() {
		return CouchDBQueryPlugin.class;
	}
	
}

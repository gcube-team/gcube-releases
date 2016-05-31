/**
 * 
 */
package org.gcube.socialnetworking.socialdataindexer;

import java.util.HashMap;
import java.util.Map;

import org.gcube.vremanagement.executor.plugin.Plugin;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The social data indexer plugin declaration class.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 *
 */
public class SocialDataIndexerPluginDeclaration implements PluginDeclaration {

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(SocialDataIndexerPluginDeclaration.class);

	/**
	 * Plugin name used by the Executor to retrieve this class
	 */
	public static final String NAME = "social-data-indexer-plugin";
	public static final String DESCRIPTION = "The social-data-indexer-plugin has the role to index data contained"
			+ "	in the Cassandra cluster using an elasticsearch index to support full-text search.";
	public static final String VERSION = "1.0.0";

	/**{@inheritDoc}*/
	@Override
	public void init() {
		logger.debug(String.format("%s initialized", SocialDataIndexerPluginDeclaration.class.getSimpleName()));
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
		return SocialDataIndexerPlugin.class;
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

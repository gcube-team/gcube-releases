/**
 * 
 */
package org.acme;

import java.util.HashMap;
import java.util.Map;

import org.gcube.vremanagement.executor.plugin.Plugin;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class HelloWorldPluginDeclaration implements PluginDeclaration {
	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(HelloWorldPlugin.class);
	
	/**
	 * Plugin name used by the Executor to retrieve this class
	 */
	public static final String NAME = "HelloWorld";
	
	public static final String DESCRIPTION = "Hello World Description";
	
	public static final String VERSION = "1.1.2";
	
	/**{@inheritDoc}*/
	@Override
	public void init() {
		logger.debug(String.format("%s initialized", HelloWorldPlugin.class.getSimpleName()));
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
		discoveredCapabilities.put("FakeKey", "FakeValue");
		return discoveredCapabilities;
	}

	/**{@inheritDoc}*/
	@Override
	public Class<? extends Plugin<? extends PluginDeclaration>> getPluginImplementation() {
		return HelloWorldPlugin.class;
	}
	
	@Override
	public String toString(){
		return String.format("{"
					+ "name:%s,"
					+ "version:%s,"
					+ "description:%s,"
					+ "pluginImplementation:%s,"
				+ "}",
				getName(), 
				getVersion(), 
				getDescription(),
				getPluginImplementation().getClass().getSimpleName());
	}
	
}

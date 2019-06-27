/**
 * 
 */
package org.gcube.accounting.insert.rstudio.plugin;

import java.util.HashMap;
import java.util.Map;

import org.gcube.vremanagement.executor.plugin.Plugin;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alessandro Pieve (ISTI - CNR) 
 *
 */
public class AccountingInsertRstudioPluginDeclaration implements PluginDeclaration {
	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(AccountingInsertRstudioPlugin.class);
	
	/**
	 * Plugin name used by the Executor to retrieve this class
	 */
	public static final String NAME = "Accounting-Insert-RStudio";
	
	public static final String DESCRIPTION = "This plugin is used to insert RStudio storage record";
	
	public static final String VERSION = "1.0.0";
	
	/**{@inheritDoc}*/
	@Override
	public void init() {
		logger.debug(String.format("%s initialized", AccountingInsertRstudioPlugin.class.getSimpleName()));
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
		return AccountingInsertRstudioPlugin.class;
	}

	@Override
	public String toString() {
		return "AccountingInsertRstudioPluginDeclaration [getName()=" + getName()
				+ ", getDescription()=" + getDescription() + ", getVersion()="
				+ getVersion() + ", getSupportedCapabilities()="
				+ getSupportedCapabilities() + ", getPluginImplementation()="
				+ getPluginImplementation() + "]";
	}
	
	
}

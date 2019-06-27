package org.gcube.dataharvest;

import java.util.HashMap;
import java.util.Map;

import org.gcube.vremanagement.executor.plugin.Plugin;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eric Perrone (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public class DataHarvestPluginDeclaration implements PluginDeclaration {
	
	private static Logger logger = LoggerFactory.getLogger(DataHarvestPluginDeclaration.class);
	
	public static final String NAME = "AccountingDataHarvester";
	public static final String DESCRIPTION = "Data Harvest for Accounting Summary Dashboard";
	public static final String VERSION = "1.0.0";

	/**{@inheritDoc}*/
	@Override
	public void init() {
		logger.debug(String.format("%s initialized", AccountingDataHarvesterPlugin.class.getSimpleName()));
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
		return AccountingDataHarvesterPlugin.class;
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

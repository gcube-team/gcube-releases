/**
 * 
 */
package org.gcube.vremanagement.executor.plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
class PluginDeclarationImpl implements PluginDeclaration {

	protected String name;
	protected String description;
	protected String version;
	protected Map<String, String> supportedCapabilities;
	
	protected PluginDeclarationImpl(){
		supportedCapabilities = new HashMap<String, String>();
	}
	
	@Override
	public void init() throws Exception {
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public Map<String, String> getSupportedCapabilities() {
		return supportedCapabilities;
	}

	@Override
	public Class<? extends Plugin<? extends PluginDeclaration>> getPluginImplementation() {
		return null;
	}

}

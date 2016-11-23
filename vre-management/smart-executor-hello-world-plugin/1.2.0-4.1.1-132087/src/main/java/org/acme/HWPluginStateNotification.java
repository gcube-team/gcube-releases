/**
 * 
 */
package org.acme;

import java.util.Map;

import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;
import org.gcube.vremanagement.executor.plugin.PluginStateNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class HWPluginStateNotification extends PluginStateNotification {

	private static Logger logger = LoggerFactory.getLogger(HWPluginStateNotification.class);
	
	public HWPluginStateNotification(Map<String, String> inputs){
		super(inputs);
		logger.debug("{} instantiated with provide inputs {}", HWPluginStateNotification.class, inputs);
	}
	
	@Override
	public void pluginStateEvolution(PluginStateEvolution pluginStateEvolution, Exception e)
			throws Exception {
		logger.debug("New PluginStateEvolution : {}. Provided inputs was {}. {}", pluginStateEvolution, inputs, e==null?"":e.getStackTrace());
	}

}

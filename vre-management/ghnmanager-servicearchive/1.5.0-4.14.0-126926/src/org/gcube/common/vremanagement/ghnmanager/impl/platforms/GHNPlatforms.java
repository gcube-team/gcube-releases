package org.gcube.common.vremanagement.ghnmanager.impl.platforms;

import java.util.HashMap;
import java.util.Map;

import org.gcube.vremanagement.virtualplatform.image.PlatformConfiguration;
import org.gcube.vremanagement.virtualplatform.image.VirtualPlatform;

/**
 * 
 * Cache for local {@link VirtualPlatform}
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class GHNPlatforms {

	private static Map<String, VirtualPlatform> vplatforms = new HashMap<String, VirtualPlatform>();

	private GHNPlatforms() {}
	
	/**
	 * Gets the platform for the given configuration
	 * @param config the platform configuration
	 * @return the platform
	 * @throws Exception if the initialization of the platform fails or it does not exist
	 */
	public static VirtualPlatform get(PlatformConfiguration config) throws Exception {
		String key = buildKey(config);
		if (!vplatforms.containsKey(key))
				vplatforms.put(key, new VirtualPlatform(config));
		return vplatforms.get(key);
	}
	
	private static String buildKey(PlatformConfiguration config) {
		return config.getName() + config.getVersion() + config.getMinorVersion();
	}
}

package org.gcube.common.vremanagement.ghnmanager.impl.platforms;

import java.io.File;
import java.util.List;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.common.PlatformDescription;
import org.gcube.vremanagement.virtualplatform.image.PlatformConfiguration;
import org.gcube.vremanagement.virtualplatform.image.Platforms;
import org.gcube.vremanagement.virtualplatform.image.VirtualPlatform;


/**
 * Search for locally available virtual platforms
 * @author Manuele Simi (ISTI-CNR)
 */
public class Finder {
	
	private static List<PlatformConfiguration> availablePlatforms;
	
	/**
	 * Checks if the platform is locally available
	 * @param platformDescription the platform configuration to look for
	 * @return the platform found, if any
	 * @throws Exception 
	 */
	public static VirtualPlatform find(PlatformDescription platformDescription) throws Exception {
		//looks for the available virtual platforms
		availablePlatforms = Platforms.listAvailablePlatforms(new File(GHNContext.getContext().getVirtualPlatformsLocation()));
		
		VirtualPlatform vplatform = null;
		for (PlatformConfiguration configuration : availablePlatforms) {
			if (configuration.getName().equalsIgnoreCase(platformDescription.getName())
				&& (configuration.getVersion() == platformDescription.getVersion())) {
				vplatform =  GHNPlatforms.get(configuration);//new VirtualPlatform(configuration);
				break;
			}
		}
		if (vplatform == null) 
			throw new PlatformNotAvailableException("Unable to find platform " + platformDescription + " on this node");
		
		return vplatform;
	}
	
	/**
	 * Specialized exception for virtual platform unavailability
	 * @author Manuele Simi (ISTI-CNR)
	 *
	 */
	public static class PlatformNotAvailableException extends Exception {
		private static final long serialVersionUID = 6692317497612642118L;
		protected PlatformNotAvailableException() {super();}
		protected PlatformNotAvailableException(String msg) {super(msg);}
	}
}

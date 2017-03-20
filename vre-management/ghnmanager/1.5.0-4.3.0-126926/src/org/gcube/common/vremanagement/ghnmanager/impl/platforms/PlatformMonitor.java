package org.gcube.common.vremanagement.ghnmanager.impl.platforms;

import java.io.File;
import java.util.List;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.common.PlatformDescription;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.virtualplatform.image.PlatformConfiguration;
import org.gcube.vremanagement.virtualplatform.image.Platforms;
import org.gcube.vremanagement.virtualplatform.image.VirtualPlatform;

public class PlatformMonitor implements Runnable{

	protected GCUBELog logger = new GCUBELog(PlatformMonitor.class);
	private boolean firstExecution = true;
	
	public PlatformMonitor() {}
	
	@Override
	public void run() {
		List<PlatformDescription> platforms = GHNContext.getContext().getGHN().getNodeDescription().getAvailablePlatforms();
		synchronized (platforms) {
			logger.trace("Checking the available platforms");
			platforms.clear();
			List<PlatformConfiguration> configurations = Platforms.listAvailablePlatforms(new File(GHNContext.getContext().getVirtualPlatformsLocation()));
			for (PlatformConfiguration config : configurations){
				logger.trace("Found configuration " + config.getName());
				try {
					VirtualPlatform vp = GHNPlatforms.get(config);
					if (!vp.isAvailable()) //try to initialize
						new PlatformCall(GHNPlatforms.get(config)).initialize();	
					else {
						if (this.firstExecution)
							new PlatformCall(GHNPlatforms.get(config)).activateAllInstances();
					}
					if (vp.isAvailable()) {
						PlatformDescription pd = new PlatformDescription();
						pd.setName(config.getName());
						pd.setMinorVersion(config.getMinorVersion());
						pd.setVersion(config.getVersion());
						platforms.add(pd);
						logger.trace("Platform " + config.getName() + " is available");
					} else {
						logger.warn("Platform " + config.getName() + " is not available");
						new PlatformCall(GHNPlatforms.get(config)).shutdown();	
					}

				} catch (Exception e) {
					logger.warn("Platform " + config.getName() + " is not available" ,e);
				}
			}
		}
		this.firstExecution  = false;
	}
	
}

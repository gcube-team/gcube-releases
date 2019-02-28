package org.gcube.common.core.monitoring;

/**
 *  Base class for Notification Probes
 *  
 * @author Andrea Manzi(CERN) 
 *
 */
public abstract class GCUBENotificationProbe  extends GCUBETestProbe {

	 @Override
	 public void execute() throws Exception {
			this.run();
		}
}

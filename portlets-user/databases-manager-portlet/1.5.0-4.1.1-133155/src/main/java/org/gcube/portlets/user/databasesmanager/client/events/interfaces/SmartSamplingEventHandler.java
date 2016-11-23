package org.gcube.portlets.user.databasesmanager.client.events.interfaces;

import org.gcube.portlets.user.databasesmanager.client.events.SmartSamplingEvent;
import com.google.gwt.event.shared.EventHandler;

public interface SmartSamplingEventHandler extends EventHandler {
	public void onSmartSampling(SmartSamplingEvent smartSamplingEvent);
}

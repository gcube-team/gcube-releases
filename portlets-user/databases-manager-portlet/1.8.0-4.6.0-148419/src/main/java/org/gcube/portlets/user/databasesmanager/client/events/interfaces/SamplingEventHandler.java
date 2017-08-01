package org.gcube.portlets.user.databasesmanager.client.events.interfaces;

import org.gcube.portlets.user.databasesmanager.client.events.SamplingEvent;
import com.google.gwt.event.shared.EventHandler;

public interface SamplingEventHandler extends EventHandler {
	public void onSampling(SamplingEvent samplingEvent);
}

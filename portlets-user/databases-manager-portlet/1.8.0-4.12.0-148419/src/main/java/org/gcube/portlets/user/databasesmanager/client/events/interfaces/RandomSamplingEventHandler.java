package org.gcube.portlets.user.databasesmanager.client.events.interfaces;

import org.gcube.portlets.user.databasesmanager.client.events.RandomSamplingEvent;
import com.google.gwt.event.shared.EventHandler;

public interface RandomSamplingEventHandler extends EventHandler {
	public void onRandomSampling(RandomSamplingEvent randomSamplingEvent);
}

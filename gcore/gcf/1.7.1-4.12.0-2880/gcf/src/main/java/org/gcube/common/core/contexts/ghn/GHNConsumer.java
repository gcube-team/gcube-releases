package org.gcube.common.core.contexts.ghn;

import org.gcube.common.core.contexts.ghn.Events.GHNEvent;
import org.gcube.common.core.contexts.ghn.Events.GHNLifeTimeEvent;
import org.gcube.common.core.contexts.ghn.Events.GHNRIRegistrationEvent;
import org.gcube.common.core.contexts.ghn.Events.GHNTopic;
import org.gcube.common.core.utils.events.GCUBEConsumer;
import org.gcube.common.core.utils.events.GCUBEEvent;

/** Base implementation of a {@link GCUBEConsumer} of {@link GHNEvent GHNEvents}.
 * @author Fabio Simeoni (University of Strathclyde)*/
public class GHNConsumer implements GCUBEConsumer<GHNTopic,Object> {

	/**Receives RI lifetime events and dispatches them to topic-specific callbacks.*/
	public <T1 extends GHNTopic, P1 extends Object> void onEvent(GCUBEEvent<T1, P1>... events) {
		if (events==null) return;
		for (GCUBEEvent<T1,P1> event : events) {
			GHNTopic topic = event.getTopic();
			switch (topic) {
				case RIREGISTRATION : this.onRIRegistration((GHNRIRegistrationEvent)event);break;
				case UPDATE : this.onGHNUpdated((GHNLifeTimeEvent)event);break;
				case SHUTDOWN : this.onGHNShutdown((GHNLifeTimeEvent)event);break;
				case READY : this.onGHNReady((GHNLifeTimeEvent)event);break;
			}
		}			
	}

	/**RIRegistration event callback.
	 * @param event the event.*/
	synchronized protected void onRIRegistration(GHNRIRegistrationEvent event) {}
	/**Update event callback.
	 * @param event the event.*/
	synchronized protected void onGHNUpdated(GHNLifeTimeEvent event) {}
	/**Shutdown event callback.
	 * @param event the event.*/
	synchronized protected void onGHNShutdown(GHNLifeTimeEvent event) {}
	/**Ready event callback.
	 * @param event the event.*/
	synchronized protected void onGHNReady(GHNLifeTimeEvent event) {}

}

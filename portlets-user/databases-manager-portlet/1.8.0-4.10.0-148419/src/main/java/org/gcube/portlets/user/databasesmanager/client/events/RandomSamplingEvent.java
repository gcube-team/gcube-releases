package org.gcube.portlets.user.databasesmanager.client.events;

import org.gcube.portlets.user.databasesmanager.client.events.interfaces.RandomSamplingEventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class RandomSamplingEvent extends GwtEvent<RandomSamplingEventHandler> {

	public static Type<RandomSamplingEventHandler> TYPE = new Type<RandomSamplingEventHandler>();

	@Override
	protected void dispatch(RandomSamplingEventHandler handler) {
		handler.onRandomSampling(this);
	}

	@Override
	public Type<RandomSamplingEventHandler> getAssociatedType() {
		return TYPE;
	}

	public EventsTypeEnum getKey() {
		return EventsTypeEnum.RANDOM_SAMPLING_EVENT;
	}
}

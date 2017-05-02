package org.gcube.portlets.user.databasesmanager.client.events;

import org.gcube.portlets.user.databasesmanager.client.events.interfaces.SamplingEventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class SamplingEvent extends GwtEvent<SamplingEventHandler> {

	public static Type<SamplingEventHandler> TYPE = new Type<SamplingEventHandler>();

	@Override
	public Type<SamplingEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SamplingEventHandler handler) {
		handler.onSampling(this);
	}

	public EventsTypeEnum getKey() {
		return EventsTypeEnum.SAMPLING_EVENT;
	}
}

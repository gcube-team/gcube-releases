package org.gcube.portlets.user.databasesmanager.client.events;

import org.gcube.portlets.user.databasesmanager.client.events.interfaces.SmartSamplingEventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class SmartSamplingEvent extends GwtEvent<SmartSamplingEventHandler> {

	public static Type<SmartSamplingEventHandler> TYPE = new Type<SmartSamplingEventHandler>();

	@Override
	protected void dispatch(SmartSamplingEventHandler handler) {
		handler.onSmartSampling(this);
	}

	@Override
	public Type<SmartSamplingEventHandler> getAssociatedType() {
		return TYPE;
	}

	public EventsTypeEnum getKey() {
		return EventsTypeEnum.SMART_SAMPLING_EVENT;
	}
}

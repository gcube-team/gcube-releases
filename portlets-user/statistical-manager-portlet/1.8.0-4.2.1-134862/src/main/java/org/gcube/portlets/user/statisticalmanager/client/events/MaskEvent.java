package org.gcube.portlets.user.statisticalmanager.client.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.EventHandler;
import java.lang.String;
import com.google.gwt.event.shared.HasHandlers;

public class MaskEvent extends GwtEvent<MaskEvent.MaskHandler> {

	public static Type<MaskHandler> TYPE = new Type<MaskHandler>();
	private String message;

	public interface MaskHandler extends EventHandler {
		void onMask(MaskEvent event);
	}

	public MaskEvent(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override
	protected void dispatch(MaskHandler handler) {
		handler.onMask(this);
	}

	@Override
	public Type<MaskHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<MaskHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, String message) {
		source.fireEvent(new MaskEvent(message));
	}
}
